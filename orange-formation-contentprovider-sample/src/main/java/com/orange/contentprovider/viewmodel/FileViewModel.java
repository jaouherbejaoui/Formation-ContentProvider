package com.orange.contentprovider.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.orange.contentprovider.model.FileModel;
import com.orange.contentprovider.db.FilesDatabase;
import com.orange.contentprovider.dao.FileDao;

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

    public LiveData<List<FileModel>> getAllFiles() {
        return fileDao.findAll();
    }

    public void saveFile(FileModel fileModel) {
        executorService.execute(() -> fileDao.save(fileModel));
    }

    public void deleteFile(FileModel post) {
        executorService.execute(() -> fileDao.delete(post));
    }
}
