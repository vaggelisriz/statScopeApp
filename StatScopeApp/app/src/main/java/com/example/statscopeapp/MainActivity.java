package com.example.statscopeapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Entry point of the application where users select their functional role.
 */
public class MainActivity extends AppCompatActivity {

    // UI Components
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
     * Links XML components to Java objects.
     */
    private void initViewComponents() {
        btnRoleAdmin = findViewById(R.id.btn_role_admin);
        btnRoleFan = findViewById(R.id.btn_role_fan);
    }

    /**
     * Assigns click behaviors to buttons.
     */
    private void setupActionListeners() {
        // Logic for Statistician role selection
        btnRoleAdmin.setOnClickListener(view -> {
            navigateToDashboard("Statistician");
        });

        // Logic for Fan role selection
        btnRoleFan.setOnClickListener(view -> {
            navigateToDashboard("Fan");
        });
    }

    /**
     * Handles navigation logic based on user selection.
     * @param selectedRole The role chosen by the user.
     */
    private void navigateToDashboard(String selectedRole) {
        // Temporary feedback. Navigation Intents will be implemented in the next step.
        Toast.makeText(this, "Accessing " + selectedRole + " Dashboard", Toast.LENGTH_SHORT).show();
    }
}