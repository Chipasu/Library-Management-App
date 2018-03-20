package com.kk2ak.library;


import java.util.Random;

public class BookDetails {
    public String genre;
    public String name;
    private String[] genreList={"Science fiction","Romance","Mystery"};
    BookDetails(String name) {
        this.name=name;
        this.genre=genreList[new Random().nextInt(genreList.length)];
    }
}
