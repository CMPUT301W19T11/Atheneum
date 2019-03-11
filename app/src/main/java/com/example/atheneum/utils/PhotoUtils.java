package com.example.atheneum.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Does encoding and decoding for photo bitmaps
 */
public class PhotoUtils {
    /**
     * Encodes a photo represented as a bitmap into a Base64 encoded string
     *
     * @param bitmapPhoto Photo as a bitmap
     * @return Base64 encoded string representing photo
     */
    public static String EncodeBitmapPhotoBase64(Bitmap bitmapPhoto) {
        // See: https://stackoverflow.com/a/9224180/11039833
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmapPhoto.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * Decodes a Base64 encoded string into a bitmap photo
     *
     * @param encodedString
     * @return Photo represented as a Bitmap
     */
    public static Bitmap DecodeBase64BitmapPhoto(String encodedString) {
        // See: https://stackoverflow.com/a/4837293/11039833
        byte[] decodedString = Base64.decode(encodedString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
