package com.howtosayit.howtosayit2.models;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Lesson implements Parcelable {
    private String name;
    private List<Phrase> phrases = new ArrayList<>();

    public Lesson(){}

    public Lesson(String name, List<Phrase> phrases) {
        this.name = name;
        this.phrases = phrases;
    }

    public List<Phrase> getPhrases() {
        return phrases;
    }

    public void setPhrases(List<Phrase> entityList) {
        this.phrases = entityList;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeList(phrases);
    }

    public static final Parcelable.Creator<Lesson> CREATOR = new Parcelable.Creator<Lesson>() {
        public Lesson createFromParcel(Parcel parcel) {
            return new Lesson(parcel);
        }

        public Lesson[] newArray(int size) {
            return new Lesson[size];
        }
    };

    public Lesson(Parcel parcel) {
        name = parcel.readString();
        phrases = parcel.readArrayList(Phrase.class.getClassLoader());
    }
}
