package com.example.lzz_finaltask.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lzz_finaltask.R;
import com.example.lzz_finaltask.model.NewsType;
import com.example.lzz_finaltask.ui.adapter.NewsTypeAdapter;
import com.example.lzz_finaltask.network.RetrofitManager;
import com.example.lzz_finaltask.network.response.BaseResponse;
import com.example.lzz_finaltask.utils.GsonUtil;
import com.example.lzz_finaltask.utils.NavigationUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscoveryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsTypeAdapter newsTypeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discovery);
        initViews();
        setupRecyclerView();
        loadNewsTypes();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);


        BottomNavigationView navigationView = findViewById(R.id.discovery_bottom_nav);
        navigationView.setSelectedItemId(R.id.navigation_explore);

        navigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                NavigationUtils.navigateWithClearTask(this, MainActivity.class);
                return true;
            } else if (itemId == R.id.navigation_explore) {
                return true;
            } else if (itemId == R.id.navigation_profile) {
                NavigationUtils.navigateWithClearTask(this, ProfileActivity.class);
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        newsTypeAdapter = new NewsTypeAdapter(this);
        recyclerView.setAdapter(newsTypeAdapter);

    }

    private void loadNewsTypes() {

    }
}