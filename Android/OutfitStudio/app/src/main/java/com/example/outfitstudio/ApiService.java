package com.example.outfitstudio;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

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
}