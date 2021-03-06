/*
 * Copyright <YEAR> <COPYRIGHT HOLDER>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.example.atheneum.viewmodels;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import com.example.atheneum.models.Book;
import com.example.atheneum.models.Transaction;
import com.example.atheneum.utils.FirebaseQueryLiveData;
import com.example.atheneum.viewmodels.FirebaseRefUtils.DatabaseWriteHelper;
import com.example.atheneum.viewmodels.FirebaseRefUtils.TransactionRefUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

/**
 * The type Transaction view model.
 * handles writing/deleting transaction data to/from firebase
 */
public class TransactionViewModel extends ViewModel {
    private static final String TAG = TransactionViewModel.class.getSimpleName();

    private final FirebaseQueryLiveData queryLiveData;

    private final LiveData<Transaction> transactionLiveData;

    private final DatabaseReference transactionRef;

    private final String bookID;

    /**
     * Instantiates a new Transaction view model.
     *
     * @param bookID the book id
     */
    public TransactionViewModel(String bookID){
        this.bookID = bookID;

        transactionRef = TransactionRefUtils.getTransactionRef(bookID);
        queryLiveData = new FirebaseQueryLiveData(transactionRef);
        transactionLiveData = Transformations.map(queryLiveData, new Deserializer());
    }

    private class Deserializer implements Function<DataSnapshot, Transaction> {
        @Override
        public Transaction apply(DataSnapshot dataSnapshot) {
            return dataSnapshot.getValue(Transaction.class);
        }
    }

    /**
     * Get transaction live data live data.
     *
     * @return the live data
     */
    @NonNull
    public LiveData<Transaction> getTransactionLiveData(){return transactionLiveData;}


    /**
     * Update transaction.
     *
     * @param transaction the transaction
     */
    public void updateTransaction(Transaction transaction){
        DatabaseWriteHelper.updateTransaction(transaction);
    }


    /**
     * Update transaction borrowed.
     *
     * @param book        the book
     * @param transaction the transaction
     */
    public void updateTransactionBorrowed(Book book, Transaction transaction){
        DatabaseWriteHelper.updateTransactionBookBorrow(book, transaction);
    }

    /**
     * Update transaction returned.
     *
     * @param book the book
     */
    public void updateTransactionReturned(Book book){
        DatabaseWriteHelper.updateTransactionBookReturn(book);
    }

}
