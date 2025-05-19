package com.pmob.aspirasiku.ui.post;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pmob.aspirasiku.R;
import com.pmob.aspirasiku.data.api.ApiService;
import com.pmob.aspirasiku.data.api.RetrofitClient;
import com.pmob.aspirasiku.data.model.Kategori;
import com.pmob.aspirasiku.data.model.NewPost;
import com.pmob.aspirasiku.data.model.Postingan;
import com.pmob.aspirasiku.ui.detail.PostDetailActivity;
import com.pmob.aspirasiku.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPostActivity extends AppCompatActivity {

    private EditText inputJudul, inputKonten;
    private Spinner spinnerKategori;
    private RadioGroup radioGroupTipe;
    private RadioButton radioAspirasi, radioPertanyaan;
    private Button btnSubmit;
    private ApiService apiService;
    private TokenManager tokenManager;
    private List<Kategori> kategoriList;
    private ArrayAdapter<String> kategoriAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        inputJudul = findViewById(R.id.inputJudul);
        inputKonten = findViewById(R.id.inputKonten);
        spinnerKategori = findViewById(R.id.spinnerKategori);
        radioGroupTipe = findViewById(R.id.radioGroupTipe);
        radioAspirasi = findViewById(R.id.radioAspirasi);
        radioPertanyaan = findViewById(R.id.radioPertanyaan);
        btnSubmit = findViewById(R.id.btnSubmit);

        apiService = RetrofitClient.getApiService();
        tokenManager = new TokenManager(this);
        kategoriList = new ArrayList<>();

        setupKategoriSpinner();
        btnSubmit.setOnClickListener(v -> submitPost());
    }

    private void setupKategoriSpinner() {
        apiService.getKategori().enqueue(new Callback<List<Kategori>>() {
            @Override
            public void onResponse(Call<List<Kategori>> call, Response<List<Kategori>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    kategoriList = response.body();
                    List<String> kategoriNames = new ArrayList<>();
                    for (Kategori kategori : kategoriList) {
                        kategoriNames.add(kategori.getNama());
                    }
                    kategoriAdapter = new ArrayAdapter<>(AddPostActivity.this,
                            android.R.layout.simple_spinner_item, kategoriNames);
                    kategoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerKategori.setAdapter(kategoriAdapter);
                } else {
                    Toast.makeText(AddPostActivity.this, "Gagal memuat kategori", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Kategori>> call, Throwable t) {
                Log.e("ADD_POST", "onFailure: " + t.getMessage());
                Toast.makeText(AddPostActivity.this, "Error koneksi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitPost() {
        String judul = inputJudul.getText().toString().trim();
        String konten = inputKonten.getText().toString().trim();
        int selectedKategoriPosition = spinnerKategori.getSelectedItemPosition();
        String tipe = radioAspirasi.isChecked() ? "aspirasi" : "pertanyaan";

        // Validasi
        if (judul.isEmpty()) {
            inputJudul.setError("Judul tidak boleh kosong");
            return;
        }
        if (konten.isEmpty()) {
            inputKonten.setError("Konten tidak boleh kosong");
            return;
        }
        if (selectedKategoriPosition == -1) {
            Toast.makeText(this, "Pilih kategori", Toast.LENGTH_SHORT).show();
            return;
        }

        int idKategori = kategoriList.get(selectedKategoriPosition).getId();
        NewPost newPost = new NewPost(judul, konten, idKategori, tipe);

        apiService.createPost(newPost, "Bearer " + tokenManager.getToken())
                .enqueue(new Callback<Postingan>() {
                    @Override
                    public void onResponse(Call<Postingan> call, Response<Postingan> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(AddPostActivity.this, "Postingan berhasil diajukan!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddPostActivity.this, PostDetailActivity.class);
                            intent.putExtra("post_id", response.body().getId());
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(AddPostActivity.this, "Gagal mengajukan postingan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Postingan> call, Throwable t) {
                        Log.e("ADD_POST", "onFailure: " + t.getMessage());
                        Toast.makeText(AddPostActivity.this, "Error koneksi", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}