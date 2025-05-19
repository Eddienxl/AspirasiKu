package com.pmob.aspirasiku.ui.dashboard;

import android.content.Intent;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pmob.aspirasiku.R;
import com.pmob.aspirasiku.adapter.PostAdapter;
import com.pmob.aspirasiku.data.api.ApiService;
import com.pmob.aspirasiku.data.api.RetrofitClient;
import com.pmob.aspirasiku.data.model.Postingan;
import com.pmob.aspirasiku.ui.post.AddPostActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private ApiService apiService;
    private FloatingActionButton fabAddPost;

    public DashboardFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        recyclerView = view.findViewById(R.id.recyclerPosts);
        fabAddPost = view.findViewById(R.id.fabAddPost);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        apiService = RetrofitClient.getApiService();

        fabAddPost.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddPostActivity.class);
            startActivity(intent);
        });

        fetchPosts();

        return view;
    }

    private void fetchPosts() {
        apiService.getAllPosts().enqueue(new Callback<List<Postingan>>() {
            @Override
            public void onResponse(Call<List<Postingan>> call, Response<List<Postingan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new PostAdapter(response.body(), getContext());
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Gagal ambil data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Postingan>> call, Throwable t) {
                Log.e("DASHBOARD", "onFailure: " + t.getMessage());
                Toast.makeText(getContext(), "Error koneksi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}