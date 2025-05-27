package com.pmob.aspirasiku.ui.admin;

import android.os.Build; // Dihapus jika @RequiresApi tidak diperlukan lagi
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
// import androidx.annotation.RequiresApi; // Dihapus jika tidak ada lagi pemanggilan API level spesifik di callback
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

        if (getContext() == null) {
            return view; // Safety check
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        apiService = RetrofitClient.getApiService();
        tokenManager = new TokenManager(getContext());

        fetchUsers();

        return view;
    }

    private void fetchUsers() {
        if (tokenManager == null || apiService == null || getContext() == null) return; // Safety check

        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            if (isAdded()) {
                Toast.makeText(getContext(), "Token tidak valid.", Toast.LENGTH_SHORT).show();
                loadDummyUsers("Token tidak valid");
            }
            return;
        }

        apiService.getAllUsers("Bearer " + token).enqueue(new Callback<List<Pengguna>>() {
            // @RequiresApi(api = Build.VERSION_CODES.N) // Hapus jika removeIf tidak digunakan atau minSdk N
            @Override
            public void onResponse(Call<List<Pengguna>> call, Response<List<Pengguna>> response) {
                if (!isAdded() || getContext() == null) return; // Fragment not attached

                if (response.isSuccessful() && response.body() != null) {
                    adapter = new UserAdminAdapter(response.body(), userId -> {
                        deleteUser(userId);
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.e("ADMIN_USER", "Failed to fetch users: " + response.code());
                    loadDummyUsers("Gagal memuat pengguna dari server");
                }
            }

            // @RequiresApi(api = Build.VERSION_CODES.N) // Hapus jika removeIf tidak digunakan atau minSdk N
            @Override
            public void onFailure(Call<List<Pengguna>> call, Throwable t) {
                if (!isAdded() || getContext() == null) return; // Fragment not attached

                Log.e("ADMIN_USER", "onFailure fetchUsers: " + t.getMessage());
                loadDummyUsers(t.getMessage());
            }
        });
    }

    // @RequiresApi(api = Build.VERSION_CODES.N) // Hapus jika removeIf tidak digunakan atau minSdk N
    private void loadDummyUsers(String reason) {
        if (!isAdded() || getContext() == null) return; // Fragment not attached
        Log.d("ADMIN_USER", "Memuat data dummy pengguna karena: " + reason);

        List<Pengguna> dummyUsers = new ArrayList<>();
        // PERBAIKAN: Tambahkan argumen password dummy (argumen ke-5)
        dummyUsers.add(new Pengguna(1, "User Dummy 1", "12345", "user1@example.com", "password123", "pengguna"));
        dummyUsers.add(new Pengguna(2, "User Dummy 2", "67890", "user2@example.com", "password456", "pengguna"));

        adapter = new UserAdminAdapter(dummyUsers, userId -> {
            if (!isAdded() || getContext() == null) return; // Fragment not attached

            // Hati-hati dengan removeIf jika minSdk < 24 tanpa desugaring
            // Alternatif manual loop untuk kompatibilitas lebih luas jika diperlukan
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dummyUsers.removeIf(user -> user.getId() == userId);
            } else {
                for (int i = dummyUsers.size() - 1; i >= 0; i--) {
                    if (dummyUsers.get(i).getId() == userId) {
                        dummyUsers.remove(i);
                        break;
                    }
                }
            }
            adapter.notifyDataSetChanged(); // Pertimbangkan DiffUtil untuk performa lebih baik
            Toast.makeText(getContext(), "Pengguna dummy dihapus", Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);
        Toast.makeText(getContext(), "Menggunakan data dummy untuk pengguna", Toast.LENGTH_SHORT).show();
    }


    private void deleteUser(int userId) {
        if (tokenManager == null || apiService == null || getContext() == null) return; // Safety check
        if (!isAdded()) return; // Fragment not attached

        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Token tidak valid untuk menghapus.", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.deleteUser(userId, "Bearer " + token).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (!isAdded() || getContext() == null) return; // Fragment not attached

                if (response.isSuccessful()) {
                    fetchUsers(); // Refresh daftar
                    Toast.makeText(getContext(), "Pengguna berhasil dihapus", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("ADMIN_USER", "Gagal menghapus pengguna: " + response.code());
                    Toast.makeText(getContext(), "Gagal menghapus pengguna", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                if (!isAdded() || getContext() == null) return; // Fragment not attached
                Log.e("ADMIN_USER", "onFailure deleteUser: " + t.getMessage());
                Toast.makeText(getContext(), "Error koneksi saat menghapus", Toast.LENGTH_SHORT).show();
            }
        });
    }
}