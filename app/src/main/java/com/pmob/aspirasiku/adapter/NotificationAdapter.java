package com.pmob.aspirasiku.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pmob.aspirasiku.R;
import com.pmob.aspirasiku.data.model.Notifikasi;
import com.pmob.aspirasiku.ui.detail.PostDetailActivity;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<Notifikasi> notifikasiList;
    private final Context context;

    public NotificationAdapter(List<Notifikasi> notifikasiList, Context context) {
        this.notifikasiList = notifikasiList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notifikasi notifikasi = notifikasiList.get(position);
        holder.txtMessage.setText(notifikasi.getPesan());
        holder.txtTime.setText(notifikasi.getWaktu());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("post_id", notifikasi.getId_postingan());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notifikasiList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage, txtTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtTime = itemView.findViewById(R.id.txtTime);
        }
    }
}