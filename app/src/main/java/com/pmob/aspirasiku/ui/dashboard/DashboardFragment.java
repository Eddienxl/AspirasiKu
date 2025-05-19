package com.pmob.aspirasiku.ui.dashboard;

import android.content.Intent;
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
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private Spinner spinnerSort, spinnerCategory;
    private ApiService apiService;
    private List<Kategori> kategoriList;
    private String selectedSort = "terbaru";
    private Integer selectedCategory = null;

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
        FloatingActionButton fabAddPost = view.findViewById(R.id.fabAddPost);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        apiService = RetrofitClient.getApiService();

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

        // Setup spinner untuk kategori
        fetchCategories();

        fabAddPost.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddPostActivity.class);
            startActivity(intent);
        });

        fetchPosts();

        return view;
    }

    private void fetchCategories() {
        apiService.getKategori().enqueue(new Callback<List<Kategori>>() {
            @Override
            public void onResponse(Call<List<Kategori>> call, Response<List<Kategori>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    kategoriList = response.body();
                    List<String> kategoriNames = new ArrayList<>();
                    kategoriNames.add("Semua Kategori");
                    for (Kategori kategori : kategoriList) {
                        kategoriNames.add(kategori.getNama());
                    }
                    ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, kategoriNames);
                    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(categoryAdapter);
                    spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedCategory = position == 0 ? null : kategoriList.get(position - 1).getId();
                            fetchPosts();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                } else {
                    // Data dummy untuk kategori jika API gagal
                    kategoriList = new ArrayList<>();
                    kategoriList.add(new Kategori(1, "Akademik"));
                    kategoriList.add(new Kategori(2, "Fasilitas"));
                    List<String> kategoriNames = new ArrayList<>();
                    kategoriNames.add("Semua Kategori");
                    for (Kategori kategori : kategoriList) {
                        kategoriNames.add(kategori.getNama());
                    }
                    ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, kategoriNames);
                    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(categoryAdapter);
                    spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedCategory = position == 0 ? null : kategoriList.get(position - 1).getId();
                            fetchPosts();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                    Toast.makeText(getContext(), "Menggunakan data dummy untuk kategori", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Kategori>> call, Throwable t) {
                Log.e("DASHBOARD", "onFailure: " + t.getMessage());
                // Data dummy untuk kategori jika API gagal
                kategoriList = new ArrayList<>();
                kategoriList.add(new Kategori(1, "Akademik"));
                kategoriList.add(new Kategori(2, "Fasilitas"));
                List<String> kategoriNames = new ArrayList<>();
                kategoriNames.add("Semua Kategori");
                for (Kategori kategori : kategoriList) {
                    kategoriNames.add(kategori.getNama());
                }
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_item, kategoriNames);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(categoryAdapter);
                spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedCategory = position == 0 ? null : kategoriList.get(position - 1).getId();
                        fetchPosts();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
                Toast.makeText(getContext(), "Menggunakan data dummy untuk kategori", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPosts() {
        apiService.getAllPosts(selectedSort, selectedCategory).enqueue(new Callback<List<Postingan>>() {
            @Override
            public void onResponse(Call<List<Postingan>> call, Response<List<Postingan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new PostAdapter(response.body(), getContext());
                    recyclerView.setAdapter(adapter);
                } else {
                    // Data dummy jika API gagal
                    List<Postingan> dummyPosts = new ArrayList<>();
                    dummyPosts.add(new Postingan(1, "Postingan 1", "Konten 1", 1, "publik", null, 5, 2));
                    dummyPosts.add(new Postingan(2, "Postingan 2", "Konten 2", 2, "publik", null, 3, 1));
                    dummyPosts.add(new Postingan(3, "Postingan 3", "Konten 3", 1, "publik", null, 7, 0));

                    // Filter berdasarkan kategori
                    List<Postingan> filteredPosts = new ArrayList<>();
                    for (Postingan post : dummyPosts) {
                        if (selectedCategory == null || post.getId_kategori() == selectedCategory) {
                            filteredPosts.add(post);
                        }
                    }

                    // Sort berdasarkan terbaru atau populer
                    if ("populer".equals(selectedSort)) {
                        Collections.sort(filteredPosts, (p1, p2) -> p2.getUpvotes() - p1.getUpvotes());
                    }

                    adapter = new PostAdapter(filteredPosts, getContext());
                    recyclerView.setAdapter(adapter);
                    Toast.makeText(getContext(), "Menggunakan data dummy untuk postingan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Postingan>> call, Throwable t) {
                Log.e("DASHBOARD", "onFailure: " + t.getMessage());
                // Data dummy jika API gagal
                List<Postingan> dummyPosts = new ArrayList<>();
                dummyPosts.add(new Postingan(1, "Postingan 1", "Konten 1", 1, "publik", null, 5, 2));
                dummyPosts.add(new Postingan(2, "Postingan 2", "Konten 2", 2, "publik", null, 3, 1));
                dummyPosts.add(new Postingan(3, "Postingan 3", "Konten 3", 1, "publik", null, 7, 0));

                // Filter berdasarkan kategori
                List<Postingan> filteredPosts = new ArrayList<>();
                for (Postingan post : dummyPosts) {
                    if (selectedCategory == null || post.getId_kategori() == selectedCategory) {
                        filteredPosts.add(post);
                    }
                }

                // Sort berdasarkan terbaru atau populer
                if ("populer".equals(selectedSort)) {
                    Collections.sort(filteredPosts, (p1, p2) -> p2.getUpvotes() - p1.getUpvotes());
                }

                adapter = new PostAdapter(filteredPosts, getContext());
                recyclerView.setAdapter(adapter);
                Toast.makeText(getContext(), "Menggunakan data dummy untuk postingan", Toast.LENGTH_SHORT).show();
            }
        });
    }
}