package com.example.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnRoleAdmin;
    private Button btnRoleFan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViewComponents();
        setupActionListeners();
    }

    // UI Initializer
    private void initViewComponents() {
        btnRoleAdmin = findViewById(R.id.btn_role_admin);
        btnRoleFan = findViewById(R.id.btn_role_fan);
    }

    // Click Handlers
    private void setupActionListeners() {
        btnRoleAdmin.setOnClickListener(view -> navigateToDashboard("Statistician"));
        btnRoleFan.setOnClickListener(view -> navigateToDashboard("Fan"));
    }

    // Navigation Logic
    private void navigateToDashboard(String selectedRole) {
        if (selectedRole.equals("Statistician")) {
            Intent intent = new Intent(MainActivity.this, StatisticianActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Accessing Fan Dashboard", Toast.LENGTH_SHORT).show();
        }
    }
}