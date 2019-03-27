package com.example.atheneum.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

/**
 * Used to represent a Photo that is stored in Firebase along with the valid operations on a Photo.
 */
public class Photo {
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

    private String photoID = "";
    private String encodedString = "";

    /**
     * Needed for Firebase. Do not use in application code.
     */
    public Photo() {

    }

    /**
     * Creates an new instance of Photo using an encodedString.
     *
     * @param photoID  Unique identifier of Photo.
     * @param encodedString Base64 encoding of the Photo.
     */
    public Photo(String photoID, String encodedString) {
        this.photoID = photoID;
        this.encodedString = encodedString;
    }

    /**
     * Creates an new instance of Photo using a Bitmap. Probably the most common way to construct
     * a Photo since Photos are sent as a bitmap by the PhotoController.
     *
     * @param photoID Unique identifier of Photo.
     * @param bitmap Raw bitmap of the Photo.
     */
    public Photo(String photoID, Bitmap bitmap) {
        this.photoID = photoID;
        this.encodedString = EncodeBitmapPhotoBase64(bitmap);
    }

    /**
     * @return Unique identifier of Photo.
     */
    public String getPhotoID() {
        return photoID;
    }

    /**
     * Updates the photoID.
     *
     * Note: When storing a list of photos in Firebase, push keys should be used as the photoID
     *       since push keys use the current timestamp to generate the key so that the data is
     *       ordered when pushed to the server. Push keys are collision resistant when multiple clients
     *       update the server at the same time.
     *
     * @param photoID Unique identifier of Photo.
     */
    public void setPhotoID(String photoID) {
        this.photoID = photoID;
    }

    /**
     * @return Base64 encoding of the Photo.
     */
    public String getEncodedString() {
        return encodedString;
    }

    /**
     * Updates the Photo by changing the Base64 encoding.
     *
     * @param encodedString Base64 encoding of the Photo.
     */
    public void setEncodedString(String encodedString) {
        this.encodedString = encodedString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Photo)) return false;
        Photo photo = (Photo) o;
        return Objects.equals(getPhotoID(), photo.getPhotoID()) &&
                Objects.equals(getEncodedString(), photo.getEncodedString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPhotoID(), getEncodedString());
    }

    @Override
    public String toString() {
        return "Photo{" +
                "photoID='" + photoID + '\'' +
                ", encodedString='" + encodedString + '\'' +
                '}';
    }
}
