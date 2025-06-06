package com.pmob.aspirasiku.data.api;

import com.pmob.aspirasiku.data.model.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // Login
    @POST("api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    // Register
    @POST("api/auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    // Get all posts with filter and search
    @GET("api/postingan")
    Call<List<Postingan>> getAllPosts(
            @Query("sort") String sort,
            @Query("kategori") Integer kategori,
            @Query("search") String search
    );

    // Get post detail
    @GET("api/postingan/{id}")
    Call<Postingan> getPostDetail(@Path("id") int postId);

    // Create new post
    @POST("api/postingan")
    Call<Postingan> createPost(@Body NewPost post, @Header("Authorization") String token);

    // Get all categories
    @GET("api/kategori")
    Call<List<Kategori>> getKategori();

    // Create new comment
    @POST("api/komentar")
    Call<Object> createComment(@Body Komentar komentar, @Header("Authorization") String token);

    // Get user profile
    @GET("api/pengguna")
    Call<Pengguna> getUserProfile(@Header("Authorization") String token);

    // Get user posts
    @GET("api/pengguna/postingan")
    Call<List<Postingan>> getUserPosts(@Header("Authorization") String token);

    // Update user profile
    @PUT("api/pengguna")
    Call<Pengguna> updateUserProfile(@Body UpdateProfileRequest request, @Header("Authorization") String token);

    // Update password
    @PUT("api/pengguna/password")
    Call<Object> updatePassword(@Body UpdatePasswordRequest request, @Header("Authorization") String token);

    // Get notifications
    @GET("api/notifikasi")
    Call<List<Notifikasi>> getNotifications(@Header("Authorization") String token);

    // Send vote
    @POST("api/interaksi")
    Call<InteraksiResponse> sendVote(@Body InteraksiRequest request, @Header("Authorization") String token);

    // Get interaction status
    @GET("api/interaksi/{id_postingan}")
    Call<InteraksiResponse> getInteractionStatus(@Path("id_postingan") int postId, @Header("Authorization") String token);

    // Admin: Get all users
    @GET("api/admin/pengguna")
    Call<List<Pengguna>> getAllUsers(@Header("Authorization") String token);

    // Admin: Delete user
    @DELETE("api/admin/pengguna/{id}")
    Call<Object> deleteUser(@Path("id") int userId, @Header("Authorization") String token);

    // Admin: Get all posts
    @GET("api/admin/postingan")
    Call<List<Postingan>> getAllAdminPosts(@Header("Authorization") String token);

    // Admin: Delete post
    @DELETE("api/admin/postingan/{id}")
    Call<Object> deletePost(@Path("id") int postId, @Header("Authorization") String token);

    // Send FCM token
    @POST("api/fcm-token")
    Call<Object> sendFcmToken(@Body FcmTokenRequest request, @Header("Authorization") String token);
}