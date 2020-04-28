package com.orange.contentprovider.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.orange.contentprovider.R;
import com.orange.contentprovider.adapter.FilesAdapter;
import com.orange.contentprovider.db.DatabaseUtils;
import com.orange.contentprovider.model.FileModel;
import com.orange.contentprovider.viewmodel.FileViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity implements FilesAdapter.OnDeleteButtonClickListener {
    static final String TAG = "MainActivity";

    static final int TAKE_PHOTO_REQUEST_CODE = 0;
    static final int PICK_PHOTO_REQUEST_CODE = 1;

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
            selectImage();
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


    private void selectImage() {
        if (checkPermission()) {
            final CharSequence[] options = {"Take photo", "Choose from gallery", "Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose image")
                    .setItems(options, (dialog, item) -> {
                        switch (item) {
                            case 0:
                                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(takePicture, TAKE_PHOTO_REQUEST_CODE);
                                break;

                            case 1:
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhoto, PICK_PHOTO_REQUEST_CODE);
                                break;

                            case 2:
                                dialog.dismiss();
                                break;

                        }
                    });
            builder.show();

        }
    }


    private void saveImageToInternal (Bitmap bm){
        ContextWrapper cw = new ContextWrapper(this);

        Long tsLong = System.currentTimeMillis();
        String imageName = tsLong.toString() + ".jpeg";

        File path = cw.getDir("Formation",MODE_PRIVATE);
        path.mkdirs();

        File imageFile = new File(path, imageName);
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(imageFile);

            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            DatabaseUtils db = new DatabaseUtils(this);
            FileModel fileModel = new FileModel(imageName,imageFile.getAbsolutePath() , getMimeType(imageFile.getAbsolutePath()));
            db.saveFile(fileModel);
            Log.d(TAG ,"saveImageToInternal " + fileModel.toString());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private  void saveImageToExternal(Bitmap bm){
        Long tsLong = System.currentTimeMillis();
        String imageName = tsLong.toString() + ".png";

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "Formation");
        path.mkdirs();

        File imageFile = new File(path, imageName);
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        DatabaseUtils db = new DatabaseUtils(this);
        FileModel fileModel = new FileModel(imageName,imageFile.getAbsolutePath() , getMimeType(imageFile.getAbsolutePath()));
        db.saveFile(fileModel);
        Log.d(TAG , fileModel.toString());

    }


    private void getImageFromGallery(Intent data){
        Uri selectedImage = data.getData();
        String[] filePath = {MediaStore.Images.Media.DATA};

        if(selectedImage != null){
            Cursor cursor = getContentResolver().query(selectedImage, filePath, null, null, null);

            if(cursor != null){
                cursor.moveToFirst();

                int columnIdex = cursor.getColumnIndex(filePath[0]);
                String picturePath = cursor.getString(columnIdex);

                DatabaseUtils db = new DatabaseUtils(this);

                FileModel fileModel = new FileModel(selectedImage.getLastPathSegment(), picturePath, getMimeType(picturePath));
                Log.d(TAG , fileModel.toString());

                db.saveFile(fileModel);
                cursor.close();
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_CANCELED){
            switch (requestCode){
                case TAKE_PHOTO_REQUEST_CODE:
                    if(resultCode == RESULT_OK && data !=null){
                        Toast.makeText(this, "from camera", Toast.LENGTH_SHORT).show();
                        Bitmap image = (Bitmap) data.getExtras().get("data");
                        //saveImageToExternal(image);
                        saveImageToInternal(image);
                    }
                    break;

                case PICK_PHOTO_REQUEST_CODE:
                    if(resultCode == RESULT_OK && data !=null){
                        Toast.makeText(this, "from gallery", Toast.LENGTH_SHORT).show();
                        getImageFromGallery(data);
                    }
                    break;
            }
        }
    }

    private String getMimeType(String url){
        String type = "";
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);

        if(extension != null){
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return  type;
    }

    private boolean checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED  &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return false;
        }else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 0){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED)  {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            }
        }
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
