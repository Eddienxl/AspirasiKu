package com.pmob.aspirasiku.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pmob.aspirasiku.R;
import com.pmob.aspirasiku.data.api.ApiService;
import com.pmob.aspirasiku.data.api.RetrofitClient;
import com.pmob.aspirasiku.data.model.Postingan;
import com.pmob.aspirasiku.data.model.Kategori;
import com.pmob.aspirasiku.data.model.Pengguna;
import com.pmob.aspirasiku.ui.detail.PostDetailActivity;
import com.pmob.aspirasiku.utils.TokenManager;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private static final String TAG = "PostAdapter";

    private List<Postingan> postList;
    private Context context;
    private boolean isReviewer = false;
    private ApiService apiService;
    private TokenManager tokenManager;

    public PostAdapter(List<Postingan> postList, Context context) {
        this.postList = postList != null ? postList : new ArrayList<>();
        this.context = context;
        this.apiService = RetrofitClient.getApiService();
        this.tokenManager = new TokenManager(context);
    }

    public void setReviewerStatus(boolean isReviewer) {
        this.isReviewer = isReviewer;
        notifyDataSetChanged();
    }

    public void removePost(int postId) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getId() == postId) {
                postList.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        if (position < 0 || position >= postList.size()) {
            Log.e(TAG, "onBindViewHolder: Invalid position " + position + ", current list size " + postList.size());
            return;
        }

        Postingan post = postList.get(position);
        Log.d(TAG, "--- Binding post ID: " + post.getId() + " | Judul: '" + post.getJudul() + "' ---");

        // --- Handle Penulis (Author) and Profile Picture ---
        Pengguna penulis = post.getPenulis();
        if (penulis != null) {
            String namaPenulis = penulis.getNama();
            String urlProfil = penulis.getProfilePicture();

            if (namaPenulis != null && !namaPenulis.isEmpty()) {
                holder.txtUserNamePost.setText(namaPenulis);
            } else {
                holder.txtUserNamePost.setText("Pengguna Anonim");
            }

            if (urlProfil != null && !urlProfil.isEmpty()) {
                Glide.with(context)
                        .load(urlProfil)
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .into(holder.imgProfilePost);
            } else {
                holder.imgProfilePost.setImageResource(R.drawable.ic_profile);
            }
        } else {
            if (post.isAnonim()) {
                holder.txtUserNamePost.setText("Anonim");
            } else {
                holder.txtUserNamePost.setText("Pengguna Tidak Diketahui");
            }
            holder.imgProfilePost.setImageResource(R.drawable.ic_profile);
            Log.e(TAG, "ERROR: Objek penulis untuk postingan '" + post.getJudul() + "' (ID: " + post.getId() + ") adalah NULL.");
        }
        // --- End Handle Penulis ---

        holder.txtJudul.setText(post.getJudul());

        String konten = post.getKonten();
        if (konten != null && !konten.isEmpty()) {
            holder.txtKonten.setText(konten);
        } else {
            holder.txtKonten.setText("Konten tidak tersedia atau kosong");
        }

        Kategori kategori = post.getKategori();
        if (kategori != null && kategori.getNama() != null && !kategori.getNama().isEmpty()) {
            holder.txtKategori.setText("Kategori: " + kategori.getNama());
        } else {
            holder.txtKategori.setText("Kategori ID: " + post.getId_kategori());
        }

        String createdAt = post.getCreated_at();
        if (createdAt != null && !createdAt.isEmpty()) {
            holder.txtWaktu.setText(createdAt);
        } else {
            holder.txtWaktu.setText("Waktu tidak diketahui");
        }

        // --- Handle Post Image (imgPost) ---
        // IMPORTANT: Your Postingan model currently does NOT have a field for a post image URL.
        // If your API returns an image URL for the post itself, you need to add it to Postingan.java
        // For now, it will remain GONE unless you provide a postImageUrl from your model.
        String postImageUrl = null; // Replace with actual getter if Postingan model updated
        // Example: String postImageUrl = post.getPostImageUrl();
        if (postImageUrl != null && !postImageUrl.isEmpty()) {
            holder.imgPost.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(postImageUrl)
                    .placeholder(R.drawable.placeholder_post_image) // Make sure this drawable exists
                    .error(R.drawable.placeholder_post_image)
                    .into(holder.imgPost);
        } else {
            holder.imgPost.setVisibility(View.GONE);
        }
        // --- End Handle Post Image ---

        // --- Handle Upvotes, Downvotes, and Comments ---
        // Make sure your Postingan model has getUpvotes(), getDownvotes(), getKomentar()
        holder.txtUpvoteCount.setText(String.valueOf(post.getUpvotes()) + " Upvotes");
        holder.txtDownvoteCount.setText(String.valueOf(post.getDownvotes()) + " Downvotes");
        holder.txtCommentCount.setText(String.valueOf(post.getKomentar() != null ? post.getKomentar().size() : 0) + " Comments");

        // TODO: Add listeners for interactive elements if they exist (e.g., upvote/downvote icons)
        // Example: holder.ivUpvote.setOnClickListener(v -> { /* handle upvote */ });


        // --- Handle Delete Button for Reviewers ---
        if (isReviewer) {
            holder.ivDeletePost.setVisibility(View.VISIBLE);
            holder.ivDeletePost.setOnClickListener(v -> {
                // You might want to add a confirmation dialog here for user experience
                deletePost(post.getId(), holder.getAdapterPosition());
            });
        } else {
            holder.ivDeletePost.setVisibility(View.GONE);
        }
        // --- End Handle Delete Button ---

        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "Postingan ID " + post.getId() + " diklik. Meluncurkan PostDetailActivity.");
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("post_id", post.getId());
            context.startActivity(intent);
        });
        Log.d(TAG, "--- End Binding post ID: " + post.getId() + " ---");
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void updatePostList(List<Postingan> newPostList) {
        this.postList.clear();
        if (newPostList != null) {
            this.postList.addAll(newPostList);
        }
        notifyDataSetChanged();
        Log.d(TAG, "Daftar postingan diperbarui. Jumlah sekarang: " + this.postList.size());
    }

    private void deletePost(int postId, int adapterPosition) {
        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(context, "Anda belum login atau sesi berakhir.", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.deletePost(postId, "Bearer " + token).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Postingan berhasil dihapus.", Toast.LENGTH_SHORT).show();
                    removePost(postId);
                } else {
                    String errorMessage = "Gagal menghapus postingan. Kode: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += " - " + response.errorBody().string();
                        } catch (IOException e) {
                            Log.e(TAG, "Error reading error body: " + e.getMessage());
                        }
                    }
                    Log.e(TAG, "Delete failed: " + errorMessage);
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e(TAG, "Delete onFailure: " + t.getMessage());
                Toast.makeText(context, "Error koneksi saat menghapus postingan.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        // Corrected TextView and ImageView declarations to match item_post.xml IDs
        TextView txtJudul, txtKategori, txtWaktu, txtUserNamePost, txtKonten;
        TextView txtUpvoteCount, txtDownvoteCount, txtCommentCount; // Corrected TextViews for counts
        ImageView imgProfilePost, imgPost, ivDeletePost; // Corrected ImageViews

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize all views using their correct IDs from item_post.xml
            txtJudul = itemView.findViewById(R.id.txtJudul);
            txtKategori = itemView.findViewById(R.id.txtKategori);
            txtWaktu = itemView.findViewById(R.id.txtWaktu);
            imgProfilePost = itemView.findViewById(R.id.imgProfilePost);
            txtUserNamePost = itemView.findViewById(R.id.txtUserNamePost);
            txtKonten = itemView.findViewById(R.id.txtKonten);

            // Initialize the count TextViews
            txtUpvoteCount = itemView.findViewById(R.id.txtUpvoteCount);
            txtDownvoteCount = itemView.findViewById(R.id.txtDownvoteCount);
            txtCommentCount = itemView.findViewById(R.id.txtCommentCount);

            // Initialize the ImageViews
            imgPost = itemView.findViewById(R.id.imgPost);
            ivDeletePost = itemView.findViewById(R.id.ivDeletePost);
        }
    }
}