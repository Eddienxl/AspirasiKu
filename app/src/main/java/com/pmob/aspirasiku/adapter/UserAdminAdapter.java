package com.pmob.aspirasiku.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pmob.aspirasiku.R;
import com.pmob.aspirasiku.data.model.Pengguna;

import java.util.List;

public class UserAdminAdapter extends RecyclerView.Adapter<UserAdminAdapter.ViewHolder> {

    private final List<Pengguna> userList;
    private final OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int userId);
    }

    public UserAdminAdapter(List<Pengguna> userList, OnDeleteClickListener listener) {
        this.userList = userList;
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pengguna user = userList.get(position);
        holder.txtName.setText(user.getNama());
        holder.txtEmail.setText(user.getEmail());
        holder.btnDelete.setOnClickListener(v -> onDeleteClickListener.onDeleteClick(user.getId()));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtEmail;
        Button btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}