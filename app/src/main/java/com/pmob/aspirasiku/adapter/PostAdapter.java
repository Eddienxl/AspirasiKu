package com.pmob.aspirasiku.adapter;

import android.content.Context;
import android.content.Intent; // Impor yang hilang
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pmob.aspirasiku.R;
import com.pmob.aspirasiku.data.model.Postingan;
import com.pmob.aspirasiku.ui.detail.PostDetailActivity; // Impor untuk PostDetailActivity

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
        // Anda mungkin ingin menampilkan nama kategori, bukan hanya ID.
        // Ini memerlukan join atau query tambahan saat mengambil data.
        // Untuk sekarang, kita tampilkan ID seperti kode Anda.
        holder.txtKategori.setText("Kategori: " + post.getId_kategori());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("post_id", post.getId()); // Pastikan Postingan model punya method getId()
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
            // Pastikan ID ini ada di item_post.xml
            txtJudul = itemView.findViewById(R.id.txtJudul);
            txtKategori = itemView.findViewById(R.id.txtKategori);
        }
    }
}