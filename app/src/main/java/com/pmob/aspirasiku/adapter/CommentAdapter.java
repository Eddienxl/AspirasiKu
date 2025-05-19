package com.pmob.aspirasiku.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pmob.aspirasiku.R;
import com.pmob.aspirasiku.data.model.Komentar;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Komentar> list;
    private Context context;

    public CommentAdapter(List<Komentar> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_komentar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Komentar komentar = list.get(position);
        holder.txtKomentar.setText(komentar.getKonten());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtKomentar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtKomentar = itemView.findViewById(R.id.txtKomentar);
        }
    }
}
