package com.example.atheneum;

import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import static org.junit.Assert.*;

public class BookTest {
    @Test
    public void GetIsbn() {
        Book book = new Book();
        Long bi = Long.valueOf(1234);

        book.setIsbn(bi);
        assertEquals(book.getIsbn(), bi);

        bi = 1234567890123L;
        book.setIsbn(bi);
        assertEquals(book.getIsbn(), bi);

        bi = 9999999999999L;
        book.setIsbn(bi);
        assertEquals(book.getIsbn(), bi);

        bi = -1L;
        book.setIsbn(bi);
        assertEquals(book.getIsbn(), bi);
    }
}
