package com.orange.contentprovider.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.orange.contentprovider.model.FileModel;

import java.util.List;

/**
 * Created by jaouher on 25/04/2020.
 */

@Dao
public interface FileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<FileModel> fileModels);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(FileModel fileModel);

    @Update
    void update(FileModel fileModel);

    @Delete
    void delete(FileModel fileModel);

    @Query("SELECT * FROM FileModel")
    LiveData<List<FileModel>> findAll();

    @Query("SELECT * FROM FileModel where id = :id")
    FileModel findFile(int id);
}
