package com.howtosayit.howtosayit2.models;


import android.os.Parcel;
import android.os.Parcelable;

public class Phrase implements Parcelable {
    private int id;
    private String rus;
    private String eng;
    private int number;
    private String lesson;
    private int start;
    private int stop;

    public Phrase(int id, String rus, String eng, int number, String lesson, int start, int stop) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(rus);
        parcel.writeString(eng);
        parcel.writeInt(number);
        parcel.writeString(lesson);
        parcel.writeInt(start);
        parcel.writeInt(stop);
    }

    public static final Parcelable.Creator<Phrase> CREATOR = new Parcelable.Creator<Phrase>() {
        public Phrase createFromParcel(Parcel parcel) {
            return new Phrase(parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readString(), parcel.readInt(), parcel.readInt());
        }

        public Phrase[] newArray(int size) {
            return new Phrase[size];
        }
    };
}
