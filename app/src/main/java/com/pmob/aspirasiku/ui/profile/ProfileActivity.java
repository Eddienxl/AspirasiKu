package com.pmob.aspirasiku.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View; // Import View for setting visibility
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pmob.aspirasiku.R;
import com.pmob.aspirasiku.adapter.PostAdapter;
import com.pmob.aspirasiku.data.api.ApiService;
import com.pmob.aspirasiku.data.api.RetrofitClient;
import com.pmob.aspirasiku.data.model.Pengguna;
import com.pmob.aspirasiku.data.model.Postingan;
import com.pmob.aspirasiku.ui.admin.AdminActivity; // Import AdminActivity
import com.pmob.aspirasiku.ui.auth.LoginActivity;
import com.pmob.aspirasiku.utils.TokenManager;
import com.pmob.aspirasiku.utils.PathUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView txtNama, txtNim, txtEmail, txtPeran;
    private Button btnEditProfile, btnChangePassword, btnLogout, btnAdmin; // Declare btnAdmin
    private ImageView imgProfile;
    private RecyclerView recyclerUserPosts;
    private ApiService apiService;
    private TokenManager tokenManager;
    private PostAdapter postAdapter;

    private int currentUserId = -1;

    private ActivityResultLauncher<String> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Assuming this is your layout file with the admin button

        txtNama = findViewById(R.id.txtNama);
        txtNim = findViewById(R.id.txtNim);
        txtEmail = findViewById(R.id.txtEmail);
        txtPeran = findViewById(R.id.txtPeran);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);
        imgProfile = findViewById(R.id.imgProfile);
        recyclerUserPosts = findViewById(R.id.recyclerUserPosts);

        // Initialize the Admin button
        btnAdmin = findViewById(R.id.btnAdmin); // Make sure you have android:id="@+id/btnAdmin" in your XML
        btnAdmin.setVisibility(View.GONE); // Initially hide the admin button

        apiService = RetrofitClient.getApiService();
        tokenManager = new TokenManager(this);

        recyclerUserPosts.setLayoutManager(new LinearLayoutManager(this));

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        uploadProfileImage(result);
                    }
                }
        );

        fetchUserProfile();

        imgProfile.setOnClickListener(v -> {
            galleryLauncher.launch("image/*");
        });

        btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });

        btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            tokenManager.clearToken();
            Toast.makeText(this, "Anda telah logout.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Set OnClickListener for the Admin button
        btnAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, AdminActivity.class);
            startActivity(intent);
        });
    }

    private void fetchUserProfile() {
        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Anda belum login.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        apiService.getUserProfile("Bearer " + token).enqueue(new Callback<Pengguna>() {
            @Override
            public void onResponse(Call<Pengguna> call, Response<Pengguna> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Pengguna pengguna = response.body();
                    currentUserId = pengguna.getId();
                    txtNama.setText("Nama: " + pengguna.getNama());
                    txtNim.setText("NIM: " + pengguna.getNim());
                    txtEmail.setText("Email: " + pengguna.getEmail());
                    txtPeran.setText("Peran: " + pengguna.getPeran());

                    // Conditional visibility for Admin button
                    if ("peninjau".equalsIgnoreCase(pengguna.getPeran())) {
                        btnAdmin.setVisibility(View.VISIBLE);
                    } else {
                        btnAdmin.setVisibility(View.GONE);
                    }

                    if (pengguna.getProfilePicture() != null && !pengguna.getProfilePicture().isEmpty()) {
                        Glide.with(ProfileActivity.this)
                                .load(pengguna.getProfilePicture())
                                .placeholder(R.drawable.ic_profile)
                                .error(R.drawable.ic_profile)
                                .into(imgProfile);
                    } else {
                        imgProfile.setImageResource(R.drawable.ic_profile);
                    }

                    fetchUserPosts(currentUserId);
                } else {
                    String errorMessage = "No error body";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (IOException e) {
                            Log.e("PROFILE", "Error reading error body: " + e.getMessage());
                            errorMessage = "Error reading error body";
                        }
                    }
                    Log.e("PROFILE", "Gagal memuat profil: " + response.code() + " - " + errorMessage);
                    Toast.makeText(ProfileActivity.this, "Gagal memuat profil", Toast.LENGTH_SHORT).show();
                    tokenManager.clearToken();
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Pengguna> call, Throwable t) {
                Log.e("PROFILE", "Fetch User Profile onFailure: " + t.getMessage());
                Toast.makeText(ProfileActivity.this, "Error koneksi saat memuat profil", Toast.LENGTH_SHORT).show();
                tokenManager.clearToken();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void fetchUserPosts(int userId) {
        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Anda belum login.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userId == -1) {
            Toast.makeText(this, "ID pengguna tidak ditemukan.", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getUserPostsById(userId, "Bearer " + token).enqueue(new Callback<List<Postingan>>() {
            @Override
            public void onResponse(Call<List<Postingan>> call, Response<List<Postingan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    postAdapter = new PostAdapter(response.body(), ProfileActivity.this);
                    recyclerUserPosts.setAdapter(postAdapter);
                    if (response.body().isEmpty()) {
                        Toast.makeText(ProfileActivity.this, "Belum ada postingan.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = "No error body";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (IOException e) {
                            Log.e("PROFILE", "Error reading error body: " + e.getMessage());
                            errorMessage = "Error reading error body";
                        }
                    }
                    Log.e("PROFILE", "Gagal memuat postingan pengguna: " + response.code() + " - " + errorMessage);
                    Toast.makeText(ProfileActivity.this, "Gagal memuat postingan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Postingan>> call, Throwable t) {
                Log.e("PROFILE", "Fetch User Posts onFailure: " + t.getMessage());
                Toast.makeText(ProfileActivity.this, "Error koneksi saat memuat postingan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadProfileImage(Uri imageUri) {
        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Anda belum login. Silakan login kembali.", Toast.LENGTH_SHORT).show();
            return;
        }

        String filePath = PathUtil.getPath(this, imageUri);
        if (filePath == null) {
            Toast.makeText(this, "Gagal mendapatkan jalur file gambar.", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(this, "File gambar tidak ditemukan.", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(imageUri)), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        Toast.makeText(this, "Mengunggah gambar profil...", Toast.LENGTH_SHORT).show();

        apiService.uploadProfilePicture(body, "Bearer " + token).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Gambar profil berhasil diunggah!", Toast.LENGTH_SHORT).show();
                    fetchUserProfile(); // Reload profile to get the updated URL
                } else {
                    String errorMessage = "Gagal mengunggah gambar profil. Code: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += " - " + response.errorBody().string();
                        } catch (IOException e) {
                            Log.e("PROFILE", "Error reading upload error body: " + e.getMessage());
                        }
                    }
                    Log.e("PROFILE", "Upload failed: " + errorMessage);
                    Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("PROFILE", "Upload onFailure: " + t.getMessage());
                Toast.makeText(ProfileActivity.this, "Error koneksi saat mengunggah gambar profil.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}