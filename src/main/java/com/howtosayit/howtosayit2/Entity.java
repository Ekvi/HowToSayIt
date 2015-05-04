package com.howtosayit.howtosayit2;


public class Entity {
    private int id;
    private String rus;
    private String eng;
    private int number;
    private String lesson;
    private int start;
    private int stop;

    public Entity(int id, String rus, String eng, int number, String lesson, int start, int stop) {
        this.id = id;
        this.rus = rus;
        this.eng = eng;
        this.number = number;
        this.lesson = lesson;
        this.start = start;
        this.stop = stop;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRus() {
        return rus;
    }

    public void setRus(String rus) {
        this.rus = rus;
    }

    public String getEng() {
        return eng;
    }

    public void setEng(String eng) {
        this.eng = eng;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStop() {
        return stop;
    }

    public void setStop(int stop) {
        this.stop = stop;
    }
}
