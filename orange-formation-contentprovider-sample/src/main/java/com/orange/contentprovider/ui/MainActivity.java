package com.orange.contentprovider;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import com.orange.contentprovider.db.File;


public class MainActivity extends AppCompatActivity implements FilesAdapter.OnDeleteButtonClickListener {

    private FilesAdapter filesAdapter;
    private FileViewModel fileViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        filesAdapter = new FilesAdapter(this, this);

        fileViewModel = ViewModelProviders.of(this).get(FileViewModel.class);
        fileViewModel.getAllFiles().observe(this, files -> filesAdapter.setData(files));

        RecyclerView recyclerView = findViewById(R.id.rvFilesLis);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(filesAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addFile) {
            fileViewModel.saveFile(new File("new file ", "new File path", "new file Type"));
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDeleteButtonClicked(File file) {
        fileViewModel.deleteFile(file);

    }
}
