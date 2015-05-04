package com.howtosayit.howtosayit2;


import java.util.ArrayList;
import java.util.List;

public class Lesson {
    private List<Entity> entityList = new ArrayList<>();

    public List<Entity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<Entity> entityList) {
        this.entityList = entityList;
    }
}
