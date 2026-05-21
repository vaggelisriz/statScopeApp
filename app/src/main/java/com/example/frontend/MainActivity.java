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

    /**
     * Initialize UI components from the XML layout
     */
    private void initViewComponents() {
        btnRoleAdmin = findViewById(R.id.btn_role_admin);
        btnRoleFan = findViewById(R.id.btn_role_fan);
    }

    /**
     * Set up click listeners for the interactive elements
     */
    private void setupActionListeners() {
        // Navigate to Statistician Dashboard
        btnRoleAdmin.setOnClickListener(view -> navigateToDashboard("Statistician"));

        // Navigate to Fan (Currently shows a message)
        btnRoleFan.setOnClickListener(view -> navigateToDashboard("Fan"));
    }

    /**
     * Handle navigation logic based on the selected user role
     * @param selectedRole The role chosen by the user
     */
    private void navigateToDashboard(String selectedRole) {
        if ("Statistician".equals(selectedRole)) {
            // Άνοιγμα του Statistician Dashboard
            Intent intent = new Intent(MainActivity.this, StatisticianActivity.class);
            startActivity(intent);
        }
        else if ("Fan".equals(selectedRole)) {
            // Ανοίγουμε το νέο Fan Activity
            Intent intent = new Intent(MainActivity.this, FanActivity.class);
            startActivity(intent);
        }
    }
}