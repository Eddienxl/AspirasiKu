package com.pmob.aspirasiku.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pmob.aspirasiku.R;
import com.pmob.aspirasiku.data.api.ApiService;
import com.pmob.aspirasiku.data.api.RetrofitClient;
import com.pmob.aspirasiku.data.model.AuthResponse;
import com.pmob.aspirasiku.data.model.LoginRequest;
import com.pmob.aspirasiku.ui.main.MainActivity;
import com.pmob.aspirasiku.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button btnLogin;
    private ApiService apiService;
    private TokenManager tokenManager;

    // Tambahkan email untuk login dummy
    private static final String DUMMY_EMAIL = "dummy@example.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailEditText);
        passwordInput = findViewById(R.id.passwordEditText);
        btnLogin = findViewById(R.id.loginButton);
        apiService = RetrofitClient.getApiService();
        tokenManager = new TokenManager(this);

        // Cek apakah sudah ada token, jika ya, langsung ke MainActivity
        // (Ini opsional, tapi praktik yang baik)
        if (tokenManager.getToken() != null && !tokenManager.getToken().isEmpty()) {
            // Anda bisa menambahkan validasi token di sini jika perlu (misalnya, cek kedaluwarsa)
            // Untuk sekarang, kita anggap token yang ada masih valid.
            // Jika token adalah token dummy, itu juga akan dianggap valid di sini.
            Toast.makeText(LoginActivity.this, "Anda sudah login.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return; // Keluar dari onCreate agar tidak melanjutkan setup login
        }


        btnLogin.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, password);
        });
    }

    private void loginUser(String email, String password) {
        // --- MODIFIKASI UNTUK LOGIN DUMMY DIMULAI DI SINI ---
        if (DUMMY_EMAIL.equals(email)) {
            // Password dummy bisa diabaikan atau dicek jika mau
            // if ("dummy123".equals(password)) { // Opsional: cek password dummy juga
            Log.d("LOGIN_DUMMY", "Login dummy berhasil untuk: " + email);
            tokenManager.saveToken("dummy_token_karena_login_dummy"); // Simpan token dummy
            Toast.makeText(LoginActivity.this, "Login Dummy Berhasil!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return; // Hentikan eksekusi lebih lanjut agar tidak memanggil API
            // } else {
            //     Toast.makeText(LoginActivity.this, "Password dummy salah", Toast.LENGTH_SHORT).show();
            //     return;
            // }
        }
        // --- MODIFIKASI UNTUK LOGIN DUMMY SELESAI ---

        // Proses login normal menggunakan API jika bukan email dummy
        LoginRequest request = new LoginRequest(email, password);
        apiService.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getToken() != null) {
                    tokenManager.saveToken(response.body().getToken());
                    Toast.makeText(LoginActivity.this, "Login Berhasil!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    // Tangani error dari server dengan lebih spesifik jika memungkinkan
                    String errorMessage = "Login gagal, cek email/sandi";
                    if (response.errorBody() != null) {
                        try {

                            Log.e("LOGIN_API", "Error: " + response.code() + " - " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("LOGIN_API", "Error parsing error body: ", e);
                        }
                    } else if (response.body() != null && response.body().getToken() == null) {
                        errorMessage = "Login berhasil tetapi tidak ada token diterima.";
                        Log.e("LOGIN_API", errorMessage);
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e("LOGIN_API", "onFailure: " + t.getMessage(), t);
                Toast.makeText(LoginActivity.this, "Error koneksi atau server tidak merespon", Toast.LENGTH_LONG).show();
            }
        });
    }
}