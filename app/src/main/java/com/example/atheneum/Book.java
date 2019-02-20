package com.example.atheneum;

import java.util.ArrayList;
import java.util.UUID;

public class Book {
    private long isbn;
    private String title;
    private String description;
    private String author;
    private User owner;
    private User borrower;
    private Status status;
    private ArrayList<Request> requests; //Request represents our Request class. NOT java's!
    private UUID bookID;
    private ArrayList<String> photos;

    public enum Status {
        AVAILABLE, REQUESTED, ACCEPTED, BORROWED
    }

    public Book() {
        this.bookID = UUID.randomUUID();
    }

    public long getIsbn() {
        return isbn;
    }

    public void setIsbn(long isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getBorrower() {
        return borrower;
    }

    public void setBorrower(User borrower) {
        this.borrower = borrower;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ArrayList<Request> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<Request> requests) {
        this.requests = requests;
    }

    public void addRequest(Request request) {
        this.requests.add(request);
    }

    public void deleteRequest(Request request) {
        this.requests.remove(request);
    }

    public void deleteRequests() {
        this.requests.clear();
    }

    public UUID getBookID() {
        return bookID;
    }

    public ArrayList<String> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<String> photos) {
        this.photos = photos;
    }

    public void addPhoto(String photo) {
        this.photos.add(photo);
    }

    public void deletePhoto(String photo) {
        this.photos.remove(photo);
    }

    public void deletePhotos() {
        this.photos.clear();
    }
}
