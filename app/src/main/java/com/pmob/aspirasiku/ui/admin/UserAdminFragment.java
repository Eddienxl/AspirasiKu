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
import com.pmob.aspirasiku.adapter.UserAdminAdapter;
import com.pmob.aspirasiku.data.api.ApiService;
import com.pmob.aspirasiku.data.api.RetrofitClient;
import com.pmob.aspirasiku.data.model.Pengguna;
import com.pmob.aspirasiku.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAdminFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdminAdapter adapter;
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

        fetchUsers();

        return view;
    }

    private void fetchUsers() {
        apiService.getAllUsers("Bearer " + tokenManager.getToken()).enqueue(new Callback<List<Pengguna>>() {
            @Override
            public void onResponse(Call<List<Pengguna>> call, Response<List<Pengguna>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new UserAdminAdapter(response.body(), userId -> {
                        deleteUser(userId);
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    // Data dummy jika API gagal
                    List<Pengguna> dummyUsers = new ArrayList<>();
                    dummyUsers.add(new Pengguna(1, "User 1", "12345", "user1@example.com", "user"));
                    dummyUsers.add(new Pengguna(2, "User 2", "67890", "user2@example.com", "user"));
                    adapter = new UserAdminAdapter(dummyUsers, userId -> {
                        dummyUsers.removeIf(user -> user.getId() == userId);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Pengguna dihapus (dummy)", Toast.LENGTH_SHORT).show();
                    });
                    recyclerView.setAdapter(adapter);
                    Toast.makeText(getContext(), "Menggunakan data dummy untuk pengguna", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Pengguna>> call, Throwable t) {
                Log.e("ADMIN_USER", "onFailure: " + t.getMessage());
                // Data dummy jika API gagal
                List<Pengguna> dummyUsers = new ArrayList<>();
                dummyUsers.add(new Pengguna(1, "User 1", "12345", "user1@example.com", "user"));
                dummyUsers.add(new Pengguna(2, "User 2", "67890", "user2@example.com", "user"));
                adapter = new UserAdminAdapter(dummyUsers, userId -> {
                    dummyUsers.removeIf(user -> user.getId() == userId);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Pengguna dihapus (dummy)", Toast.LENGTH_SHORT).show();
                });
                recyclerView.setAdapter(adapter);
                Toast.makeText(getContext(), "Menggunakan data dummy untuk pengguna", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUser(int userId) {
        apiService.deleteUser(userId, "Bearer " + tokenManager.getToken()).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    fetchUsers(); // Refresh daftar
                    Toast.makeText(getContext(), "Pengguna dihapus", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Gagal menghapus pengguna", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("ADMIN_USER", "onFailure: " + t.getMessage());
                Toast.makeText(getContext(), "Error koneksi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}