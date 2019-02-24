package com.example.atheneum;

import java.util.ArrayList;

public class OwnerCollection {
    private User owner;
    private ArrayList<Book> ownList;


    public OwnerCollection(){
        this.owner = new User();
        this.ownList = new ArrayList<Book>();
    }


    public OwnerCollection(User owner){
        this.owner = owner;
        this.ownList = new ArrayList<Book>();
    }

    public void setOwner(User owner){this.owner = owner;}

    public void setOwnList(ArrayList<Book> ownList){this.ownList = ownList;}

    public User getOwner(){return this.owner;}

    public ArrayList<Book> getOwnList(){return this.ownList;}

    public void addBook(Book book){this.ownList.add(book);}

    public void deleteBook(Book book){this.ownList.remove(book);}

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
