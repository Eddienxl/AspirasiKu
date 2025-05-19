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

    // Get all posts
    @GET("api/postingan")
    Call<List<Postingan>> getAllPosts();

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

    // Get interaction status (optional)
    @GET("api/interaksi/{id_postingan}")
    Call<InteraksiResponse> getInteractionStatus(@Path("id_postingan") int postId, @Header("Authorization") String token);
}