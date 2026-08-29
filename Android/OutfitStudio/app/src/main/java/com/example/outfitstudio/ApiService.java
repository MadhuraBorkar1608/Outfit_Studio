package com.example.outfitstudio;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    @GET("api/test")
    Call<TestResponse> testApi();

    @POST("api/register")
    Call<RegisterResponse> registerUser(
            @Body RegisterRequest request
    );

    @POST("api/login")
    Call<LoginResponse> loginUser(
            @Body LoginRequest request
    );

    @GET("api/profile/{user_id}")
    Call<ProfileResponse> getProfile(
            @Path("user_id") int userId
    );

    @PUT("api/profile/{user_id}")
    Call<ProfileUpdateResponse> updateProfile(
            @Path("user_id") int userId,
            @Body ProfileUpdateRequest request
    );

    @Multipart
    @POST("api/appearance/analyze")
    Call<AppearanceResponse> analyzeAppearance(
            @Part("user_id") RequestBody userId,
            @Part MultipartBody.Part image,
            @Part("chest") RequestBody chest,
            @Part("waist") RequestBody waist,
            @Part("hip") RequestBody hip
    );

    // Add clothing
    @Multipart
    @POST("api/wardrobe")
    Call<WardrobeResponse> addWardrobeItem(
            @Part("user_id") RequestBody userId,
            @Part("name") RequestBody name,
            @Part("category") RequestBody category,
            @Part("color") RequestBody color,
            @Part MultipartBody.Part image
    );

    // Get all wardrobe items
    @GET("api/wardrobe/{user_id}")
    Call<WardrobeListResponse> getWardrobeItems(
            @Path("user_id") int userId
    );

    // Get single wardrobe item
    @GET("api/wardrobe/item/{item_id}")
    Call<WardrobeResponse> getWardrobeItem(
            @Path("item_id") int itemId
    );

    // Update wardrobe item
    @PUT("api/wardrobe/{item_id}")
    Call<WardrobeResponse> updateWardrobeItem(
            @Path("item_id") int itemId,
            @Body WardrobeUpdateRequest request
    );

    // Delete wardrobe item
    @DELETE("api/wardrobe/{item_id}")
    Call<WardrobeResponse> deleteWardrobeItem(
            @Path("item_id") int itemId
    );
}