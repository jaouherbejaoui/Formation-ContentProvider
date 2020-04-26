package com.orange.contentprovider.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.contentprovider.R;
import com.orange.contentprovider.model.File;

public class FileDetailsActivity extends AppCompatActivity {
    private File mFile;
    private TextView tvFileName;
    ImageView ivFile;
    TextView tvType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_details);

        setFile();
        setView();
        loadData(mFile);
    }

    private void setView(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvFileName = findViewById(R.id.tvFileName);
        ivFile = findViewById(R.id.ivFile);
        tvType = findViewById(R.id.tvType);


    }

    private void loadData(File file ){
        tvFileName.setText(file.getFileName());
        //tvPath.setText(file.getPath());
        tvType.setText(file.getType());
    }

    private void setFile(){
        Intent i = getIntent();
        mFile = (File) i.getSerializableExtra("file_key");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
