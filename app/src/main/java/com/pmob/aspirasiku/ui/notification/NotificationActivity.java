package com.pmob.aspirasiku.ui.notification;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pmob.aspirasiku.R;
import com.pmob.aspirasiku.adapter.NotificationAdapter;
import com.pmob.aspirasiku.data.api.ApiService;
import com.pmob.aspirasiku.data.api.RetrofitClient;
import com.pmob.aspirasiku.data.model.Notifikasi;
import com.pmob.aspirasiku.utils.TokenManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerNotifications;
    private NotificationAdapter adapter;
    private ApiService apiService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerNotifications = findViewById(R.id.recyclerNotifications);
        recyclerNotifications.setLayoutManager(new LinearLayoutManager(this));

        apiService = RetrofitClient.getApiService();
        tokenManager = new TokenManager(this);

        fetchNotifications();
    }

    private void fetchNotifications() {
        apiService.getNotifications("Bearer " + tokenManager.getToken()).enqueue(new Callback<List<Notifikasi>>() {
            @Override
            public void onResponse(Call<List<Notifikasi>> call, Response<List<Notifikasi>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new NotificationAdapter(response.body(), NotificationActivity.this);
                    recyclerNotifications.setAdapter(adapter);
                } else {
                    Toast.makeText(NotificationActivity.this, "Gagal memuat notifikasi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Notifikasi>> call, Throwable t) {
                Log.e("NOTIFICATION", "onFailure: " + t.getMessage());
                Toast.makeText(NotificationActivity.this, "Error koneksi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}