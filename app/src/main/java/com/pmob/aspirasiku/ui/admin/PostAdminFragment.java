package com.pmob.aspirasiku.ui.admin;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pmob.aspirasiku.R;
import com.pmob.aspirasiku.adapter.PostAdminAdapter;
import com.pmob.aspirasiku.data.api.ApiService;
import com.pmob.aspirasiku.data.api.RetrofitClient;
import com.pmob.aspirasiku.data.model.Postingan;
import com.pmob.aspirasiku.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdminFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdminAdapter adapter;
    private ApiService apiService;
    private TokenManager tokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerAdminList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        apiService = RetrofitClient.getApiService();
        tokenManager = new TokenManager(getContext()); // Pastikan getContext() aman di sini

        fetchPosts();

        return view;
    }

    private void fetchPosts() {
        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Token tidak valid, silakan login ulang.", Toast.LENGTH_LONG).show();
            // Mungkin arahkan ke login atau handle kasus token tidak ada
            loadDummyData("Token tidak valid");
            return;
        }

        apiService.getAllAdminPosts("Bearer " + token).enqueue(new Callback<List<Postingan>>() {
            @Override
            public void onResponse(Call<List<Postingan>> call, Response<List<Postingan>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) { // Tambahkan isAdded()
                    adapter = new PostAdminAdapter(response.body(), postId -> {
                        deletePost(postId);
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    if (isAdded()) {
                        loadDummyData("Gagal memuat postingan dari server.");
                        Toast.makeText(getContext(), "Menggunakan data dummy untuk postingan", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Postingan>> call, Throwable t) {
                if (isAdded()) { // Tambahkan isAdded()
                    Log.e("ADMIN_POST", "onFailure: " + t.getMessage());
                    loadDummyData(t.getMessage());
                    Toast.makeText(getContext(), "Menggunakan data dummy untuk postingan (onFailure)", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadDummyData(String reason) {
        if (!isAdded()) return; // Cek lagi sebelum memanipulasi UI atau context

        Log.d("ADMIN_POST", "Memuat data dummy karena: " + reason);
        List<Postingan> dummyPosts = new ArrayList<>();
        // Hapus argumen ke-8 (integer terakhir)
        dummyPosts.add(new Postingan(1, "Postingan Dummy 1", "Konten Dummy 1", 1, "publik", null, 5));
        dummyPosts.add(new Postingan(2, "Postingan Dummy 2", "Konten Dummy 2", 2, "publik", null, 3));

        adapter = new PostAdminAdapter(dummyPosts, postId -> {
            if (!isAdded()) return;
            // Hati-hati memodifikasi list yang sama yang digunakan adapter
            // Sebaiknya buat list baru atau gunakan filter yang mengembalikan list baru
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dummyPosts.removeIf(post -> post.getId() == postId);
            }
            // Untuk refresh yang lebih aman, berikan list baru ke adapter
            // atau implementasikan DiffUtil jika list sangat besar
            adapter.updateData(new ArrayList<>(dummyPosts)); // Asumsi ada method updateData di adapter
            // adapter.notifyDataSetChanged(); // Ini kurang efisien
            Toast.makeText(getContext(), "Postingan dummy dihapus", Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);
    }


    private void deletePost(int postId) {
        if (!isAdded()) return; // Cek sebelum melakukan network call atau update UI

        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Token tidak valid untuk menghapus.", Toast.LENGTH_LONG).show();
            return;
        }

        apiService.deletePost(postId, "Bearer " + token).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (isAdded()) { // Tambahkan isAdded()
                    if (response.isSuccessful()) {
                        fetchPosts(); // Refresh daftar
                        Toast.makeText(getContext(), "Postingan dihapus", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Gagal menghapus postingan, server response: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                if (isAdded()) { // Tambahkan isAdded()
                    Log.e("ADMIN_POST", "onFailure deletePost: " + t.getMessage());
                    Toast.makeText(getContext(), "Error koneksi saat menghapus", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}