package com.pmob.aspirasiku.ui.main;



import android.annotation.SuppressLint;

import android.content.Intent;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;

import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;



import android.view.View;

import android.widget.AdapterView; // Added for Spinner

import android.widget.ArrayAdapter; // Added for Spinner

import android.widget.ProgressBar;

import android.widget.Spinner; // Added

import android.widget.TextView;

import android.widget.Toast;



import com.pmob.aspirasiku.R;

import com.pmob.aspirasiku.adapter.PostAdapter;

import com.pmob.aspirasiku.data.api.ApiService;

import com.pmob.aspirasiku.data.api.RetrofitClient;

import com.pmob.aspirasiku.data.model.Kategori; // Added

import com.pmob.aspirasiku.data.model.Pengguna;

import com.pmob.aspirasiku.data.model.Postingan;

import com.pmob.aspirasiku.ui.admin.AdminActivity;

import com.pmob.aspirasiku.ui.notification.NotificationActivity;

import com.pmob.aspirasiku.ui.profile.ProfileActivity;

import com.pmob.aspirasiku.ui.post.AddPostActivity;



import com.pmob.aspirasiku.utils.TokenManager;

import android.util.Log;



import java.io.IOException; // Added for errorBody().string()

import java.util.ArrayList;

import java.util.List;



import retrofit2.Call;

import retrofit2.Callback;

import retrofit2.Response;



public class MainActivity extends AppCompatActivity {



    private static final String TAG = "MainActivity";



    private ApiService apiService;

    private TokenManager tokenManager;

    private boolean isAdmin = false;



    private RecyclerView recyclerViewPosts;

    private PostAdapter postAdapter;

    private ProgressBar progressBar;

    private TextView tvErrorMessage;

    private Spinner spinnerCategoryFilter;



    private List<Kategori> categories = new ArrayList<>();

    private ArrayAdapter<String> categoryAdapter;

    private Integer selectedCategoryId = null;



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setTitle("Aspirasiku");

        }



        apiService = RetrofitClient.getApiService();

        tokenManager = new TokenManager(this);



        recyclerViewPosts = findViewById(R.id.recyclerViewPosts);

        progressBar = findViewById(R.id.progressBar);

        tvErrorMessage = findViewById(R.id.tvErrorMessage);

        spinnerCategoryFilter = findViewById(R.id.spinnerCategoryFilter);



        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));

        postAdapter = new PostAdapter(new ArrayList<>(), this);

        recyclerViewPosts.setAdapter(postAdapter);



// Setup Spinner Adapter

        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCategoryFilter.setAdapter(categoryAdapter);



// Spinner Item Selected Listener

        spinnerCategoryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) { // "Semua Kategori" is at position 0

                    selectedCategoryId = null; // No category filter

                } else {

// Adjust position for 1-based index of categories list (skipping "Semua Kategori")

// The categories list is 0-indexed, so if position is 1 (first actual category),

// it corresponds to categories.get(0).

                    selectedCategoryId = categories.get(position - 1).getId();

                }

                Log.d(TAG, "Selected category ID: " + selectedCategoryId);

                fetchAllPosts(); // Reload posts based on new filter

            }



            @Override

            public void onNothingSelected(AdapterView<?> parent) {

// Do nothing

            }

        });



// Fetch user profile and categories first, then posts will be fetched

// via the spinner's onItemSelected callback when categories are populated.

        fetchUserProfile();

        fetchCategories();

    }



    @Override

    protected void onResume() {

        super.onResume();

// Reload posts (based on current filter) and user profile on resume

        fetchAllPosts();

        fetchUserProfile();

    }



    private void fetchUserProfile() {

        String token = tokenManager.getToken();

        if (token == null || token.isEmpty()) {

            isAdmin = false;

            invalidateOptionsMenu(); // Update menu options

            Toast.makeText(this, "Silakan login untuk fitur lengkap.", Toast.LENGTH_SHORT).show();

            return;

        }



        apiService.getUserProfile("Bearer " + token).enqueue(new Callback<Pengguna>() {

            @Override

            public void onResponse(Call<Pengguna> call, Response<Pengguna> response) {

                if (response.isSuccessful() && response.body() != null) {

                    isAdmin = "pengelola".equals(response.body().getPeran());

                    invalidateOptionsMenu(); // Update menu options

                    Log.d(TAG, "User role: " + response.body().getPeran() + ", IsAdmin: " + isAdmin);

                } else {

                    isAdmin = false;

                    invalidateOptionsMenu(); // Update menu options

                    String errorMessage = "Failed to fetch user profile: " + response.code();

                    if (response.errorBody() != null) {

                        try {

                            errorMessage += " - " + response.errorBody().string();

                        } catch (IOException e) {

                            Log.e(TAG, "Error reading error body: " + e.getMessage());

                        }

                    }

                    Log.e(TAG, errorMessage);

                }

            }



            @Override

            public void onFailure(Call<Pengguna> call, Throwable t) {

                isAdmin = false;

                invalidateOptionsMenu(); // Update menu options

                Log.e(TAG, "Error fetching user profile: " + t.getMessage());

            }

        });

    }



    private void fetchCategories() {

        apiService.getKategori().enqueue(new Callback<List<Kategori>>() {

            @Override

            public void onResponse(Call<List<Kategori>> call, Response<List<Kategori>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    categories.clear();

                    categories.addAll(response.body());

                    populateCategorySpinner();

                    Log.d(TAG, "Successfully loaded " + categories.size() + " categories.");

                } else {

                    String errorMessage = "Failed to load categories: " + response.code();

                    if (response.errorBody() != null) {

                        try {

                            errorMessage += " - " + response.errorBody().string();

                        } catch (IOException e) {

                            Log.e(TAG, "Error reading category error body: " + e.getMessage());

                        }

                    }

                    Log.e(TAG, errorMessage);

                    Toast.makeText(MainActivity.this, "Gagal memuat kategori.", Toast.LENGTH_SHORT).show();

                }

            }



            @Override

            public void onFailure(Call<List<Kategori>> call, Throwable t) {

                Log.e(TAG, "Network error fetching categories: " + t.getMessage(), t);

                Toast.makeText(MainActivity.this, "Error koneksi saat memuat kategori.", Toast.LENGTH_SHORT).show();

            }

        });

    }



    private void populateCategorySpinner() {

        List<String> categoryNames = new ArrayList<>();

        categoryNames.add("Semua Kategori"); // First item to show all posts

        for (Kategori kategori : categories) {

            categoryNames.add(kategori.getNama());

        }

        categoryAdapter.clear();

        categoryAdapter.addAll(categoryNames);

        categoryAdapter.notifyDataSetChanged();



// Set initial selection to "Semua Kategori"

// This will trigger onItemSelected, which in turn calls fetchAllPosts().

        spinnerCategoryFilter.setSelection(0);

    }



    private void fetchAllPosts() {

        progressBar.setVisibility(View.VISIBLE);

        tvErrorMessage.setVisibility(View.GONE);

        recyclerViewPosts.setVisibility(View.GONE);



// Pass selectedCategoryId to the API call

        apiService.getAllPosts(null, selectedCategoryId, null).enqueue(new Callback<List<Postingan>>() {

            @Override

            public void onResponse(Call<List<Postingan>> call, Response<List<Postingan>> response) {

                progressBar.setVisibility(View.GONE);



                if (response.isSuccessful() && response.body() != null) {

                    List<Postingan> posts = response.body();

                    if (!posts.isEmpty()) {

                        postAdapter.updatePostList(posts);

                        recyclerViewPosts.setVisibility(View.VISIBLE);

                        Log.d(TAG, "Successfully loaded " + posts.size() + " posts for category ID: " + selectedCategoryId);

                    } else {

                        tvErrorMessage.setText("Tidak ada postingan yang tersedia untuk kategori ini.");

                        tvErrorMessage.setVisibility(View.VISIBLE);

                        Log.d(TAG, "API returned an empty list of posts for category ID: " + selectedCategoryId);

                    }

                } else {

                    String errorMessage = "Gagal memuat postingan. Kode: " + response.code();

                    if (response.errorBody() != null) {

                        try {

                            errorMessage += " - " + response.errorBody().string();

                        } catch (IOException e) {

                            Log.e(TAG, "Error reading post error body: " + e.getMessage());

                        }

                    }

                    tvErrorMessage.setText(errorMessage);

                    tvErrorMessage.setVisibility(View.VISIBLE);

                    Log.e(TAG, "Failed to load posts: " + errorMessage);

                }

            }



            @Override

            public void onFailure(Call<List<Postingan>> call, Throwable t) {

                progressBar.setVisibility(View.GONE);

                tvErrorMessage.setText("Terjadi kesalahan jaringan: " + t.getMessage());

                tvErrorMessage.setVisibility(View.VISIBLE);

                Log.e(TAG, "Network error fetching posts: " + t.getMessage(), t);

            }

        });

    }



    @SuppressLint("ResourceType")

    @Override

    public boolean onCreateOptionsMenu(android.view.Menu menu) {

        getMenuInflater().inflate(R.menu.menu_profile, menu);

        android.view.MenuItem adminItem = menu.findItem(R.id.action_admin);

        if (adminItem != null) {

            adminItem.setVisible(isAdmin);

            Log.d(TAG, "Admin menu item visibility set to: " + isAdmin);

        }

        return true;

    }



    @Override

    public boolean onOptionsItemSelected(android.view.MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_notification) {

            startActivity(new Intent(this, NotificationActivity.class));

            return true;

        } else if (id == R.id.activity_add_post) {

            startActivity(new Intent(this, AddPostActivity.class));

            return true;

        } else if (id == R.id.action_profile) {

            startActivity(new Intent(this, ProfileActivity.class));

            return true;

        } else if (id == R.id.action_admin) {

            if (isAdmin) {

                startActivity(new Intent(this, AdminActivity.class));

                return true;

            } else {

                Toast.makeText(this, "Anda tidak memiliki akses admin.", Toast.LENGTH_SHORT).show();

                return false;

            }

        }

        return super.onOptionsItemSelected(item);

    }

}