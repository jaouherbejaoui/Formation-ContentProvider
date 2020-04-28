package com.orange.contentprovider.adapter;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import com.orange.contentprovider.R;
import com.orange.contentprovider.contentprovider.FileDateContentProvider;
import com.orange.contentprovider.model.FileModel;

import static android.content.ContentValues.TAG;

/**
 * Created by jaouher on 25/04/2020.
 */

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FileViewHolder> {
    private static final String TAG = "FilesAdapter";

    public interface OnDeleteButtonClickListener {
        void onDeleteButtonClicked(FileModel fileModel);
    }

    private List<FileModel> data;
    private Context context;
    private LayoutInflater layoutInflater;
    private OnDeleteButtonClickListener onDeleteButtonClickListener;

    public FilesAdapter(Context context, OnDeleteButtonClickListener listener) {
        this.data = new ArrayList<>();
        this.context = context;
        this.onDeleteButtonClickListener = listener;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.layout_file_item, parent, false);
        return new FileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FileViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<FileModel> newData) {
        if (data != null) {
            FileDiffCallback fileDiffCallback = new FileDiffCallback(data, newData);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(fileDiffCallback);

            data.clear();
            data.addAll(newData);
            diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            data = newData;
        }
    }

    class FileViewHolder extends RecyclerView.ViewHolder {

        private TextView tvFileName, tvPath, tvType;
        private ImageView imageView;
        private AppCompatImageButton btnDelete;

        FileViewHolder(View itemView) {
            super(itemView);


            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvPath = itemView.findViewById(R.id.tvPath);
            tvType = itemView.findViewById(R.id.tvType);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            imageView = itemView.findViewById(R.id.imageViewPreview);
        }

        void bind(final FileModel fileModel) {
            if (fileModel != null) {
                tvFileName.setText(fileModel.getFileName());
                tvPath.setText(fileModel.getPath());
                tvType.setText(fileModel.getType());
                imageView.setImageBitmap(BitmapFactory.decodeFile(fileModel.getPath()));

                btnDelete.setOnClickListener(v -> {
                    if (onDeleteButtonClickListener != null)
                        onDeleteButtonClickListener.onDeleteButtonClicked(fileModel);
                });

                itemView.setOnClickListener(v -> {
                    //Toast.makeText(context, fileModel.getFileName(), Toast.LENGTH_SHORT).show();
                    showCustomDialog(fileModel);
                });

            }
        }


        public void showCustomDialog(FileModel file) {
            // custom dialog
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_file_details);

            // set the custom dialog components - text, image and button
            TextView text = dialog.findViewById(R.id.tvFileName);
            text.setText(file.getFileName());

            ImageView image = dialog.findViewById(R.id.ivFile);
            image.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));

            AppCompatImageButton btnShare = dialog.findViewById(R.id.btnShare);
            AppCompatImageButton btnOpen = dialog.findViewById(R.id.btnOpen);


            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Toast.makeText(context, "share", Toast.LENGTH_SHORT).show();
                    shareFileWithContentProvider(context,file);
                }
            });


            btnOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Toast.makeText(context, "open", Toast.LENGTH_SHORT).show();
                    openFileWithContentProvider(context, file);
                }
            });

            dialog.show();
        }

        private void shareFileWithContentProvider(Context context, FileModel fileModel) {
            FileDateContentProvider fileDateContentProvider = new FileDateContentProvider(context);
            Uri uri = Uri.parse(FileDateContentProvider.CONTENT_URI_STRING
                    + File.separator
                    + FileDateContentProvider.IMAGE_BASE_PATH
                    +fileModel.getId());

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "subject");
            shareIntent.putExtra(Intent.EXTRA_TEXT, fileModel.getFileName());
            shareIntent.setType(fileModel.getType());
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

            context.startActivity(Intent.createChooser(shareIntent, "Email: "));



        }

        private void openFileWithContentProvider(Context context, FileModel fileModel) {
            FileDateContentProvider fileDateContentProvider = new FileDateContentProvider(context);
            Uri uri = Uri.parse(FileDateContentProvider.CONTENT_URI_STRING
                    + File.separator
                    + FileDateContentProvider.IMAGE_BASE_PATH
                    +fileModel.getId());

            //uri = "content://com.orange.cprovider/img/6"
            Log.d(TAG, uri.toString());

            Intent openIntent = new Intent(Intent.ACTION_VIEW);
            openIntent.setDataAndType(uri,fileModel.getType());
            openIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                context.startActivity(openIntent);
            }catch(ActivityNotFoundException e){
                Toast.makeText(context, "No application found", Toast.LENGTH_SHORT).show();
            }
        }

    }


    class FileDiffCallback extends DiffUtil.Callback {

        private final List<FileModel> oldFileModels, newFileModels;

        public FileDiffCallback(List<FileModel> oldFileModels, List<FileModel> newFileModels) {
            this.oldFileModels = oldFileModels;
            this.newFileModels = newFileModels;
        }

        @Override
        public int getOldListSize() {
            return oldFileModels.size();
        }

        @Override
        public int getNewListSize() {
            return newFileModels.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldFileModels.get(oldItemPosition).getId() == newFileModels.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldFileModels.get(oldItemPosition).equals(newFileModels.get(newItemPosition));
        }
    }
}

