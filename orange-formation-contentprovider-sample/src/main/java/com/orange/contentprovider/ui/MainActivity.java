package com.orange.contentprovider.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.orange.contentprovider.model.FileModel;
import com.orange.contentprovider.viewmodel.FileViewModel;
import com.orange.contentprovider.adapter.FilesAdapter;
import com.orange.contentprovider.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements FilesAdapter.OnDeleteButtonClickListener {
    static final String TAG = "MainActivity";

    Toolbar toolbar;
    private FilesAdapter filesAdapter;
    private FileViewModel fileViewModel;
    private FloatingActionButton fabAdd;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setAdapter();
        setView();
    }

    private void setView() {
        //setup view's ids
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.rvFilesLis);
        fabAdd = findViewById(R.id.fabAdd);

        //setup toolbar
        setSupportActionBar(toolbar);

        //floating action button set event
        fabAdd.setOnClickListener(view -> {
            fileViewModel.saveFile(createRandomFileObject());
        });

        //setup recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(filesAdapter);
    }


    private void setAdapter() {
        filesAdapter = new FilesAdapter(this, this);
        fileViewModel = ViewModelProviders.of(this).get(FileViewModel.class);
        fileViewModel.getAllFiles().observe(this, files -> filesAdapter.setData(files));
    }

    private FileModel createRandomFileObject(){
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();

        return new FileModel (ts, "filepath/"+ts,".type");
    }

    @Override
    public void onDeleteButtonClicked(FileModel fileModel) {
        fileViewModel.deleteFile(fileModel);
    }
}
