package com.pmob.aspirasiku.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pmob.aspirasiku.R;
import com.pmob.aspirasiku.data.model.Postingan;

import java.util.List;

public class PostAdminAdapter extends RecyclerView.Adapter<PostAdminAdapter.ViewHolder> {

    private final List<Postingan> postList;
    private final OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int postId);
    }

    public PostAdminAdapter(List<Postingan> postList, OnDeleteClickListener listener) {
        this.postList = postList;
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Postingan post = postList.get(position);
        holder.txtTitle.setText(post.getJudul());
        holder.txtContent.setText(post.getKonten());
        holder.btnDelete.setOnClickListener(v -> onDeleteClickListener.onDeleteClick(post.getId()));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtContent;
        Button btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtContent = itemView.findViewById(R.id.txtContent);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}