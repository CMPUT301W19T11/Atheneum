/*
 * Copyright <YEAR> <COPYRIGHT HOLDER>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.example.atheneum.viewmodels.FirebaseRefUtils;

import com.example.atheneum.models.Book;
import com.example.atheneum.models.Transaction;
import com.google.firebase.database.DatabaseReference;


/**
 * The type Transaction ref utils.
 * utility class for getting firebase references to transactions
 */
public class TransactionRefUtils extends RootRefUtils{
    /**
     * The constant TRANSACTION_REF.
     */
    public static final DatabaseReference TRANSACTION_REF = ROOT_REF.child("transactions");

    /**
     * Get transaction ref database reference.
     *
     * @param transaction the transaction
     * @return the database reference
     */
    public static final DatabaseReference getTransactionRef(Transaction transaction){
        return TRANSACTION_REF.child(transaction.getBookID());
    }

    /**
     * Get transaction ref database reference.
     *
     * @param bookID the book id
     * @return the database reference
     */
    public static DatabaseReference getTransactionRef(String bookID){
        return TRANSACTION_REF.child(bookID);
    }
}
