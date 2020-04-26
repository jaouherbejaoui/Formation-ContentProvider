package com.orange.contentprovider.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.orange.contentprovider.dao.FileDao;
import com.orange.contentprovider.model.FileModel;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by jaouher on 25/04/2020.
 */

@Database(entities = {FileModel.class}, version = 1)
public abstract class FilesDatabase extends RoomDatabase {

    private static FilesDatabase INSTANCE;

    private final static List<FileModel> FILES_ARRAYS = Arrays.asList(
            new FileModel("filename1", "path1", "type1"),
            new FileModel("filename2", "path2", "type2"),
            new FileModel("filename3", "path3", "type3")
    );

    public abstract FileDao postDao();

    private static final Object sLock = new Object();

    public static FilesDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        FilesDatabase.class, "files.db")
                        .allowMainThreadQueries()
                        .addCallback(new Callback() {
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                super.onCreate(db);
                                Executors.newSingleThreadExecutor().execute(
                                        () -> getInstance(context).postDao().saveAll(FILES_ARRAYS));
                            }
                        })
                        .build();
            }
            return INSTANCE;
        }
    }
}
