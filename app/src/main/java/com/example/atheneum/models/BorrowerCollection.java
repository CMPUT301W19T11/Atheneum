/*
 * Copyright <YEAR> <COPYRIGHT HOLDER>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.example.atheneum.models;

import java.util.ArrayList;

public class BorrowerCollection {
    private User borrower;
    private ArrayList<Book> borrowList;

    /**
     * Instantiates a new BorrowerCollection.
     *
     */
    public BorrowerCollection(){
        this.borrower = new User();
        this.borrowList = new ArrayList<>();
    }


    /**
     * Instantiates a new BorrowerCollection.
     *
     * @param borrower the username of the borrower
     */
    public BorrowerCollection(User borrower){
        this.borrower = borrower;
        this.borrowList = new ArrayList<Book>();
    }


    /**
     * Gets the User borrower.
     *
     * @return the borrower's username
     */
    public User getBorrower(){return this.borrower;}


    /**
     * Sets the User borrower.
     *
     * @param borrower username of borrower
     */
    public void setBorrower(User borrower){this.borrower = borrower;}

    /**
     * Gets the list of borrowed books.
     *
     * @return borrowList
     */
    public ArrayList<Book> getBorrowList(){return this.borrowList;}

    /**
     * Sets the borrow list.
     *
     * @param list the arraylist
     */
    public void setBorrowList(ArrayList<Book> list){this.borrowList = list;}

    /**
     * Adds a book to the borrowList.
     *
     * @param book the book to be added
     */
    public void addBook(Book book){
        this.borrowList.add(book);
    }


}
