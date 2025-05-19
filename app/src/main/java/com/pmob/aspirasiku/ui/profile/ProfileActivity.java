package com.pmob.aspirasiku.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pmob.aspirasiku.R;
import com.pmob.aspirasiku.adapter.PostAdapter;
import com.pmob.aspirasiku.data.api.ApiService;
import com.pmob.aspirasiku.data.api.RetrofitClient;
import com.pmob.aspirasiku.data.model.Pengguna;
import com.pmob.aspirasiku.data.model.Postingan;
import com.pmob.aspirasiku.ui.detail.PostDetailActivity;
import com.pmob.aspirasiku.utils.TokenManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView txtNama, txtNim, txtEmail, txtPeran;
    private Button btnEditProfile, btnChangePassword;
    private RecyclerView recyclerUserPosts;
    private ApiService apiService;
    private TokenManager tokenManager;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txtNama = findViewById(R.id.txtNama);
        txtNim = findViewById(R.id.txtNim);
        txtEmail = findViewById(R.id.txtEmail);
        txtPeran = findViewById(R.id.txtPeran);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        recyclerUserPosts = findViewById(R.id.recyclerUserPosts);

        apiService = RetrofitClient.getApiService();
        tokenManager = new TokenManager(this);

        recyclerUserPosts.setLayoutManager(new LinearLayoutManager(this));

        fetchUserProfile();
        fetchUserPosts();

        btnEditProfile.setOnClickListener(v -> {
            // Navigasi ke EditProfileActivity (akan dibuat di langkah berikutnya)
            startActivity(new Intent(this, EditProfileActivity.class));
        });

        btnChangePassword.setOnClickListener(v -> {
            // Navigasi ke ChangePasswordActivity (akan dibuat di langkah berikutnya)
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });
    }

    private void fetchUserProfile() {
        apiService.getUserProfile("Bearer " + tokenManager.getToken()).enqueue(new Callback<Pengguna>() {
            @Override
            public void onResponse(Call<Pengguna> call, Response<Pengguna> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Pengguna pengguna = response.body();
                    txtNama.setText("Nama: " + pengguna.getNama());
                    txtNim.setText("NIM: " + pengguna.getNim());
                    txtEmail.setText("Email: " + pengguna.getEmail());
                    txtPeran.setText("Peran: " + pengguna.getPeran());
                } else {
                    Toast.makeText(ProfileActivity.this, "Gagal memuat profil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Pengguna> call, Throwable t) {
                Log.e("PROFILE", "onFailure: " + t.getMessage());
                Toast.makeText(ProfileActivity.this, "Error koneksi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserPosts() {
        apiService.getUserPosts("Bearer " + tokenManager.getToken()).enqueue(new Callback<List<Postingan>>() {
            @Override
            public void onResponse(Call<List<Postingan>> call, Response<List<Postingan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    postAdapter = new PostAdapter(response.body(), ProfileActivity.this);
                    recyclerUserPosts.setAdapter(postAdapter);
                } else {
                    Toast.makeText(ProfileActivity.this, "Gagal memuat postingan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Postingan>> call, Throwable t) {
                Log.e("PROFILE", "onFailure: " + t.getMessage());
                Toast.makeText(ProfileActivity.this, "Error koneksi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}