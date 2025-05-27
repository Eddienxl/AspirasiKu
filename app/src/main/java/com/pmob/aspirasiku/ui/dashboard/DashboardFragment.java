package com.pmob.aspirasiku.ui.dashboard;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pmob.aspirasiku.R;
import com.pmob.aspirasiku.adapter.PostAdapter;
import com.pmob.aspirasiku.data.api.ApiService;
import com.pmob.aspirasiku.data.api.RetrofitClient;
import com.pmob.aspirasiku.data.model.Kategori;
import com.pmob.aspirasiku.data.model.Postingan;
import com.pmob.aspirasiku.ui.post.AddPostActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private Spinner spinnerSort, spinnerCategory;
    private SearchView searchView;
    private ApiService apiService;
    private List<Kategori> kategoriList = new ArrayList<>(); // Inisialisasi untuk menghindari NullPointerException
    private String selectedSort = "terbaru";
    private Integer selectedCategory = null;
    private String searchQuery = null;

    public DashboardFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        recyclerView = view.findViewById(R.id.recyclerPosts);
        spinnerSort = view.findViewById(R.id.spinnerSort);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        searchView = view.findViewById(R.id.searchView);
        FloatingActionButton fabAddPost = view.findViewById(R.id.fabAddPost);

        if (getContext() == null) { // Tambahan: Safety check untuk getContext()
            return view; // atau handle error
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        apiService = RetrofitClient.getApiService();

        // Setup SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query.isEmpty() ? null : query;
                fetchPosts();
                searchView.clearFocus(); // Opsional: Sembunyikan keyboard setelah submit
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Mengubah agar fetchPosts dipanggil hanya jika ada perubahan signifikan atau setelah delay
                // Untuk sekarang, kita biarkan seperti ini, tapi pertimbangkan debounce untuk performa
                searchQuery = newText.isEmpty() ? null : newText;
                fetchPosts();
                return true;
            }
        });

        // Setup spinner untuk sorting
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_options, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSort = position == 0 ? "terbaru" : "populer";
                fetchPosts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        fetchCategories(); // Setup spinner untuk kategori akan dipanggil di sini

        fabAddPost.setOnClickListener(v -> {
            if (getContext() != null) {
                Intent intent = new Intent(getContext(), AddPostActivity.class);
                startActivity(intent);
            }
        });

        fetchPosts(); // Panggilan awal untuk memuat postingan

        return view;
    }

    private void fetchCategories() {
        if (getContext() == null || apiService == null) return; // Safety check

        apiService.getKategori().enqueue(new Callback<List<Kategori>>() {
            @Override
            public void onResponse(Call<List<Kategori>> call, Response<List<Kategori>> response) {
                if (!isAdded() || getContext() == null) return; // Cek Fragment masih ter-attach

                if (response.isSuccessful() && response.body() != null) {
                    kategoriList = response.body();
                    setupCategorySpinner(kategoriList);
                } else {
                    // Data dummy untuk kategori jika API gagal
                    kategoriList = new ArrayList<>();
                    kategoriList.add(new Kategori(1, "Akademik"));
                    kategoriList.add(new Kategori(2, "Fasilitas"));
                    setupCategorySpinner(kategoriList);
                    Toast.makeText(getContext(), "Menggunakan data dummy untuk kategori", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Kategori>> call, Throwable t) {
                if (!isAdded() || getContext() == null) return; // Cek Fragment masih ter-attach

                Log.e("DASHBOARD", "Fetch Categories onFailure: " + t.getMessage());
                // Data dummy untuk kategori jika API gagal
                kategoriList = new ArrayList<>();
                kategoriList.add(new Kategori(1, "Akademik"));
                kategoriList.add(new Kategori(2, "Fasilitas"));
                setupCategorySpinner(kategoriList);
                Toast.makeText(getContext(), "Gagal memuat kategori, menggunakan data dummy", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCategorySpinner(List<Kategori> categories) {
        if (getContext() == null || spinnerCategory == null) return; // Safety check

        List<String> kategoriNames = new ArrayList<>();
        kategoriNames.add("Semua Kategori"); // Opsi default
        for (Kategori kategori : categories) {
            kategoriNames.add(kategori.getNama());
        }
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, kategoriNames);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedCategory = null; // "Semua Kategori" dipilih
                } else {
                    // Pastikan kategoriList tidak kosong dan position valid
                    if (kategoriList != null && !kategoriList.isEmpty() && (position - 1) < kategoriList.size()) {
                        selectedCategory = kategoriList.get(position - 1).getId();
                    } else {
                        selectedCategory = null; // Fallback jika ada masalah
                    }
                }
                fetchPosts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void fetchPosts() {
        if (getContext() == null || apiService == null) return; // Safety check

        apiService.getAllPosts(selectedSort, selectedCategory, searchQuery).enqueue(new Callback<List<Postingan>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<List<Postingan>> call, Response<List<Postingan>> response) {
                if (!isAdded() || getContext() == null) return; // Cek Fragment masih ter-attach

                if (response.isSuccessful() && response.body() != null) {
                    List<Postingan> posts = response.body();
                    // Lakukan filtering dan sorting di sisi client jika API tidak mendukungnya secara penuh
                    // atau jika Anda ingin konsistensi antara data live dan dummy.
                    // Untuk sekarang, kita asumsikan API sudah melakukan sort & filter
                    adapter = new PostAdapter(posts, getContext());
                    recyclerView.setAdapter(adapter);
                } else {
                    loadDummyPosts("Gagal memuat postingan dari server.");
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onFailure(Call<List<Postingan>> call, Throwable t) {
                if (!isAdded() || getContext() == null) return; // Cek Fragment masih ter-attach
                Log.e("DASHBOARD", "Fetch Posts onFailure: " + t.getMessage());
                loadDummyPosts(t.getMessage());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadDummyPosts(String reason) {
        if (getContext() == null) return;
        Log.d("DASHBOARD", "Memuat data dummy postingan karena: " + reason);

        List<Postingan> dummyPosts = new ArrayList<>();
        // PERBAIKAN: Hapus argumen integer terakhir
        dummyPosts.add(new Postingan(1, "Tanya Akademik", "Apa syarat kelulusan?", 1, "publik", null, 5));
        dummyPosts.add(new Postingan(2, "Fasilitas Rusak", "AC di kelas mati", 2, "publik", null, 3));
        dummyPosts.add(new Postingan(3, "Jadwal Akademik", "Kapan ujian?", 1, "publik", null, 7));

        // Filter berdasarkan kategori
        List<Postingan> filteredPosts = new ArrayList<>();
        if (selectedCategory == null) {
            filteredPosts.addAll(dummyPosts);
        } else {
            for (Postingan post : dummyPosts) {
                if (post.getId_kategori() == selectedCategory) {
                    filteredPosts.add(post);
                }
            }
        }

        // Filter berdasarkan pencarian
        if (searchQuery != null && !searchQuery.isEmpty()) {
            String queryLower = searchQuery.toLowerCase();
            // Menggunakan stream untuk filter, atau loop biasa juga bisa
            filteredPosts = filteredPosts.stream()
                    .filter(post -> post.getJudul().toLowerCase().contains(queryLower))
                    .collect(Collectors.toList());
        }

        // Sort berdasarkan terbaru atau populer
        if ("populer".equals(selectedSort)) {
            // Asumsi getUpvotes() ada di model Postingan
            Collections.sort(filteredPosts, (p1, p2) -> Integer.compare(p2.getUpvotes(), p1.getUpvotes()));
        } else {
            // Untuk "terbaru", jika ada timestamp, sort berdasarkan itu.
            // Jika tidak, urutan dummy saat ini mungkin sudah cukup.
            // Collections.sort(filteredPosts, (p1, p2) -> Integer.compare(p2.getId(), p1.getId())); // Contoh sort by ID desc
        }

        adapter = new PostAdapter(filteredPosts, getContext());
        recyclerView.setAdapter(adapter);
        Toast.makeText(getContext(), "Menggunakan data dummy untuk postingan", Toast.LENGTH_SHORT).show();
    }
}