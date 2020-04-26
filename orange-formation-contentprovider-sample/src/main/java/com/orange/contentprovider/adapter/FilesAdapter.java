package com.orange.contentprovider;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


import com.orange.contentprovider.db.File;

/**
 * Created by jaouher on 25/04/2020.
 */

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FileViewHolder> {

    public interface OnDeleteButtonClickListener {
        void onDeleteButtonClicked(File file);
    }

    private List<File> data;
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

    public void setData(List<File> newData) {
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
        private Button btnDelete;

        FileViewHolder(View itemView) {
            super(itemView);

            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvPath = itemView.findViewById(R.id.tvPath);
            tvType = itemView.findViewById(R.id.tvType);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(final File file) {
            if (file != null) {
                tvFileName.setText(file.getFileName());
                tvPath.setText(file.getPath());
                tvType.setText(file.getType());
                btnDelete.setOnClickListener(v -> {
                    if (onDeleteButtonClickListener != null)
                        onDeleteButtonClickListener.onDeleteButtonClicked(file);
                });

            }
        }

    }

    class FileDiffCallback extends DiffUtil.Callback {

        private final List<File> oldFiles, newFiles;

        public FileDiffCallback(List<File> oldFiles, List<File> newFiles) {
            this.oldFiles = oldFiles;
            this.newFiles = newFiles;
        }

        @Override
        public int getOldListSize() {
            return oldFiles.size();
        }

        @Override
        public int getNewListSize() {
            return newFiles.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldFiles.get(oldItemPosition).getId() == newFiles.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldFiles.get(oldItemPosition).equals(newFiles.get(newItemPosition));
        }
    }
}
