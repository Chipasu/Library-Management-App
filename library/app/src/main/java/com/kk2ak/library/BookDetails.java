package com.kk2ak.library;

public class BookDetails {
    public String bookid;
    public String genre;
    public String name;
    public String author;
    public String imageURL;

    BookDetails(String name, String genre, String author, String imageURL, String bookid) {
        this.bookid = bookid;
        this.name = name;
        this.genre = genre;
        this.author = author;
        this.imageURL = imageURL;
    }
}
