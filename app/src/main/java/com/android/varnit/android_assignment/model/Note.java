package com.android.varnit.android_assignment.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "note")
public class Note implements Serializable{
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "isFavorite")
    private boolean isFavorite;

    @ColumnInfo(name = "isStarred")
    private boolean isStarred;

    @ColumnInfo(name = "isPoemCategory")
    private boolean isPoemCategory;

    @ColumnInfo(name = "isStoryCategory")
    private boolean isStoryCategory;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public boolean getIsStarred() {
        return isStarred;
    }

    public void setIsStarred(boolean isStarred) {
        this.isStarred = isStarred;
    }

    public boolean getIsPoemCategory() {
        return isPoemCategory;
    }

    public void setIsPoemCategory(boolean category) {
        this.isPoemCategory = category;
    }

    public boolean getIsStoryCategory() {
        return isStoryCategory;
    }

    public void setIsStoryCategory(boolean storyCategory) {
        isStoryCategory = storyCategory;
    }
}