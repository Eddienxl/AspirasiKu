package com.pmob.aspirasiku.adapter;

import android.content.Context;
import android.util.Log; // Tambahkan import Log
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pmob.aspirasiku.R;
import com.pmob.aspirasiku.data.model.Komentar;
import com.pmob.aspirasiku.data.model.Pengguna; // Pastikan ini diimport

import java.util.ArrayList; // Pastikan ini diimport
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Komentar> list;
    private Context context;

    public CommentAdapter(List<Komentar> list, Context context) {
        // Penting: Inisialisasi list di sini, jangan biarkan null
        this.list = list != null ? list : new ArrayList<>();
        this.context = context;
    }

    public void updateComments(List<Komentar> newComments) {
        if (newComments != null) {
            this.list.clear();
            this.list.addAll(newComments);
            notifyDataSetChanged();
            Log.d("CommentAdapter", "Komentar diperbarui. Jumlah: " + this.list.size());
        } else {
            Log.w("CommentAdapter", "newComments is null, not updating adapter.");
        }
    }

    // Optional: Method untuk menambah satu komentar (jika API mengembalikan objek komentar setelah post)
    public void addComment(Komentar newComment) {
        if (newComment != null) {
            this.list.add(newComment);
            notifyItemInserted(this.list.size() - 1);
            // Bisa juga panggil smoothScrollToPosition di Activity setelah addComment
            Log.d("CommentAdapter", "Komentar baru ditambahkan: " + newComment.getKonten());
        }
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

        // Debugging di sini:
        Log.d("CommentAdapter", "Binding Komentar [" + position + "]: " + komentar.getKonten());
        if (komentar.getPenulis() != null) {
            Log.d("CommentAdapter", "  Penulis: " + komentar.getPenulis().getNama());
        } else {
            Log.d("CommentAdapter", "  Penulis: Anonim");
        }

        holder.txtKomentar.setText(komentar.getKonten());

        Pengguna penulis = komentar.getPenulis();
        if (penulis != null && penulis.getNama() != null) {
            holder.txtUserNameComment.setText(penulis.getNama()); // PENTING: GUNAKAN ID INI
        } else {
            holder.txtUserNameComment.setText("Anonim");
        }

        // Jika ada txtTimeComment di item_komentar.xml, set juga di sini
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtKomentar;
        TextView txtUserNameComment; // PENTING: SESUAIKAN NAMA VARIABEL DENGAN XML

        // Jika ada txtTimeComment di item_komentar.xml
        // TextView txtTimeComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtKomentar = itemView.findViewById(R.id.txtKomentar);
            txtUserNameComment = itemView.findViewById(R.id.txtUserNameComment); // PENTING: SESUAIKAN ID INI

            // Jika ada txtTimeComment di item_komentar.xml
            // txtTimeComment = itemView.findViewById(R.id.txtTimeComment);
        }
    }
}