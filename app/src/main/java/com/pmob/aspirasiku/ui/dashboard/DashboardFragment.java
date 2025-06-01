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
import android.widget.ProgressBar; // Import ProgressBar
import android.widget.Spinner;
import android.widget.TextView; // Import TextView
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
import com.pmob.aspirasiku.data.model.Pengguna; // Import Pengguna
import com.pmob.aspirasiku.data.model.Postingan;
import com.pmob.aspirasiku.ui.post.AddPostActivity;
import com.pmob.aspirasiku.utils.TokenManager; // Import TokenManager (jika digunakan)

import java.io.IOException;
import java.time.LocalDateTime; // Untuk data dummy tanggal
import java.time.format.DateTimeFormatter; // Untuk data dummy tanggal
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter adapter; // Deklarasikan adapter di sini
    private Spinner spinnerSort, spinnerCategory;
    private SearchView searchView;
    private FloatingActionButton fabAddPost; // Deklarasikan FloatingActionButton
    private ProgressBar progressBar; // Deklarasikan ProgressBar
    private TextView tvErrorMessage; // Deklarasikan TextView untuk error

    private ApiService apiService;
    private List<Kategori> kategoriList = new ArrayList<>();
    private String selectedSort = "terbaru"; // Default sort
    private Integer selectedCategory = null; // Default: Semua Kategori
    private String searchQuery = null;

    private boolean isReviewerStatus = false; // Status peninjau dari MainActivity

    public DashboardFragment() {
        // Required empty public constructor
    }

    // Method yang dipanggil dari MainActivity untuk update status reviewer
    public void updateReviewerStatus(boolean isReviewer) {
        this.isReviewerStatus = isReviewer;
        if (adapter != null) {
            adapter.setReviewerStatus(isReviewerStatus);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inisialisasi ApiService di onCreate fragment
        apiService = RetrofitClient.getApiService();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Inisialisasi View dari layout
        recyclerView = view.findViewById(R.id.recyclerViewPosts); // Ganti recyclerPosts menjadi recyclerViewPosts
        spinnerSort = view.findViewById(R.id.spinnerSort);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        searchView = view.findViewById(R.id.searchView);
        fabAddPost = view.findViewById(R.id.fabAddPost); // Inisialisasi FAB
        progressBar = view.findViewById(R.id.progressBar); // Inisialisasi ProgressBar
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage); // Inisialisasi TextView error

        if (getContext() == null) {
            return view; // Safety check
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Inisialisasi adapter dengan list kosong, status reviewer akan di-set kemudian
        adapter = new PostAdapter(new ArrayList<>(), getContext());
        recyclerView.setAdapter(adapter);

        // Setup SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query.isEmpty() ? null : query;
                fetchPosts();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Gunakan debounce atau panggil fetchPosts() hanya saat query berubah signifikan
                // Untuk sekarang, kita panggil setiap perubahan, namun perhatikan performa pada data besar.
                searchQuery = newText.isEmpty() ? null : newText;
                fetchPosts();
                return true;
            }
        });

        // Setup spinner untuk sorting
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.sort_options, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSort = position == 0 ? "terbaru" : "populer"; // Pastikan "terbaru" atau "populer" sesuai API
                fetchPosts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        fetchCategories(); // Ini akan memanggil setupCategorySpinner dan kemudian fetchPosts

        fabAddPost.setOnClickListener(v -> {
            if (getContext() != null) {
                Intent intent = new Intent(getContext(), AddPostActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Muat ulang postingan setiap kali fragment resume
        fetchPosts();
        // Set ulang status reviewer ke adapter karena adapter mungkin di-reset atau karena data baru
        if (adapter != null) {
            adapter.setReviewerStatus(isReviewerStatus);
        }
    }

    private void showLoading() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (tvErrorMessage != null) tvErrorMessage.setVisibility(View.GONE);
        if (recyclerView != null) recyclerView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }

    private void showErrorMessage(String message) {
        if (tvErrorMessage != null) {
            tvErrorMessage.setText(message);
            tvErrorMessage.setVisibility(View.VISIBLE);
        }
        if (recyclerView != null) recyclerView.setVisibility(View.GONE);
    }

    private void showRecyclerView() {
        if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
        if (tvErrorMessage != null) tvErrorMessage.setVisibility(View.GONE);
    }

    private void fetchCategories() {
        if (getContext() == null || apiService == null) {
            setupCategorySpinner(new ArrayList<>()); // Setup spinner kosong jika konteks/API null
            return;
        }

        apiService.getKategori().enqueue(new Callback<List<Kategori>>() {
            @Override
            public void onResponse(Call<List<Kategori>> call, Response<List<Kategori>> response) {
                if (!isAdded() || getContext() == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    kategoriList.clear();
                    kategoriList.addAll(response.body());
                    setupCategorySpinner(kategoriList);
                } else {
                    Log.e("DASHBOARD", "Gagal memuat kategori dari server. Code: " + response.code());
                    String errorBody = "";
                    if (response.errorBody() != null) {
                        try {
                            errorBody = response.errorBody().string();
                        } catch (IOException e) {
                            Log.e("DASHBOARD", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Log.e("DASHBOARD", "Error response body: " + errorBody);
                    loadDummyCategories("Gagal memuat kategori dari server: " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<List<Kategori>> call, Throwable t) {
                if (!isAdded() || getContext() == null) return;

                Log.e("DASHBOARD", "Fetch Categories onFailure: " + t.getMessage());
                loadDummyCategories("Error koneksi saat memuat kategori: " + t.getMessage());
            }
        });
    }

    private void loadDummyCategories(String reason) {
        Log.d("DASHBOARD", "Memuat data dummy kategori karena: " + reason);
        kategoriList.clear();
        kategoriList.add(new Kategori(1, "Fasilitas Kampus"));
        kategoriList.add(new Kategori(2, "Kesejahteraan Mahasiswa"));
        kategoriList.add(new Kategori(3, "Kegiatan Kemahasiswaan"));
        kategoriList.add(new Kategori(4, "Sarana dan Prasarana Digital"));
        kategoriList.add(new Kategori(5, "Keamanan dan Ketertiban"));
        kategoriList.add(new Kategori(6, "Lingkungan dan Kebersihan"));
        kategoriList.add(new Kategori(7, "Transportasi dan Akses"));
        kategoriList.add(new Kategori(8, "Kebijakan dan Administrasi"));
        kategoriList.add(new Kategori(9, "Saran dan Inovasi"));
        setupCategorySpinner(kategoriList);
        Toast.makeText(getContext(), "Gagal memuat kategori, menggunakan data dummy", Toast.LENGTH_SHORT).show();
    }


    private void setupCategorySpinner(List<Kategori> categories) {
        if (getContext() == null || spinnerCategory == null) return;

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
                    selectedCategory = null;
                } else {
                    if (kategoriList != null && !kategoriList.isEmpty() && (position - 1) < kategoriList.size()) {
                        selectedCategory = kategoriList.get(position - 1).getId();
                    } else {
                        selectedCategory = null;
                    }
                }
                fetchPosts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void fetchPosts() {
        showLoading(); // Tampilkan loading sebelum fetch

        // Pastikan apiService tidak null sebelum memanggilnya
        if (apiService == null) {
            hideLoading();
            showErrorMessage("Error: ApiService belum diinisialisasi.");
            Log.e("DASHBOARD", "ApiService is null during fetchPosts");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                loadDummyPosts("ApiService null"); // Load dummy jika API Service belum siap
            }
            return;
        }

        apiService.getAllPosts(selectedSort, selectedCategory, searchQuery).enqueue(new Callback<List<Postingan>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<List<Postingan>> call, Response<List<Postingan>> response) {
                hideLoading(); // Sembunyikan loading setelah respons

                if (!isAdded() || getContext() == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<Postingan> posts = response.body();
                    if (!posts.isEmpty()) {
                        adapter.updatePostList(posts);
                        adapter.setReviewerStatus(isReviewerStatus); // Pastikan status reviewer di-set
                        showRecyclerView();
                    } else {
                        adapter.updatePostList(new ArrayList<>()); // Kosongkan daftar
                        showErrorMessage("Tidak ada postingan yang tersedia.");
                    }
                } else {
                    String errorMessage = "Gagal memuat postingan dari server. Kode: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += " - " + response.errorBody().string();
                        } catch (IOException e) {
                            Log.e("DASHBOARD", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Log.e("DASHBOARD", errorMessage);
                    loadDummyPosts(errorMessage);
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onFailure(Call<List<Postingan>> call, Throwable t) {
                hideLoading(); // Sembunyikan loading setelah gagal
                if (!isAdded() || getContext() == null) return;

                Log.e("DASHBOARD", "Fetch Posts onFailure: " + t.getMessage());
                loadDummyPosts("Terjadi kesalahan jaringan: " + t.getMessage());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadDummyPosts(String reason) {
        if (getContext() == null) return;
        Log.d("DASHBOARD", "Memuat data dummy postingan karena: " + reason);

        List<Postingan> dummyPosts = new ArrayList<>();
        // Contoh Pengguna dummy
        Pengguna dummyUser1 = new Pengguna(1, "Mahasiswa A", "12345678", "a@example.com", "pass1", "mahasiswa", "https://via.placeholder.com/150/0000FF/FFFFFF?text=MA");
        Pengguna dummyUser2 = new Pengguna(2, "Dosen X", "87654321", "x@example.com", "pass2", "dosen", "https://via.placeholder.com/150/FF0000/FFFFFF?text=DX");
        Pengguna dummyUserAnonim = new Pengguna(0, "Anonim", null, null, null, "mahasiswa", null); // untuk kasus anonim

        // Contoh Kategori dummy (sesuai dengan kategoriList di atas)
        Kategori katFasilitas = new Kategori(1, "Fasilitas Kampus");
        Kategori katKesejahteraan = new Kategori(2, "Kesejahteraan Mahasiswa");
        Kategori katKegiatan = new Kategori(3, "Kegiatan Kemahasiswaan");
        Kategori katLingkungan = new Kategori(6, "Lingkungan dan Kebersihan");


        // Menggunakan konstruktor Postingan yang benar
        // Postingan(int id, String judul, String konten, int id_kategori, Kategori kategori, boolean anonim, String status, String created_at, int upvotes, int downvotes, List<Komentar> komentar, Pengguna penulis)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dummyPosts.add(new Postingan(1, "AC Kelas Rusak", "AC di ruang kelas F201 sudah 3 hari tidak berfungsi. Sangat panas saat perkuliahan.",
                    1, katFasilitas, false, "publik", getCurrentTimestamp(), 15, 2, new ArrayList<>(), dummyUser1));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dummyPosts.add(new Postingan(2, "Perbaikan Kantin", "Kantin perlu diperbaiki, banyak meja yang rusak.",
                    1, katFasilitas, false, "publik", getPastTimestamp(1), 10, 1, new ArrayList<>(), dummyUser2));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dummyPosts.add(new Postingan(3, "Pengadaan Tempat Sampah", "Mohon ditambah tempat sampah di area taman kampus.",
                    6, katLingkungan, false, "publik", getPastTimestamp(2), 20, 5, new ArrayList<>(), dummyUser1));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dummyPosts.add(new Postingan(4, "Kualitas Air Minum", "Air di dispenser kampus terasa aneh, mohon dicek.",
                    2, katKesejahteraan, true, "publik", getPastTimestamp(3), 8, 0, new ArrayList<>(), dummyUserAnonim)); // Postingan anonim
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dummyPosts.add(new Postingan(5, "Acara Donor Darah", "Ada acara donor darah minggu depan, ajak teman-teman!",
                    3, katKegiatan, false, "publik", getPastTimestamp(4), 30, 0, new ArrayList<>(), dummyUser2));
        }


        // Filter berdasarkan kategori
        List<Postingan> filteredPosts = new ArrayList<>();
        if (selectedCategory == null) {
            filteredPosts.addAll(dummyPosts);
        } else {
            for (Postingan post : dummyPosts) {
                // Perbandingan id_kategori di Postingan dengan selectedCategory
                if (post.getId_kategori() == selectedCategory) {
                    filteredPosts.add(post);
                }
            }
        }

        // Filter berdasarkan pencarian
        if (searchQuery != null && !searchQuery.isEmpty()) {
            String queryLower = searchQuery.toLowerCase();
            filteredPosts = filteredPosts.stream()
                    .filter(post -> (post.getJudul() != null && post.getJudul().toLowerCase().contains(queryLower)) ||
                            (post.getKonten() != null && post.getKonten().toLowerCase().contains(queryLower)))
                    .collect(Collectors.toList());
        }

        // Sort berdasarkan terbaru atau populer
        if ("populer".equals(selectedSort)) {
            Collections.sort(filteredPosts, (p1, p2) -> Integer.compare(p2.getUpvotes(), p1.getUpvotes()));
        } else { // "terbaru"
            // Untuk "terbaru", kita bisa sort berdasarkan created_at (jika formatnya mendukung perbandingan string langsung)
            // Atau jika tidak, bisa berdasarkan ID descending (postingan ID lebih besar = lebih baru)
            // Asumsi created_at formatnya ISO 8601 atau sejenisnya sehingga bisa diurutkan string.
            Collections.sort(filteredPosts, (p1, p2) -> p2.getCreated_at().compareTo(p1.getCreated_at()));
        }

        adapter.updatePostList(filteredPosts);
        adapter.setReviewerStatus(isReviewerStatus); // Pastikan status reviewer di-set ke adapter
        showRecyclerView();
        Toast.makeText(getContext(), "Menggunakan data dummy untuk postingan", Toast.LENGTH_SHORT).show();
    }

    // Helper untuk timestamp dummy
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getPastTimestamp(int daysAgo) {
        return LocalDateTime.now().minusDays(daysAgo).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}