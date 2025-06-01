package com.pmob.aspirasiku.data.api;

import com.pmob.aspirasiku.data.model.*; // Ini sudah mengimpor semua model dari package tersebut

import java.util.List;

import okhttp3.MultipartBody; // Perlu untuk upload file
import okhttp3.RequestBody;    // Perlu untuk upload file
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // --- API Endpoints (Autentikasi) ---
    @POST("api/auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @POST("api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @GET("api/auth/profile")
    Call<Pengguna> getUserProfile(@Header("Authorization") String token);

    @PUT("api/auth/ubah-kata-sandi")
    Call<Object> updatePassword(@Body UpdatePasswordRequest request, @Header("Authorization") String token);

    @PUT("api/auth/ubah-profil")
    Call<Pengguna> updateUserProfile(@Body UpdateProfileRequest request, @Header("Authorization") String token);

    @Multipart
    @PUT("api/auth/upload-profile-picture")
    Call<Object> uploadProfilePicture(
            @Part MultipartBody.Part file,
            @Header("Authorization") String token
    );

    // --- API Endpoints (Postingan) ---
    @GET("api/postingan")
    Call<List<Postingan>> getAllPosts(
            @Query("sort") String sort,
            @Query("kategori") Integer kategoriId,
            @Query("search") String keyword
    );

    @GET("api/postingan/{id}")
    Call<Postingan> getPostDetail(@Path("id") int postId);

    @GET("api/postingan/kategori/{nama}")
    Call<List<Postingan>> getPostsByCategoryName(@Path("nama") String categoryName);

    @POST("api/postingan")
    Call<Postingan> createPost(@Body NewPost post, @Header("Authorization") String token);

    @PUT("api/postingan/{id}")
    Call<Postingan> updatePost(@Path("id") int postId, @Body NewPost post, @Header("Authorization") String token);

    @DELETE("api/postingan/{id}")
    Call<Object> deletePost(@Path("id") int postId, @Header("Authorization") String token);

    // --- API Endpoints (Komentar) ---
    @GET("api/komentar")
    Call<List<Komentar>> getAllComments(@Header("Authorization") String token);

    @GET("api/komentar/{id}")
    Call<Komentar> getCommentById(@Path("id") int commentId, @Header("Authorization") String token);

    @POST("api/komentar")
    Call<Object> createComment(@Body Komentar komentar, @Header("Authorization") String token);

    @PUT("api/komentar/{id}")
    Call<Object> updateComment(@Path("id") int commentId, @Body Komentar komentar, @Header("Authorization") String token);
    @GET("api/postingan/{postId}/komentar") // Ini adalah endpoint baru yang saya rekomendasikan
    Call<List<Komentar>> getCommentsByPostId(@Path("postId") int postId);
    @DELETE("api/komentar/{id}")
    Call<Object> deleteComment(@Path("id") int commentId, @Header("Authorization") String token);

    // --- API Endpoints (Kategori) ---
    @GET("api/kategori")
    Call<List<Kategori>> getKategori();

    @GET("api/kategori/{id}")
    Call<Kategori> getCategoryById(@Path("id") int categoryId);

    @POST("api/kategori")
    Call<Kategori> createCategory(@Body Kategori kategori, @Header("Authorization") String token);

    @PUT("api/kategori/{id}")
    Call<Kategori> updateCategory(@Path("id") int categoryId, @Body Kategori kategori, @Header("Authorization") String token);

    @DELETE("api/kategori/{id}")
    Call<Object> deleteCategory(@Path("id") int categoryId, @Header("Authorization") String token);

    // --- API Endpoints (Pengguna) ---
    @GET("api/pengguna")
    Call<List<Pengguna>> getAllUsers(@Header("Authorization") String token);

    @GET("api/pengguna/{id}")
    Call<Pengguna> getUserById(@Path("id") int userId, @Header("Authorization") String token);

    @GET("api/pengguna/nim/{nim}")
    Call<Pengguna> getUserByNim(@Path("nim") String nim, @Header("Authorization") String token);

    @GET("api/pengguna/{id}/postingan")
    Call<List<Postingan>> getUserPostsById(@Path("id") int userId, @Header("Authorization") String token);

    @POST("api/pengguna")
    Call<Pengguna> createUser(@Body RegisterRequest user, @Header("Authorization") String token);

    @PUT("api/pengguna/{id}")
    Call<Pengguna> updateUser(@Path("id") int userId, @Body UpdateProfileRequest user, @Header("Authorization") String token);

    @DELETE("api/pengguna/{id}")
    Call<Object> deleteUser(@Path("id") int userId, @Header("Authorization") String token);

    // --- API Endpoints (Notifikasi) ---
    @GET("api/notifikasi")
    Call<List<Notifikasi>> getNotifications(@Header("Authorization") String token);

    @POST("api/notifikasi")
    Call<Notifikasi> createNotification(@Body Notifikasi notification, @Header("Authorization") String token);

    @PUT("api/notifikasi/{id}/dibaca")
    Call<Object> markNotificationAsRead(@Path("id") int notificationId, @Header("Authorization") String token);

    @PUT("api/notifikasi/semua/dibaca")
    Call<Object> markAllNotificationsAsRead(@Header("Authorization") String token);

    @POST("api/interaksi") // Endpoint untuk mengirim vote
    Call<InteraksiResponse> sendVote(@Body InteraksiRequest request, @Header("Authorization") String token);

    // Endpoint yang benar untuk mendapatkan status interaksi (vote)
    @GET("api/interaksi/postingan/{id_postingan}")
    Call<InteraksiResponse> getInteractionStatus(@Path("id_postingan") int postId, @Header("Authorization") String token);

    @POST("api/fcm-token") // Endpoint untuk mengirim FCM token (HANYA SATU KALI)
    Call<Object> sendFcmToken(@Body FcmTokenRequest request, @Header("Authorization") String token);
}