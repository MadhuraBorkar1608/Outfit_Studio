package com.example.outfitstudio;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.PUT;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.Part;





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
            @Part MultipartBody.Part image
    );
}