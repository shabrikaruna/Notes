package com.example.android.notes.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "note")
public class NoteEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    @Ignore
    public NoteEntry(String title, String description, Date updatedAt) {
        this.title = title;
        this.description = description;
        this.updatedAt = updatedAt;
    }

    public NoteEntry(int id, String title, String description, Date updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.updatedAt = updatedAt;
    }

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

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
