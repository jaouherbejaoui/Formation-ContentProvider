package com.orange.contentprovider.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.orange.contentprovider.db.DatabaseUtils;
import com.orange.contentprovider.model.FileModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileDateContentProvider extends ContentProvider {

    private static final String TAG = "FileDateContentProvider";

    //content privder uri String
    public static final String CONTENT_URI_STRING = "content://com.orange.cprovider";

    //content privder authority
    public static final String CONTENT_AUTHORITY = "com.orange.cprovider";


    public static final String IMAGE_BASE_PATH = "img/";
    public static final int IMAGE_BASE_CODE = 1;


    public static final UriMatcher uriMAtcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //uriMAtcher.addURI(CONTENT_AUTHORITY, IMAGE_BASE_PATH+"*", IMAGE_BASE_CODE);
        uriMAtcher.addURI("com.orange.cprovider", "img/*", 1);
        uriMAtcher.addURI("com.orange.cprovider", "video/*", 2);
    }

    private Context context;

    public FileDateContentProvider(Context context) {
        this.context = context;
    }

    public FileDateContentProvider() {
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        Log.d(TAG, uri.toString());

        int matcherCode = uriMAtcher.match(uri);
        Log.d(TAG, "matcherCode" +String.valueOf(matcherCode));
        if (matcherCode == -1) {
            return null;
        }

        switch (matcherCode){
            case 1:
                DatabaseUtils db = new DatabaseUtils(context);
                FileModel fileModel = db.getFile(Integer.valueOf(uri.getLastPathSegment()));
                Log.d(TAG, fileModel.toString());

                if(fileModel == null){
                    Log.d(TAG, "fileModel is null");

                    return null;
                }

                File file =new File(fileModel.getPath());
                if(file.exists()) {
                    try {
                        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                    }catch (IOException e){
                        Log.d(TAG, e.getMessage());
                        return null;

                    }
                }
        }
        return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(TAG, uri.toString());

        int matcherCode = uriMAtcher.match(uri);
        Log.d(TAG, "matcherCode" +String.valueOf(matcherCode));
        if (matcherCode == -1) {
            return null;
        }

        MatrixCursor matrixCursor = new MatrixCursor(projection, 1);
        Object[] resultRow = new Object[projection.length];

        switch (matcherCode) {
            case 1: {

                //"com.orange.cprovider/img/3"
                DatabaseUtils db = new DatabaseUtils(context);
                FileModel fileModel = db.getFile(Integer.valueOf(uri.getLastPathSegment()));

                if (fileModel == null) {
                    return null;
                }

                for (int i = 0; i < projection.length; i++) {
                    if (OpenableColumns.DISPLAY_NAME.equals(projection[i])) {
                        resultRow[i] = "Orange12444";
                    }
                    /*
                    if(OpenableColumns.SIZE.equals(projection[i])){
                        resultRow[i] = fileModel.getFileName();
                    }
                     */
                }
            }
        }

        matrixCursor.addRow(resultRow);
        return matrixCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        Log.d(TAG, uri.toString());

        int matcherCode = uriMAtcher.match(uri);
        Log.d(TAG, "matcherCode" +String.valueOf(matcherCode));

        if (matcherCode == -1) {
            return null;
        }

        switch (matcherCode) {
            case 1: {
                DatabaseUtils db = new DatabaseUtils(context);
                FileModel fileModel = db.getFile(Integer.valueOf(uri.getLastPathSegment()));

                if(fileModel == null) {
                    return null;
                }

                return fileModel.getType();
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
