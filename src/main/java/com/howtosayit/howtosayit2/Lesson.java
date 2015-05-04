package com.howtosayit.howtosayit2;


import java.util.ArrayList;
import java.util.List;

public class Lesson {
    private List<Phrase> phrases = new ArrayList<>();

    public List<Phrase> getPhrases() {
        return phrases;
    }

    public void setPhrases(List<Phrase> entityList) {
        this.phrases = entityList;
    }
}
