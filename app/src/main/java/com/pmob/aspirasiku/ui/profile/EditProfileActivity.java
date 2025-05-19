package com.pmob.aspirasiku.ui.profile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pmob.aspirasiku.R;
import com.pmob.aspirasiku.data.api.ApiService;
import com.pmob.aspirasiku.data.api.RetrofitClient;
import com.pmob.aspirasiku.data.model.Pengguna;
import com.pmob.aspirasiku.data.model.UpdateProfileRequest;
import com.pmob.aspirasiku.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private EditText inputNama, inputEmail;
    private Button btnSave;
    private ApiService apiService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        inputNama = findViewById(R.id.inputNama);
        inputEmail = findViewById(R.id.inputEmail);
        btnSave = findViewById(R.id.btnSave);

        apiService = RetrofitClient.getApiService();
        tokenManager = new TokenManager(this);

        fetchCurrentProfile();

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void fetchCurrentProfile() {
        apiService.getUserProfile("Bearer " + tokenManager.getToken()).enqueue(new Callback<Pengguna>() {
            @Override
            public void onResponse(Call<Pengguna> call, Response<Pengguna> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Pengguna pengguna = response.body();
                    inputNama.setText(pengguna.getNama());
                    inputEmail.setText(pengguna.getEmail());
                }
            }

            @Override
            public void onFailure(Call<Pengguna> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        String nama = inputNama.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();

        if (nama.isEmpty()) {
            inputNama.setError("Nama tidak boleh kosong");
            return;
        }
        if (email.isEmpty()) {
            inputEmail.setError("Email tidak boleh kosong");
            return;
        }

        UpdateProfileRequest request = new UpdateProfileRequest(nama, email);
        apiService.updateUserProfile(request, "Bearer " + tokenManager.getToken()).enqueue(new Callback<Pengguna>() {
            @Override
            public void onResponse(Call<Pengguna> call, Response<Pengguna> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Pengguna> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Error koneksi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}