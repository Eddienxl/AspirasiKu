package com.pmob.aspirasiku.ui.profile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pmob.aspirasiku.R;
import com.pmob.aspirasiku.data.api.ApiService;
import com.pmob.aspirasiku.data.api.RetrofitClient;
import com.pmob.aspirasiku.data.model.UpdatePasswordRequest;
import com.pmob.aspirasiku.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText inputOldPassword, inputNewPassword;
    private Button btnSave;
    private ApiService apiService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        inputOldPassword = findViewById(R.id.inputOldPassword);
        inputNewPassword = findViewById(R.id.inputNewPassword);
        btnSave = findViewById(R.id.btnSave);

        apiService = RetrofitClient.getApiService();
        tokenManager = new TokenManager(this);

        btnSave.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String oldPassword = inputOldPassword.getText().toString().trim();
        String newPassword = inputNewPassword.getText().toString().trim();

        if (oldPassword.isEmpty()) {
            inputOldPassword.setError("Kata sandi lama tidak boleh kosong");
            return;
        }
        if (newPassword.isEmpty()) {
            inputNewPassword.setError("Kata sandi baru tidak boleh kosong");
            return;
        }

        UpdatePasswordRequest request = new UpdatePasswordRequest(oldPassword, newPassword);
        apiService.updatePassword(request, "Bearer " + tokenManager.getToken()).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this, "Kata sandi berhasil diubah", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Gagal mengubah kata sandi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this, "Error koneksi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}