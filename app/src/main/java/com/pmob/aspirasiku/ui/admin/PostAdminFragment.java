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

        if (getContext() == null) {
            return view; // Safety check
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        apiService = RetrofitClient.getApiService();
        tokenManager = new TokenManager(getContext());

        fetchPosts();

        return view;
    }

    private void fetchPosts() {
        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            if (isAdded()) { // Pastikan Fragment masih ter-attach sebelum menampilkan Toast
                Toast.makeText(getContext(), "Token tidak valid, silakan login ulang.", Toast.LENGTH_LONG).show();
            }
            loadDummyData("Token tidak valid");
            return;
        }

        // PERBAIKAN: Menggunakan getAllPosts() karena getAllAdminPosts() sudah tidak ada
        // Anda mungkin perlu menyesuaikan parameter sort, kategoriId, search sesuai kebutuhan admin
        apiService.getAllPosts(null, null, null).enqueue(new Callback<List<Postingan>>() {
            @Override
            public void onResponse(Call<List<Postingan>> call, Response<List<Postingan>> response) {
                if (!isAdded() || getContext() == null) return; // Fragment not attached or context null

                if (response.isSuccessful() && response.body() != null) {
                    adapter = new PostAdminAdapter(response.body(), postId -> {
                        deletePost(postId);
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.e("ADMIN_POST", "Failed to fetch posts: " + response.code() + " - " + (response.errorBody() != null ? response.errorBody().toString() : "No error body"));
                    loadDummyData("Gagal memuat postingan dari server.");
                    Toast.makeText(getContext(), "Menggunakan data dummy untuk postingan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Postingan>> call, Throwable t) {
                if (!isAdded() || getContext() == null) return; // Fragment not attached or context null

                Log.e("ADMIN_POST", "onFailure fetchPosts: " + t.getMessage());
                loadDummyData(t.getMessage());
                Toast.makeText(getContext(), "Menggunakan data dummy untuk postingan (onFailure)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDummyData(String reason) {
        if (!isAdded() || getContext() == null) return; // Fragment not attached or context null

        Log.d("ADMIN_POST", "Memuat data dummy karena: " + reason);
        List<Postingan> dummyPosts = new ArrayList<>();
        // Pastikan constructor Postingan sesuai (sekarang 9 argumen: id, judul, konten, id_kategori, status, created_at, upvotes, downvotes, List<Komentar>)
        dummyPosts.add(new Postingan(1, "Postingan Dummy 1", "Konten Dummy 1", 1, "publik", "2024-05-30T10:00:00Z", 5, 2, new ArrayList<>()));
        dummyPosts.add(new Postingan(2, "Postingan Dummy 2", "Konten Dummy 2", 2, "publik", "2024-05-30T11:00:00Z", 3, 1, new ArrayList<>()));

        adapter = new PostAdminAdapter(dummyPosts, postId -> {
            if (!isAdded() || getContext() == null) return; // Fragment not attached or context null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dummyPosts.removeIf(post -> post.getId() == postId);
            } else {
                for (int i = dummyPosts.size() - 1; i >= 0; i--) {
                    if (dummyPosts.get(i).getId() == postId) {
                        dummyPosts.remove(i);
                        break;
                    }
                }
            }
            // Untuk refresh yang lebih aman, berikan list baru ke adapter
            // atau implementasikan DiffUtil jika list sangat besar
            // Asumsi adapter.updateData() ada
            adapter.updateData(new ArrayList<>(dummyPosts));
            Toast.makeText(getContext(), "Postingan dummy dihapus", Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);
    }


    private void deletePost(int postId) {
        if (!isAdded() || getContext() == null) return; // Fragment not attached or context null

        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Token tidak valid untuk menghapus.", Toast.LENGTH_LONG).show();
            return;
        }

        apiService.deletePost(postId, "Bearer " + token).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (!isAdded() || getContext() == null) return; // Fragment not attached or context null

                if (response.isSuccessful()) {
                    fetchPosts(); // Refresh daftar
                    Toast.makeText(getContext(), "Postingan dihapus", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("ADMIN_POST", "Gagal menghapus postingan: " + response.code() + " - " + (response.errorBody() != null ? response.errorBody().toString() : "No error body"));
                    Toast.makeText(getContext(), "Gagal menghapus postingan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                if (!isAdded() || getContext() == null) return; // Fragment not attached or context null
                Log.e("ADMIN_POST", "onFailure deletePost: " + t.getMessage());
                Toast.makeText(getContext(), "Error koneksi saat menghapus", Toast.LENGTH_SHORT).show();
            }
        });
    }
}