package com.orange.contentprovider.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


import com.orange.contentprovider.R;
import com.orange.contentprovider.model.FileModel;

/**
 * Created by jaouher on 25/04/2020.
 */

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FileViewHolder> {

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
        private AppCompatImageButton btnDelete;

        FileViewHolder(View itemView) {
            super(itemView);


            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvPath = itemView.findViewById(R.id.tvPath);
            tvType = itemView.findViewById(R.id.tvType);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(final FileModel fileModel) {
            if (fileModel != null) {
                tvFileName.setText(fileModel.getFileName());
                tvPath.setText(fileModel.getPath());
                tvType.setText(fileModel.getType());
                btnDelete.setOnClickListener(v -> {
                    if (onDeleteButtonClickListener != null)
                        onDeleteButtonClickListener.onDeleteButtonClicked(fileModel);
                });

                itemView.setOnClickListener(v -> {

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

            Button dialogButton = (Button) dialog.findViewById(R.id.btnShare);
            // if button is clicked, close the custom dialog
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Toast.makeText(context, "share", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.show();
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
