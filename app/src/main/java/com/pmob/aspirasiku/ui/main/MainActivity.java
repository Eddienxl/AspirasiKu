package com.pmob.aspirasiku.ui.main;

import android.content.Intent;
import android.os.Bundle;
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

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        menu.findItem(R.id.action_admin).setVisible(isAdmin); // Hanya tampilkan untuk pengelola
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
            startActivity(new Intent(this, AdminActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}