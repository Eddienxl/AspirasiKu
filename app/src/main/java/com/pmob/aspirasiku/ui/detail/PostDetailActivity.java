package com.pmob.aspirasiku.ui.detail;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.pmob.aspirasiku.data.model.InteraksiResponse; // Ini adalah model respons vote
import com.pmob.aspirasiku.data.model.Komentar;
import com.pmob.aspirasiku.data.model.Pengguna;
import com.pmob.aspirasiku.data.model.Postingan;
import com.pmob.aspirasiku.utils.TokenManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity {

    private static final String TAG = "PostDetailActivity";

    private TextView txtTitle, txtContent, txtUpvotes, txtDownvotes;
    private TextView txtUserName;

    private ImageButton btnUpvote, btnDownvote;
    private Button btnSubmitComment;
    private EditText inputComment;
    private RecyclerView recyclerComments;
    private CommentAdapter commentAdapter;
    private ApiService apiService;
    private TokenManager tokenManager;
    private int postId;

    // Current state of user's interaction with this post
    private boolean userHasUpvoted = false;
    private boolean userHasDownvoted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // Initialize Views from activity_post_detail.xml
        txtTitle = findViewById(R.id.txtTitle);
        txtContent = findViewById(R.id.txtContent);
        txtUpvotes = findViewById(R.id.txtUpvotes);
        txtDownvotes = findViewById(R.id.txtDownvotes);
        btnUpvote = findViewById(R.id.btnUpvote);
        btnDownvote = findViewById(R.id.btnDownvote);
        txtUserName = findViewById(R.id.txtUserName);
        btnSubmitComment = findViewById(R.id.btnSubmitComment);
        inputComment = findViewById(R.id.inputComment);
        recyclerComments = findViewById(R.id.recyclerComments);

        apiService = RetrofitClient.getApiService();
        tokenManager = new TokenManager(this);

        // --- Initialize RecyclerView and Adapter ---
        recyclerComments.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(new ArrayList<>(), this);
        recyclerComments.setAdapter(commentAdapter);
        // --- End Initialization ---

        // Get post_id from Intent
        postId = getIntent().getIntExtra("post_id", -1);
        if (postId != -1) {
            fetchPostDetail(); // Fetch post details
            fetchCommentsForPost(); // Fetch comments for this post
            fetchUserInteractionStatus(); // Fetch user's current vote status
        } else {
            Toast.makeText(this, "ID postingan tidak valid", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if ID is invalid
        }

        // Set OnClickListener for upvote button
        btnUpvote.setOnClickListener(v -> {
            if (tokenManager.getToken() == null || tokenManager.getToken().isEmpty()) {
                Toast.makeText(this, "Anda harus login untuk vote", Toast.LENGTH_SHORT).show();
                return;
            }
            sendVote("upvote");
        });

        // Set OnClickListener for downvote button
        btnDownvote.setOnClickListener(v -> {
            if (tokenManager.getToken() == null || tokenManager.getToken().isEmpty()) {
                Toast.makeText(this, "Anda harus login untuk vote", Toast.LENGTH_SHORT).show();
                return;
            }
            sendVote("downvote");
        });

        // Set OnClickListener for submit comment button
        btnSubmitComment.setOnClickListener(v -> {
            if (tokenManager.getToken() == null || tokenManager.getToken().isEmpty()) {
                Toast.makeText(this, "Anda harus login untuk berkomentar", Toast.LENGTH_SHORT).show();
                return;
            }
            submitComment();
        });
    }

    // --- Method to fetch post details from API ---
    private void fetchPostDetail() {
        Log.d(TAG, "Memulai fetchPostDetail untuk postId: " + postId);
        apiService.getPostDetail(postId).enqueue(new Callback<Postingan>() {
            @Override
            public void onResponse(Call<Postingan> call, Response<Postingan> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "fetchPostDetail berhasil. Menerima data postingan.");
                    Postingan post = response.body();

                    // Update UI post details
                    txtTitle.setText(post.getJudul());
                    txtContent.setText(post.getKonten());

                    Pengguna penulis = post.getPenulis();
                    if (penulis != null && penulis.getNama() != null) {
                        txtUserName.setText(penulis.getNama());
                    } else {
                        txtUserName.setText("Pengguna Anonim");
                    }

                    // Ensure vote counts are updated from the Postingan object
                    updateVoteCounts(post.getUpvotes(), post.getDownvotes());

                } else {
                    String errorMsg = "Terjadi kesalahan saat memuat detail postingan.";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                            Log.e(TAG, "fetchPostDetail Gagal: " + response.code() + " - " + errorMsg);
                        } catch (IOException e) {
                            Log.e(TAG, "IOException saat membaca error body fetchPostDetail: " + e.getMessage(), e);
                            errorMsg = "Error membaca body error (IO)";
                        }
                    } else {
                        Log.e(TAG, "fetchPostDetail Gagal: " + response.code() + " - No error body");
                    }
                    Toast.makeText(PostDetailActivity.this, "Gagal memuat detail postingan: " + response.code() + " " + errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Postingan> call, Throwable t) {
                Log.e(TAG, "fetchPostDetail onFailure: " + t.getMessage(), t);
                Toast.makeText(PostDetailActivity.this, "Error koneksi saat memuat detail postingan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- Method to fetch all comments for a specific post ---
    private void fetchCommentsForPost() {
        Log.d(TAG, "Memulai fetchCommentsForPost untuk postId: " + postId);
        apiService.getCommentsByPostId(postId).enqueue(new Callback<List<Komentar>>() {
            @Override
            public void onResponse(Call<List<Komentar>> call, Response<List<Komentar>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Komentar> komentarList = response.body();
                    Log.i(TAG, "Jumlah komentar diterima dari server (panggilan terpisah): " + (komentarList != null ? komentarList.size() : "0 (null list)"));

                    updateCommentsUI(komentarList); // Memisahkan logika update UI komentar ke metode terpisah
                } else {
                    String errorMsg = "Terjadi kesalahan saat memuat komentar.";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (IOException e) {
                            Log.e(TAG, "IOException saat membaca error body fetchCommentsForPost: " + e.getMessage(), e);
                            errorMsg = "Error membaca body error (IO)";
                        }
                    } else {
                        Log.e(TAG, "Fetch Comments Gagal: " + response.code() + " - No error body");
                    }
                    Toast.makeText(PostDetailActivity.this, "Gagal memuat komentar: " + response.code() + " " + errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Komentar>> call, Throwable t) {
                Log.e(TAG, "Fetch Comments onFailure: " + t.getMessage(), t);
                Toast.makeText(PostDetailActivity.this, "Error koneksi saat memuat komentar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Metode terpisah untuk mengupdate UI komentar
    private void updateCommentsUI(List<Komentar> komentarList) {
        commentAdapter.updateComments(komentarList != null ? komentarList : new ArrayList<>());
        Log.d(TAG, "Data komentar adapter diperbarui dari panggilan terpisah.");
    }


    // --- Method to fetch user's interaction status (upvote/downvote) ---
    private void fetchUserInteractionStatus() {
        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            userHasUpvoted = false;
            userHasDownvoted = false;
            updateVoteButtonStates(false, false);
            return;
        }

        apiService.getInteractionStatus(postId, "Bearer " + token).enqueue(new Callback<InteraksiResponse>() {
            @Override
            public void onResponse(Call<InteraksiResponse> call, Response<InteraksiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    InteraksiResponse result = response.body();
                    userHasUpvoted = result.hasUserUpvoted();
                    userHasDownvoted = result.hasUserDownvoted();
                    updateVoteButtonStates(userHasUpvoted, userHasDownvoted);
                    Log.d(TAG, "Status interaksi user berhasil diambil. Upvoted: " + userHasUpvoted + ", Downvoted: " + userHasDownvoted);
                } else {
                    String errorMsg = "Terjadi kesalahan saat mengambil status interaksi.";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                            if (response.code() == 404 && errorMsg.contains("Interaksi tidak ditemukan")) {
                                Log.i(TAG, "Interaksi tidak ditemukan untuk postingan ini, dianggap belum vote.");
                                userHasUpvoted = false;
                                userHasDownvoted = false;
                                updateVoteButtonStates(false, false);
                                return;
                            }
                            Log.e(TAG, "Fetch Interaction Status Gagal: " + response.code() + " - " + errorMsg);
                        } catch (IOException e) {
                            Log.e(TAG, "IOException saat membaca error body fetchInteractionStatus: " + e.getMessage(), e);
                            errorMsg = "Error membaca body error (IO)";
                        }
                    } else {
                        Log.e(TAG, "Fetch Interaction Status Gagal: " + response.code() + " - No error body");
                    }
                    Toast.makeText(PostDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    userHasUpvoted = false;
                    userHasDownvoted = false;
                    updateVoteButtonStates(false, false);
                }
            }

            @Override
            public void onFailure(Call<InteraksiResponse> call, Throwable t) {
                Log.e(TAG, "Fetch Interaction Status onFailure: " + t.getMessage(), t);
                Toast.makeText(PostDetailActivity.this, "Error koneksi saat mengambil status interaksi", Toast.LENGTH_SHORT).show();
                userHasUpvoted = false;
                userHasDownvoted = false;
                updateVoteButtonStates(false, false);
            }
        });
    }

    // --- Method to update the state of vote buttons ---
    private void updateVoteButtonStates(boolean upvoted, boolean downvoted) {
        if (upvoted) {
            btnUpvote.setEnabled(false);
            btnDownvote.setEnabled(true);
        } else if (downvoted) {
            btnUpvote.setEnabled(true);
            btnDownvote.setEnabled(false);
        } else {
            btnUpvote.setEnabled(true);
            btnDownvote.setEnabled(true);
        }
    }

    // --- Method to send vote (upvote/downvote) ---
    private void sendVote(String type) {
        // Optimistically update UI based on intended action
        int currentUpvotes = Integer.parseInt(txtUpvotes.getText().toString());
        int currentDownvotes = Integer.parseInt(txtDownvotes.getText().toString());

        // Store original state for potential revert
        final boolean originalUserHasUpvoted = userHasUpvoted; // Make final
        final boolean originalUserHasDownvoted = userHasDownvoted; // Make final
        final int initialUpvotes = currentUpvotes; // Store initial counts as final
        final int initialDownvotes = currentDownvotes; // Store initial counts as final


        if (type.equals("upvote")) {
            if (userHasUpvoted) {
                userHasUpvoted = false;
                currentUpvotes--;
            } else {
                userHasUpvoted = true;
                currentUpvotes++;
                if (userHasDownvoted) {
                    userHasDownvoted = false;
                    currentDownvotes--;
                }
            }
        } else { // type is "downvote"
            if (userHasDownvoted) {
                userHasDownvoted = false;
                currentDownvotes--;
            } else {
                userHasDownvoted = true;
                currentDownvotes++;
                if (userHasUpvoted) {
                    userHasUpvoted = false;
                    currentUpvotes--;
                }
            }
        }
        updateVoteCounts(currentUpvotes, currentDownvotes);
        updateVoteButtonStates(userHasUpvoted, userHasDownvoted);

        // Temporarily disable buttons to prevent multiple rapid clicks
        btnUpvote.setEnabled(false);
        btnDownvote.setEnabled(false);

        InteraksiRequest request = new InteraksiRequest(postId, type);
        apiService.sendVote(request, "Bearer " + tokenManager.getToken()).enqueue(new Callback<InteraksiResponse>() {
            @Override
            public void onResponse(Call<InteraksiResponse> call, Response<InteraksiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    InteraksiResponse result = response.body();
                    updateVoteCounts(result.getUpvotes(), result.getDownvotes());
                    userHasUpvoted = result.hasUserUpvoted();
                    userHasDownvoted = result.hasUserDownvoted();
                    updateVoteButtonStates(userHasUpvoted, userHasDownvoted);
                    Toast.makeText(PostDetailActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Vote berhasil: " + type + ". API message: " + result.getMessage());
                } else {
                    // Revert optimistic UI updates on failure
                    // Menggunakan nilai awal yang disimpan sebagai final
                    updateVoteCounts(initialUpvotes, initialDownvotes);
                    userHasUpvoted = originalUserHasUpvoted;
                    userHasDownvoted = originalUserHasDownvoted;
                    updateVoteButtonStates(userHasUpvoted, userHasDownvoted);

                    String errorBodyString = "Tidak ada body error.";
                    try {
                        if (response.errorBody() != null) {
                            errorBodyString = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body (IOException) sendVote: " + e.getMessage(), e);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body (Other Exception) sendVote: " + e.getMessage(), e);
                    }
                    Log.e(TAG, "Send Vote Gagal: " + response.code() + " - " + errorBodyString);
                    Toast.makeText(PostDetailActivity.this, "Gagal mengirim vote: " + response.code() + " - " + errorBodyString, Toast.LENGTH_LONG).show();
                }
                btnUpvote.setEnabled(true);
                btnDownvote.setEnabled(true);
            }

            @Override
            public void onFailure(Call<InteraksiResponse> call, Throwable t) {
                userHasUpvoted = originalUserHasUpvoted;
                userHasDownvoted = originalUserHasDownvoted;
                updateVoteButtonStates(userHasUpvoted, userHasDownvoted);

                Log.e(TAG, "Send Vote onFailure: " + t.getMessage(), t);
                Toast.makeText(PostDetailActivity.this, "Error koneksi saat mengirim vote: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                btnUpvote.setEnabled(true);
                btnDownvote.setEnabled(true);
            }
        });
    }

    // --- Method to update vote counts on UI ---
    private void updateVoteCounts(int upvotes, int downvotes) {
        txtUpvotes.setText(String.valueOf(upvotes));
        txtDownvotes.setText(String.valueOf(downvotes));
    }

    // --- Method to submit comment ---
    private void submitComment() {
        String content = inputComment.getText().toString().trim();
        if (content.isEmpty()) {
            inputComment.setError("Komentar tidak boleh kosong");
            inputComment.requestFocus();
            return;
        }

        btnSubmitComment.setEnabled(false); // Disable button temporarily
        Log.d(TAG, "Mengirim komentar: " + content);

        Komentar komentarRequest = new Komentar(postId, content);

        // API call to create comment
        apiService.createComment(komentarRequest, "Bearer " + tokenManager.getToken()).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                btnSubmitComment.setEnabled(true); // Re-enable button
                if (response.isSuccessful()) {
                    Log.i(TAG, "Komentar berhasil dikirim ke server.");
                    inputComment.setText(""); // Clear comment input

                    // Refresh comment list after a short delay
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        Log.d(TAG, "Memanggil fetchCommentsForPost setelah submit komentar berhasil dengan delay.");
                        fetchCommentsForPost(); // <<< Panggil metode BARU untuk me-refresh komentar
                    }, 500);

                    Toast.makeText(PostDetailActivity.this, "Komentar berhasil dikirim", Toast.LENGTH_SHORT).show();
                } else {
                    String errorBodyString = "Tidak ada body error.";
                    try {
                        if (response.errorBody() != null) {
                            errorBodyString = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body (IOException) submitComment: " + e.getMessage(), e);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body (Other Exception) submitComment: " + e.getMessage(), e);
                    }
                    Log.e(TAG, "Submit Comment Gagal: " + response.code() + " - " + errorBodyString);
                    Toast.makeText(PostDetailActivity.this, "Gagal mengirim komentar: " + response.code() + " - " + errorBodyString, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                btnSubmitComment.setEnabled(true); // Re-enable button
                Log.e(TAG, "Submit Comment onFailure: " + t.getMessage(), t);
                Toast.makeText(PostDetailActivity.this, "Error koneksi saat mengirim komentar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}