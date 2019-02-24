package com.example.atheneum;

import java.util.ArrayList;

public class OwnerCollection {
    private User owner;
    private ArrayList<Book> ownList;

    /**
     * blank constructor
     */
    public OwnerCollection(){
        this.owner = new User();
        this.ownList = new ArrayList<Book>();
    }

    /**
     * constructor with specified attributes
     * @param owner
     */
    public OwnerCollection(User owner){
        this.owner = owner;
        this.ownList = new ArrayList<Book>();
    }

    /**
     *
     * @return owner of this collection
     */
    public User getOwner(){return this.owner;}

    /**
     *
     * @return collection of the owning book
     */
    public ArrayList<Book> getOwnList(){return this.ownList;}

    /**
     * add new owning book
     * @param book
     */
    public void addBook(Book book){this.ownList.add(book);}

    /**
     * delete a owning book
     * @param book
     */
    public void deleteBook(Book book){this.ownList.remove(book);}

    /**
     * filter out a book list by status
     * @param status
     * @return a book list with specified status
     */
    public ArrayList<Book> filterBooks(Book.Status status){
        ArrayList<Book> filteredList = new ArrayList<Book>();

        for(Book b: ownList){
            if(b.getStatus().equals(status)){
                filteredList.add(b);
            }
        }
        return filteredList;

    }
}
