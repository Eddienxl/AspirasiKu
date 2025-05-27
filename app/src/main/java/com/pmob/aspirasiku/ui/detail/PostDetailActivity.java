package com.pmob.aspirasiku.ui.detail;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pmob.aspirasiku.R;
import com.pmob.aspirasiku.adapter.CommentAdapter;
import com.pmob.aspirasiku.data.api.ApiService;
import com.pmob.aspirasiku.data.api.RetrofitClient;
import com.pmob.aspirasiku.data.model.InteraksiRequest;
import com.pmob.aspirasiku.data.model.InteraksiResponse;
import com.pmob.aspirasiku.data.model.Komentar;
import com.pmob.aspirasiku.data.model.Postingan;
import com.pmob.aspirasiku.utils.TokenManager;

import java.util.ArrayList; // Impor jika List Komentar dari API bisa null
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity {

    private TextView txtTitle, txtContent, txtUpvotes, txtDownvotes;
    private Button btnUpvote, btnDownvote, btnSubmitComment;
    private EditText inputComment;
    private RecyclerView recyclerComments;
    private CommentAdapter commentAdapter;
    private ApiService apiService;
    private TokenManager tokenManager;
    private int postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        txtTitle = findViewById(R.id.txtTitle);
        txtContent = findViewById(R.id.txtContent);
        txtUpvotes = findViewById(R.id.txtUpvotes);
        txtDownvotes = findViewById(R.id.txtDownvotes);
        btnUpvote = findViewById(R.id.btnUpvote);
        btnDownvote = findViewById(R.id.btnDownvote);
        btnSubmitComment = findViewById(R.id.btnSubmitComment);
        inputComment = findViewById(R.id.inputComment);
        recyclerComments = findViewById(R.id.recyclerComments);

        apiService = RetrofitClient.getApiService();
        tokenManager = new TokenManager(this);
        recyclerComments.setLayoutManager(new LinearLayoutManager(this));

        postId = getIntent().getIntExtra("post_id", -1);
        if (postId != -1) {
            fetchPostDetail();
        } else {
            Toast.makeText(this, "ID postingan tidak valid", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnUpvote.setOnClickListener(v -> {
            if (tokenManager.getToken() == null || tokenManager.getToken().isEmpty()) {
                Toast.makeText(this, "Anda harus login untuk vote", Toast.LENGTH_SHORT).show();
                return;
            }
            sendVote("upvote");
        });
        btnDownvote.setOnClickListener(v -> {
            if (tokenManager.getToken() == null || tokenManager.getToken().isEmpty()) {
                Toast.makeText(this, "Anda harus login untuk vote", Toast.LENGTH_SHORT).show();
                return;
            }
            sendVote("downvote");
        });
        btnSubmitComment.setOnClickListener(v -> {
            if (tokenManager.getToken() == null || tokenManager.getToken().isEmpty()) {
                Toast.makeText(this, "Anda harus login untuk berkomentar", Toast.LENGTH_SHORT).show();
                return;
            }
            submitComment();
        });
    }

    private void fetchPostDetail() {
        // Cek jika token diperlukan untuk mengambil detail postingan
        // String token = tokenManager.getToken();
        // String authHeader = (token != null && !token.isEmpty()) ? "Bearer " + token : null;
        // Jika API getPostDetail tidak butuh token, panggil seperti biasa:
        apiService.getPostDetail(postId).enqueue(new Callback<Postingan>() {
            @Override
            public void onResponse(Call<Postingan> call, Response<Postingan> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Postingan post = response.body();
                    txtTitle.setText(post.getJudul());
                    txtContent.setText(post.getKonten());
                    updateVoteCounts(post.getUpvotes(), post.getDownvotes()); // Sekarang seharusnya OK

                    List<Komentar> komentarList = post.getKomentar(); // Sekarang seharusnya OK
                    if (komentarList != null) {
                        commentAdapter = new CommentAdapter(komentarList, PostDetailActivity.this);
                        recyclerComments.setAdapter(commentAdapter);
                    } else {
                        // Handle jika daftar komentar null, misalnya dengan list kosong
                        commentAdapter = new CommentAdapter(new ArrayList<>(), PostDetailActivity.this);
                        recyclerComments.setAdapter(commentAdapter);
                    }
                } else {
                    Toast.makeText(PostDetailActivity.this, "Gagal memuat detail: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Postingan> call, Throwable t) {
                Log.e("POST_DETAIL", "Fetch Detail onFailure: " + t.getMessage());
                Toast.makeText(PostDetailActivity.this, "Error koneksi saat memuat detail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendVote(String type) {
        InteraksiRequest request = new InteraksiRequest(postId, type);
        apiService.sendVote(request, "Bearer " + tokenManager.getToken()).enqueue(new Callback<InteraksiResponse>() {
            @Override
            public void onResponse(Call<InteraksiResponse> call, Response<InteraksiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    InteraksiResponse result = response.body();
                    updateVoteCounts(result.getUpvotes(), result.getDownvotes());
                    // Toast.makeText(PostDetailActivity.this, "Vote berhasil", Toast.LENGTH_SHORT).show(); // Mungkin tidak perlu jika UI langsung update
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e("POST_DETAIL", "Send Vote Gagal: " + response.code() + " - " + errorBody);
                        Toast.makeText(PostDetailActivity.this, "Gagal mengirim vote: " + response.code(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("POST_DETAIL", "Error parsing error body vote", e);
                        Toast.makeText(PostDetailActivity.this, "Gagal mengirim vote: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<InteraksiResponse> call, Throwable t) {
                Log.e("POST_DETAIL", "Send Vote onFailure: " + t.getMessage());
                Toast.makeText(PostDetailActivity.this, "Error koneksi saat mengirim vote", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateVoteCounts(int upvotes, int downvotes) {
        txtUpvotes.setText(String.valueOf(upvotes));
        txtDownvotes.setText(String.valueOf(downvotes));
    }

    private void submitComment() {
        String content = inputComment.getText().toString().trim();
        if (content.isEmpty()) {
            inputComment.setError("Komentar tidak boleh kosong");
            return;
        }

        // Asumsi Komentar.java memiliki constructor (int postId, String content)
        // atau Anda membuat model request khusus untuk komentar.
        Komentar komentarRequest = new Komentar(postId, content);

        apiService.createComment(komentarRequest, "Bearer " + tokenManager.getToken()).enqueue(new Callback<Object>() { // Ganti Object dengan response model yang sesuai jika ada
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    inputComment.setText("");
                    fetchPostDetail(); // Refresh komentar dan detail postingan
                    Toast.makeText(PostDetailActivity.this, "Komentar berhasil dikirim", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e("POST_DETAIL", "Submit Comment Gagal: " + response.code() + " - " + errorBody);
                        Toast.makeText(PostDetailActivity.this, "Gagal mengirim komentar: " + response.code(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("POST_DETAIL", "Error parsing error body comment", e);
                        Toast.makeText(PostDetailActivity.this, "Gagal mengirim komentar: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("POST_DETAIL", "Submit Comment onFailure: " + t.getMessage());
                Toast.makeText(PostDetailActivity.this, "Error koneksi saat mengirim komentar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}