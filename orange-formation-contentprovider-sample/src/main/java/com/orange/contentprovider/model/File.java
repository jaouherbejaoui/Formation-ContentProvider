package com.orange.contentprovider.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by jaouher on 25/04/2020.
 */

@Entity
public class File {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String fileName;
    private String path;
    private String type;

    public File() {
    }

    //Constructor
    public File(String fileName, String path, String type) {
        this.fileName = fileName;
        this.path = path;
        this.type = type;
    }

    //getters & setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
