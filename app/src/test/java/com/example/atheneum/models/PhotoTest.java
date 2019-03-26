package com.example.atheneum.models;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PhotoTest {

    private static final String ORIGINAL_PHOTO_ID = "1324134ufufajdf39413";
    private static final String NEW_PHOTO_ID = "8934cczxjvhvj1234safafh";
    private static final String ORIGINAL_ENCODED_STRING = "0x324fjjfuehrhfh247141";
    private static final String NEW_ENCODED_STRING = "0x44sdfjggg344141kfgfhgfdhffg";

    private Photo photo;

    @Before
    public void setUp() throws Exception {
        photo = new Photo(ORIGINAL_PHOTO_ID, ORIGINAL_ENCODED_STRING);
    }

    @Test
    public void getPhotoID() {
        assertEquals(photo.getPhotoID(), ORIGINAL_PHOTO_ID);
    }

    @Test
    public void setPhotoID() {
        photo.setPhotoID(NEW_PHOTO_ID);
        assertEquals(photo.getPhotoID(), NEW_PHOTO_ID);
        photo.setPhotoID(ORIGINAL_PHOTO_ID);
        assertEquals(photo.getPhotoID(), ORIGINAL_PHOTO_ID);
    }

    @Test
    public void getEncodedString() {
        assertEquals(photo.getEncodedString(), ORIGINAL_ENCODED_STRING);
    }

    @Test
    public void setEncodedString() {
        photo.setEncodedString(NEW_ENCODED_STRING);
        assertEquals(photo.getEncodedString(), NEW_ENCODED_STRING);
        photo.setEncodedString(ORIGINAL_ENCODED_STRING);
        assertEquals(photo.getEncodedString(), ORIGINAL_ENCODED_STRING);
    }
}