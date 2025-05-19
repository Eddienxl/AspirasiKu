package com.pmob.aspirasiku.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pmob.aspirasiku.R;
import com.pmob.aspirasiku.data.model.Postingan;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Postingan> postList;
    private Context context;

    public PostAdapter(List<Postingan> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Postingan post = postList.get(position);
        holder.txtJudul.setText(post.getJudul());
        holder.txtKategori.setText("Kategori: " + post.getId_kategori());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("post_id", post.getId());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView txtJudul, txtKategori;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            txtJudul = itemView.findViewById(R.id.txtJudul);
            txtKategori = itemView.findViewById(R.id.txtKategori);
        }
    }
}
