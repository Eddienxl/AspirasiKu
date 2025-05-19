package com.pmob.aspirasiku.ui.admin;

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
        tokenManager = new TokenManager(getContext());

        fetchPosts();

        return view;
    }

    private void fetchPosts() {
        apiService.getAllAdminPosts("Bearer " + tokenManager.getToken()).enqueue(new Callback<List<Postingan>>() {
            @Override
            public void onResponse(Call<List<Postingan>> call, Response<List<Postingan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new PostAdminAdapter(response.body(), postId -> {
                        deletePost(postId);
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    // Data dummy jika API gagal
                    List<Postingan> dummyPosts = new ArrayList<>();
                    dummyPosts.add(new Postingan(1, "Postingan 1", "Konten 1", 1, "publik", null, 5, 2));
                    dummyPosts.add(new Postingan(2, "Postingan çe", "Konten 2", 2, "publik", null, 3, 1));
                    adapter = new PostAdminAdapter(dummyPosts, postId -> {
                        dummyPosts.removeIf(post -> post.getId() == postId);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Postingan dihapus (dummy)", Toast.LENGTH_SHORT).show();
                    });
                    recyclerView.setAdapter(adapter);
                    Toast.makeText(getContext(), "Menggunakan data dummy untuk postingan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Postingan>> call, Throwable t) {
                Log.e("ADMIN_POST", "onFailure: " + t.getMessage());
                // Data dummy jika API gagal
                List<Postingan> dummyPosts = new ArrayList<>();
                dummyPosts.add(new Postingan(1, "Postingan 1", "Konten 1", 1, "publik", null, 5, 2));
                dummyPosts.add(new Postingan(2, "Postingan 2", "Konten 2", 2, "publik", null, 3, 1));
                adapter = new PostAdminAdapter(dummyPosts, postId -> {
                    dummyPosts.removeIf(post -> post.getId() == postId);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Postingan dihapus (dummy)", Toast.LENGTH_SHORT).show();
                });
                recyclerView.setAdapter(adapter);
                Toast.makeText(getContext(), "Menggunakan data dummy untuk postingan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deletePost(int postId) {
        apiService.deletePost(postId, "Bearer " + tokenManager.getToken()).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    fetchPosts(); // Refresh daftar
                    Toast.makeText(getContext(), "Postingan dihapus", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Gagal menghapus postingan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("ADMIN_POST", "onFailure: " + t.getMessage());
                Toast.makeText(getContext(), "Error koneksi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}