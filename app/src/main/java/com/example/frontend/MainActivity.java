package com.example.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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

        // Navigate to Fan/Live Matches Screen
        btnRoleFan.setOnClickListener(view -> navigateToDashboard("Fan"));
    }

    /**
     * Handle navigation logic based on the selected user role
     * @param selectedRole The role chosen by the user
     */
    private void navigateToDashboard(String selectedRole) {
        if ("Statistician".equals(selectedRole)) {
            // Start the activity for adding/editing matches
            Intent intent = new Intent(MainActivity.this, StatisticianActivity.class);
            startActivity(intent);
        } else {
            // Start the activity to view live match scores from the backend
            Intent intent = new Intent(MainActivity.this, LiveMatchesActivity.class);
            startActivity(intent);
        }
    }
}