package com.example.atheneum;

import java.util.ArrayList;

/**
 * The type Owner collection.
 * A wrapper around an ArrayList of Books to hold all the books owned by a given user
 */
public class OwnerCollection {
    private User owner;
    private ArrayList<Book> ownList;

    /**
     * Instantiates a new Owner collection, with no books initially added.
     * This should be the constructor used any time a new User is created, as by default, a new
     * user will not have any owned books.
     *
     * @param owner the owner
     */
    public OwnerCollection(User owner) {
        this.owner = owner;
        this.ownList = new ArrayList<Book>();
    }

    /**
     * Instantiates a new Owner collection, with an intial list of owned books. Mostly for
     * testing purposes, this is not a realistic constructor to be used when creating a new User.
     *
     * @param owner    the owner
     * @param bookList the book list
     */
    public OwnerCollection(User owner, ArrayList<Book> bookList) {
        this.owner = owner;
        this.ownList = new ArrayList<Book>();
        this.ownList.addAll(bookList);
    }

    /**
     * Gets owner.
     *
     * @return the owner
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Sets owner.
     *
     * @param owner the owner
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }

    /**
     * Gets the list of owned books from the OwnerCollection.
     *
     * @return the owned list
     */
    public ArrayList<Book> getOwnList() {
        return ownList;
    }

    /**
     * Sets the list of owned books in the collection all at once. Mostly for testing purposes.
     * In general, for app use books will only be added or removed one at at time.
     *
     * @param ownList the list of books to which the OwnerCollection's ownList will be set
     */
    public void setOwnList(ArrayList<Book> ownList) {
        this.ownList = ownList;
    }

    /**
     * Add book to the OwnerCollection
     *
     * @param book the book to be added
     */
    public void addBook(Book book) {
        this.ownList.add(book);
    }

    /**
     * Delete a book from the list.
     *
     * @param book the book to be removed
     */
    public void deleteBook(Book book) {
        this.ownList.remove(book);
    }

    /**
     * Filter books to return a list of all books with a given status.
     *
     * @param filter the status on which to filter
     * @return the array list containing books in the OwnerCollections's ownList that have the status specified by filter
     */
    public ArrayList<Book> filterBooks(Book.Status filter) {
        // TODO: METHOD STUB FOR filter method to be added later
        return new ArrayList<Book>();
    }
}
