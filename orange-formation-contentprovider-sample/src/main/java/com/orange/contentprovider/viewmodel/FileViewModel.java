package com.orange.contentprovider.adapter;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.orange.contentprovider.db.File;
import com.orange.contentprovider.db.FilesDatabase;
import com.orange.contentprovider.db.FileDao;

/**
 * Created by jaouher on 25/04/2020.
 */

public class FileViewModel extends AndroidViewModel {

    private FileDao fileDao;
    private ExecutorService executorService;

    public FileViewModel(@NonNull Application application) {
        super(application);
        fileDao = FilesDatabase.getInstance(application).postDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<File>> getAllFiles() {
        return fileDao.findAll();
    }

    public void saveFile(File file) {
        executorService.execute(() -> fileDao.save(file));
    }

    public void deleteFile(File post) {
        executorService.execute(() -> fileDao.delete(post));
    }
}
