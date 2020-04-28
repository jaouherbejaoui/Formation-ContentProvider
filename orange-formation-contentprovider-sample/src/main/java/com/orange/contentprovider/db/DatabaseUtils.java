package com.orange.contentprovider.db;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.orange.contentprovider.dao.FileDao;
import com.orange.contentprovider.model.FileModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseUtils {
    private FileDao fileDao;
    private ExecutorService executorService;

    public DatabaseUtils(Context context) {
        fileDao = FilesDatabase.getInstance(context).postDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<FileModel>> getAllFiles() {
        return fileDao.findAll();
    }

    public FileModel getFile(int id) {
        return fileDao.findFile(id);
    }

    public void saveFile(FileModel fileModel) {
        executorService.execute(() -> fileDao.save(fileModel));
    }

    public void deleteFile(FileModel post) {
        executorService.execute(() -> fileDao.delete(post));
    }
}
