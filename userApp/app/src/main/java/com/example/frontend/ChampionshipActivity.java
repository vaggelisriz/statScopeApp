package com.example.frontend;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ChampionshipActivity extends AppCompatActivity {

    private TextView tvDashboardTitle;
    private ImageButton btnBack;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private String championshipName;
    private int championshipId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_championship);

        // UI Initialization
        tvDashboardTitle = findViewById(R.id.tv_dashboard_title);
        btnBack = findViewById(R.id.btn_back_dashboard);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        btnBack.setOnClickListener(v -> finish());

        // Λήψη δεδομένων από το Intent
        championshipName = getIntent().getStringExtra("CHAMPIONSHIP_NAME");
        championshipId = getIntent().getIntExtra("CHAMPIONSHIP_ID", -1);

        if (championshipName != null) {
            tvDashboardTitle.setText(championshipName.toUpperCase());
        }

        // ΣΥΝΔΕΣΗ TAB LAYOUT & VIEW PAGER

        // 1. Ορίζουμε τον adapter στο ViewPager
        ChampionshipPagerAdapter pagerAdapter = new ChampionshipPagerAdapter(this, championshipId);
        viewPager.setAdapter(pagerAdapter);

        // 2. Ενώνουμε το TabLayout με το ViewPager και ονομάζουμε τις καρτέλες
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("STANDINGS");
                    break;
                case 1:
                    tab.setText("TEAMS");
                    break;
                case 2:
                    tab.setText("MATCHES");
                    break;
            }
        }).attach();
    }
}