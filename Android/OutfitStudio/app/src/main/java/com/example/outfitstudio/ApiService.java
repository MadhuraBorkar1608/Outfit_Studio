package com.example.outfitstudio;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.PUT;
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
}