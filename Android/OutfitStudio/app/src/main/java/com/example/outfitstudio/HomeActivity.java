package com.example.outfitstudio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        // Get logged-in user's ID
        userId = getIntent().getIntExtra("user_id", -1);

        TextView tvWelcome =
                findViewById(R.id.tvWelcome);

        Button btnProfile =
                findViewById(R.id.btnProfile);

        // Display user's name if available
        String userName =
                getIntent().getStringExtra("user_name");

        if (userName != null && !userName.isEmpty()) {
            tvWelcome.setText(
                    "Welcome, " + userName
            );
        }

        // Open Profile
        btnProfile.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    ProfileActivity.class
            );

            intent.putExtra(
                    "user_id",
                    userId
            );

            startActivity(intent);
        });

        Button btnAppearance = findViewById(R.id.btnAppearance);

        btnAppearance.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    AppearanceActivity.class
            );

            intent.putExtra(
                    "user_id",
                    userId
            );

            startActivity(intent);
        });
    }
}