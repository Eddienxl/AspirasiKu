package com.pmob.aspirasiku.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.pmob.aspirasiku.R;
import com.pmob.aspirasiku.data.api.ApiService;
import com.pmob.aspirasiku.data.api.RetrofitClient;
import com.pmob.aspirasiku.data.model.Pengguna;
import com.pmob.aspirasiku.ui.admin.AdminActivity;
import com.pmob.aspirasiku.ui.notification.NotificationActivity;
import com.pmob.aspirasiku.ui.profile.ProfileActivity;
import com.pmob.aspirasiku.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ApiService apiService;
    private TokenManager tokenManager;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        apiService = RetrofitClient.getApiService();
        tokenManager = new TokenManager(this);

        // Cek apakah pengguna adalah admin
        fetchUserProfile();
    }

    private void fetchUserProfile() {
        apiService.getUserProfile("Bearer " + tokenManager.getToken()).enqueue(new Callback<Pengguna>() {
            @Override
            public void onResponse(Call<Pengguna> call, Response<Pengguna> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isAdmin = "pengelola".equals(response.body().getPeran());
                    invalidateOptionsMenu(); // Refresh menu
                }
            }

            @Override
            public void onFailure(Call<Pengguna> call, Throwable t) {
                isAdmin = false; // Default ke false jika gagal
                invalidateOptionsMenu();
            }
        });
    }

    @SuppressLint("ResourceType") // Tetap perhatikan penggunaan @SuppressLint ini. ResourceType biasanya untuk inflater jika ID layout tidak konstan.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.layout.menu_profile, menu); // Diperbaiki ke R.menu
        if (menu.findItem(R.id.action_admin) != null) { // Tambahkan null check untuk keamanan
            menu.findItem(R.id.action_admin).setVisible(isAdmin); // Hanya tampilkan untuk admin/pengelola
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_notification) {
            startActivity(new Intent(this, NotificationActivity.class));
            return true;
        } else if (id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        } else if (id == R.id.action_admin) {
            if (isAdmin) { // Sebaiknya tambahkan pengecekan isAdmin di sini juga
                startActivity(new Intent(this, AdminActivity.class));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
} // Baris tambahan dan error di akhir file telah dihapus