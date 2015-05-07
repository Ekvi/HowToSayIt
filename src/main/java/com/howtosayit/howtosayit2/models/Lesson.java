package com.howtosayit.howtosayit2.models;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Lesson implements Parcelable {
    private List<Phrase> phrases = new ArrayList<>();

    public Lesson(){}

    public List<Phrase> getPhrases() {
        return phrases;
    }

    public void setPhrases(List<Phrase> entityList) {
        this.phrases = entityList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
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
        phrases = parcel.readArrayList(Phrase.class.getClassLoader());
    }
}
