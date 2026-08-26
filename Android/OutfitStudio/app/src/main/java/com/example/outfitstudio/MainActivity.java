package com.example.outfitstudio;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d("API_TEST", "Starting API request");

        RetrofitClient.getApiService()
                .testApi()
                .enqueue(new Callback<TestResponse>() {

                    @Override
                    public void onResponse(
                            Call<TestResponse> call,
                            Response<TestResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            Log.d(
                                    "API_TEST",
                                    "Success: " + response.body().getMessage()
                            );

                        } else {

                            Log.d(
                                    "API_TEST",
                                    "API error: " + response.code()
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<TestResponse> call,
                            Throwable t) {

                        Log.e(
                                "API_TEST",
                                "Connection failed",
                                t
                        );
                    }
                });
    }
}