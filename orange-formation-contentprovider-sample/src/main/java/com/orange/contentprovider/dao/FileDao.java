package com.orange.contentprovider.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.orange.contentprovider.model.File;

import java.util.List;

/**
 * Created by jaouher on 25/04/2020.
 */

@Dao
public interface FileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<File> files);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(File file);

    @Update
    void update(File file);

    @Delete
    void delete(File file);

    @Query("SELECT * FROM File")
    LiveData<List<File>> findAll();

    @Query("SELECT * FROM File where id = :id")
    File findFile(int id);
}
