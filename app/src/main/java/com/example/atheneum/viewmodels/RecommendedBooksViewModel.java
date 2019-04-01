package com.example.atheneum.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.Log;

import com.example.atheneum.models.Book;
import com.example.atheneum.utils.FirebaseQueryLiveData;
import com.example.atheneum.viewmodels.FirebaseRefUtils.BooksRefUtils;
import com.example.atheneum.viewmodels.FirebaseRefUtils.BorrowedBooksHistoryRefUtils;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Abstracts operations on a list of recommended books retrieved from a Firebase query and provides
 * a LiveData stream to a View (either an Activity or Fragment). This livedata stream can be observed
 * for changes by adding an observer and then updating the view based on the changes to the data.
 * The list of recommended books queried by Firebase is found by matching books containing isbns
 * that users who have the isbn of the book the user just returned in their history.
 */
public class RecommendedBooksViewModel extends ViewModel {
    private static final String TAG = RecommendedBooksViewModel.class.getSimpleName();

    // Purely used internally in this class, never returned to the view
    private final FirebaseQueryLiveData recommendedIsbnsQueryLiveData;
    private final LiveData<List<Long>> recommendedIsbnsLiveData;
    private final FirebaseQueryLiveData booksQueryLiveData;
    private final LiveData<List<Book>> booksLiveData;
    private final MediatorLiveData<Pair<List<Long>, List<Book>>> pairMediatorLiveData;

    // Final result that is returned to the view
    private final LiveData<List<Book>> recommendedBooksLiveData;

    /**
     * Sorts a collection of isbns based on a map of isbn to it's frequency in the user history table.
     * Collection entries are sorted in descending order based on the frequency of the isbn
     * in the map. Ties are broken based on the isbn.
     */
    private class SortComparator implements Comparator<Long> {
        private final HashMap<Long, Integer> isbnFrequencyMap;

        /**
         * Instantiates a new Sort comparator.
         *
         * @param isbnFrequencyMap the isbn frequency map
         */
        public SortComparator(HashMap<Long, Integer> isbnFrequencyMap) {
            this.isbnFrequencyMap = isbnFrequencyMap;
        }

        @Override
        public int compare(Long isbn, Long otherIsbn) {
            // See: https://www.geeksforgeeks.org/sort-elements-by-frequency-set-5-using-java-map/
            // Compare frequency in this order to sort in descending order
            int frequencyCompare = isbnFrequencyMap.get(otherIsbn).compareTo(isbnFrequencyMap.get(isbn));
            int isbnCompare = isbn.compareTo(otherIsbn);
            // If frequencies are the same, break ties using isbn
            if (frequencyCompare == 0) {
                return isbnCompare;
            }
            return frequencyCompare;
        }
    }

    /**
     * Instantiate a new instance of RecommendedBooksViewModel.
     *
     * @param borrowerID UserID of the user who just returned the book with the isbn passed into this                   constructor.
     * @param isbn       ISBN of the book that the user just returned.
     */
    public RecommendedBooksViewModel(final String borrowerID, final Long isbn) {
        if (isbn != Book.INVALILD_ISBN) {
            // If the isbn passed in is valid, only search the history of users who borrowed that isbn
            recommendedIsbnsQueryLiveData = new FirebaseQueryLiveData(BorrowedBooksHistoryRefUtils.getUserHistoriesContainingIsbn(isbn));
        } else {
            // Otherwise, search the history of all users
            recommendedIsbnsQueryLiveData = new FirebaseQueryLiveData(BorrowedBooksHistoryRefUtils.BORROWED_BOOKS_HISTORY_REF);
        }
        recommendedIsbnsLiveData = Transformations.map(recommendedIsbnsQueryLiveData, new android.arch.core.util.Function<DataSnapshot, List<Long>>() {
            /**
             * Whenever new isbn's are added to user histories which contain the isbn of the book
             * we just returned (aka the one passed into the view model), convert the Datasnapshot
             * into a list of isbns to recommend to the user. Generate the list of recommended isbns
             * by finding isbns that the user hasn't read yet and recommended them in order of the
             * frequency at which they appear in the histories of all other users who read the book
             * the user just returned. This function is applied whenever the value of
             * recommendedIsbnsQueryLiveData changes and updates the value of recommendedIsbnsLiveData.
             *
             * @param dataSnapshot Raw value from query
             * @return List of isbns to recommend to user.
             */
            @Override
            public List<Long> apply(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    HashMap<Long, Integer> isbnFrequencyMap = new HashMap<>();
                    ArrayList<Long> isbnsUserHasRead = new ArrayList<>();

                    // Each user in the history table has their userID as a key and a list of
                    // 'borrowedIsbn:true' pairs as a value where borrowedIsbn is an isbn they borrowed
                    // represented as a string (since it's a key). The borrowedIsbn has a value of
                    // true since Firebase needs a truthy value (aka not null) to store this relation.
                    for (DataSnapshot userIsbnList : dataSnapshot.getChildren()) {
                        if (userIsbnList.getKey().equals(borrowerID)) {
                            // When presented with the history of borrowerID, record the books they've
                            // read.
                            for (DataSnapshot isbnSnapshot : userIsbnList.getChildren()) {
                                Long isbn = Long.valueOf(isbnSnapshot.getKey());
                                isbnsUserHasRead.add(isbn);
                            }
                        } else {
                            // Otherwise, update the frequency map
                            for (DataSnapshot isbnSnapshot : userIsbnList.getChildren()) {
                                Long isbn = Long.valueOf(isbnSnapshot.getKey());
                                if (isbnFrequencyMap.containsKey(isbn)) {
                                    isbnFrequencyMap.put(isbn, isbnFrequencyMap.get(isbn) + 1);
                                } else {
                                    isbnFrequencyMap.put(isbn, 1);
                                }
                            }
                        }
                    }

                    Log.i(TAG, "before filtering freq map:");
                    for (Map.Entry<Long, Integer> entry : isbnFrequencyMap.entrySet()) {
                        Log.i(TAG, String.format("recommendedIsbn: %d, frequency: %d", entry.getKey(), entry.getValue()));
                    }
                    // Don't recommend books we've already read
                    for (Long isbn : isbnsUserHasRead) {
                        isbnFrequencyMap.remove(isbn);
                    }
                    Log.i(TAG, "after filtering freq map:");
                    for (Map.Entry<Long, Integer> entry : isbnFrequencyMap.entrySet()) {
                        Log.i(TAG, String.format("recommendedIsbn: %d, frequency: %d", entry.getKey(), entry.getValue()));
                    }

                    // See: https://stackoverflow.com/questions/31820049/how-to-convert-hash-map-keys-into-list
                    ArrayList<Long> recommendedIsbns = new ArrayList<>(isbnFrequencyMap.keySet());
                    // Sort in-place based on frequency, using isbn to break ties
                    Collections.sort(recommendedIsbns, new RecommendedBooksViewModel.SortComparator(isbnFrequencyMap));
                    return recommendedIsbns;
                }
                return null;
            }
        });

        booksQueryLiveData = new FirebaseQueryLiveData(BooksRefUtils.BOOKS_REF);
        booksLiveData = Transformations.map(booksQueryLiveData, new android.arch.core.util.Function<DataSnapshot, List<Book>>() {
            /**
             * Whenever new books are added to the database, we find books that the user has not yet
             * borrowed and whose status is either available or requested. In addition, we filter
             * away books that contain the same isbn of a book the user owns. This function is applied
             * whenever the value of booksQueryLiveData changes and updates the value of booksLiveData.
             *
             * @param dataSnapshot Raw value of query
             * @return List of all books that the user doesn't own and that are either available or
             *         requested.
             */
            @Override
            public List<Book> apply(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Book> books = new ArrayList<>();
                    Set<Long> isbnsCurrentUserOwns = new HashSet<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Book book = data.getValue(Book.class);
                        if (book.getOwnerID().equals(borrowerID)) {
                            isbnsCurrentUserOwns.add(book.getIsbn());
                        } else if (!book.getOwnerID().equals(borrowerID)
                                && (book.getStatus().equals(Book.Status.AVAILABLE)
                                || book.getStatus().equals(Book.Status.REQUESTED))) {
                            books.add(book);
                        }
                    }

                    // Remove books containing isbns the user owns.
                    // This is to prevent recommending books that have the same isbn as a book that
                    // the user owns but with a different book id.
                    // Removing via the iterator does this in place.
                    Iterator<Book> booksIter = books.iterator();
                    while (booksIter.hasNext()) {
                        Book book = booksIter.next();
                        if (isbnsCurrentUserOwns.contains(book.getIsbn())) {
                            booksIter.remove();
                        }
                    }
                    return books;
                }
                return null;
            }
        });

        // Collect the values of recommendedIsbnsLiveData and booksLiveData
        // into one data structure whenever either of the two liveData changes.
        pairMediatorLiveData = new MediatorLiveData<>();
        // Initialize value of mediator to prevent NullPointerExceptions
        pairMediatorLiveData.setValue(new Pair<List<Long>, List<Book>>(null, null));
        pairMediatorLiveData.addSource(recommendedIsbnsLiveData, new Observer<List<Long>>() {
            @Override
            public void onChanged(@Nullable List<Long> recommendedIsbns) {
                Pair<List<Long>, List<Book>> currentPair = pairMediatorLiveData.getValue();
                pairMediatorLiveData.setValue(new Pair<>(recommendedIsbns, currentPair.second));
            }
        });
        pairMediatorLiveData.addSource(booksLiveData, new Observer<List<Book>>() {
            @Override
            public void onChanged(@Nullable List<Book> books) {
                Pair<List<Long>, List<Book>> currentPair = pairMediatorLiveData.getValue();
                pairMediatorLiveData.setValue(new Pair<>(currentPair.first, books));
            }
        });

        recommendedBooksLiveData = Transformations.map(pairMediatorLiveData, new android.arch.core.util.Function<Pair<List<Long>, List<Book>>, List<Book>>() {
            /**
             * Whenever pairMediatorLiveData changes, return a list of recommended books that will be
             * used to update recommendedBooksLiveData. The recommendation algorithm is based on
             * the intersection of the list of isbns and the list of books contained in
             * pairMediatorLiveData where books which contain an isbn in the list of isbns and which
             * can be borrowed (ie. they are not owned by the user we pass into the view model,
             * and their status is either available or requested and the books's isbn doesn't match
             * that of a book the user already owns).
             *
             * @param pair Pair containing the list of recommended isbns and list of books from database
             *             (pre-filtered).
             * @return List of recommended books.
             */
            @Override
            public List<Book> apply(Pair<List<Long>, List<Book>> pair) {
                List<Long> recommendedIsbns = pair.first;
                List<Book> books = pair.second;

                // Check cases of empty intersection
                if (recommendedIsbns == null
                        || recommendedIsbns.isEmpty()
                        || books == null
                        || books.isEmpty()) {
                    return null;
                }

                // For every recommended isbn, map all the books that match the isbn and that the
                // user can potentially request.
                HashMap<Long, List<Book>> recommendedIsbnBooksMap = new HashMap<>();
                for (long isbn : recommendedIsbns) {
                    recommendedIsbnBooksMap.put(isbn, new ArrayList<Book>());
                }
                for (Book book : books) {
                    if (recommendedIsbnBooksMap.containsKey(book.getIsbn())) {
                        recommendedIsbnBooksMap.get(book.getIsbn()).add(book);
                    }
                }

                // Collect each list of books corresponding to each isbn and flatten them
                // into the output list, making sure to match the order of recommendedIsbns
                // since recommendedIsbns is sorted based on which isbn the user is more likely
                // to read.
                List<Book> recommendedBooks = new ArrayList<>();
                for (long isbn : recommendedIsbns) {
                    recommendedBooks.addAll(recommendedIsbnBooksMap.get(isbn));
                }
                return recommendedBooks;
            }
        });

    }

    /**
     * Gets recommended books live data.
     *
     * @return Lifecycle -aware observable stream of {@code List<Book>} that the view can observe for changes.
     */
    public LiveData<List<Book>> getRecommendedBooksLiveData() {
        return recommendedBooksLiveData;
    }
}
