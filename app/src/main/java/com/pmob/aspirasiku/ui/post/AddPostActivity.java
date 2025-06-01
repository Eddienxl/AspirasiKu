package com.pmob.aspirasiku.ui.post;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
    // ProgressBar tidak dideklarasikan di sini

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
        // ProgressBar tidak diinisialisasi di sini

        apiService = RetrofitClient.getApiService();
        tokenManager = new TokenManager(this);
        kategoriList = new ArrayList<>();

        setupKategoriSpinner();
        btnSubmit.setOnClickListener(v -> submitPost());
    }

    private void setupKategoriSpinner() {
        btnSubmit.setEnabled(false); // Nonaktifkan tombol submit saat memuat kategori

        apiService.getKategori().enqueue(new Callback<List<Kategori>>() {
            @Override
            public void onResponse(Call<List<Kategori>> call, Response<List<Kategori>> response) {
                btnSubmit.setEnabled(true); // Aktifkan kembali tombol submit

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
                    Toast.makeText(AddPostActivity.this, "Gagal memuat kategori: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("ADD_POST", "Failed to load categories: " + response.code() + " - " + (response.errorBody() != null ? response.errorBody().toString() : ""));
                    btnSubmit.setEnabled(false); // Nonaktifkan submit jika tidak ada kategori
                }
            }

            @Override
            public void onFailure(Call<List<Kategori>> call, Throwable t) {
                btnSubmit.setEnabled(false); // Nonaktifkan submit jika ada error koneksi

                Log.e("ADD_POST", "Error koneksi saat memuat kategori: " + t.getMessage(), t);
                Toast.makeText(AddPostActivity.this, "Error koneksi saat memuat kategori", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitPost() {
        String judul = inputJudul.getText().toString().trim();
        String konten = inputKonten.getText().toString().trim();
        int selectedKategoriPosition = spinnerKategori.getSelectedItemPosition();

        int selectedRadioButtonId = radioGroupTipe.getCheckedRadioButtonId();
        String tipe = null;
        if (selectedRadioButtonId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
            tipe = selectedRadioButton.getText().toString().toLowerCase();
            if ("aspirasi".equals(tipe)) {
                tipe = "aspirasi";
            } else if ("pertanyaan".equals(tipe)) {
                tipe = "pertanyaan";
            } else {
                tipe = "default";
            }
        }


        // Validasi
        if (judul.isEmpty()) {
            inputJudul.setError("Judul tidak boleh kosong");
            inputJudul.requestFocus();
            return;
        }
        if (konten.isEmpty()) {
            inputKonten.setError("Konten tidak boleh kosong");
            inputKonten.requestFocus();
            return;
        }
        if (kategoriList.isEmpty() || selectedKategoriPosition == AdapterView.INVALID_POSITION) {
            Toast.makeText(this, "Kategori belum dimuat atau tidak dipilih", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedRadioButtonId == -1) {
            Toast.makeText(this, "Pilih tipe postingan (Aspirasi/Pertanyaan)", Toast.LENGTH_SHORT).show();
            return;
        }

        int idKategori = kategoriList.get(selectedKategoriPosition).getId();

        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Anda harus login untuk membuat postingan.", Toast.LENGTH_SHORT).show();
            return;
        }

        NewPost newPost = new NewPost(judul, konten, idKategori, tipe);

        btnSubmit.setEnabled(false); // Nonaktifkan tombol submit saat mengirim

        apiService.createPost(newPost, "Bearer " + token)
                .enqueue(new Callback<Postingan>() {
                    @Override
                    public void onResponse(Call<Postingan> call, Response<Postingan> response) {
                        btnSubmit.setEnabled(true); // Aktifkan tombol submit

                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(AddPostActivity.this, "Postingan berhasil diajukan!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddPostActivity.this, PostDetailActivity.class);
                            intent.putExtra("post_id", response.body().getId());
                            startActivity(intent);
                            finish();
                        } else {
                            String errorMessage = "Gagal mengajukan postingan";
                            if (response.errorBody() != null) {
                                try {
                                    errorMessage += ": " + response.errorBody().string();
                                    Log.e("ADD_POST", "Server Error: " + response.code() + " - " + errorMessage);
                                } catch (Exception e) {
                                    Log.e("ADD_POST", "Error parsing error body: " + e.getMessage());
                                }
                            } else {
                                Log.e("ADD_POST", "Failed to submit post: " + response.code());
                            }
                            Toast.makeText(AddPostActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Postingan> call, Throwable t) {
                        btnSubmit.setEnabled(true); // Aktifkan tombol submit

                        Log.e("ADD_POST", "Koneksi Error saat mengajukan postingan: " + t.getMessage(), t);
                        Toast.makeText(AddPostActivity.this, "Error koneksi saat mengajukan postingan", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}