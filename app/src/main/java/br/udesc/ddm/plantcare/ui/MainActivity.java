package br.udesc.ddm.plantcare.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import br.udesc.ddm.plantcare.R;
import br.udesc.ddm.plantcare.service.WateringReminderService;
import br.udesc.ddm.plantcare.ui.fragments.AddCareLogFragment;
import br.udesc.ddm.plantcare.ui.fragments.AddPlantFragment;
import br.udesc.ddm.plantcare.ui.fragments.HomeFragment;
import br.udesc.ddm.plantcare.ui.fragments.LightSensorFragment;
import br.udesc.ddm.plantcare.ui.fragments.PlantDetailFragment;
import br.udesc.ddm.plantcare.ui.fragments.SettingsFragment;
import br.udesc.ddm.plantcare.ui.fragments.SpeciesCatalogFragment;
import br.udesc.ddm.plantcare.viewmodel.PlantViewModel;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ImageButton btnHeaderBack;
    private TextView tvHeaderTitle;
    private PlantViewModel plantViewModel;
    private boolean suppressNavListener = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        plantViewModel = new ViewModelProvider(this).get(PlantViewModel.class);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        btnHeaderBack = findViewById(R.id.btnHeaderBack);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        TextView btnHeaderCatalog = findViewById(R.id.btnHeaderCatalog);
        ImageButton btnHeaderProfile = findViewById(R.id.btnHeaderProfile);

        requestNotificationPermissionIfNeeded();
        startWateringBackgroundService();

        btnHeaderBack.setOnClickListener(v -> onBackPressed());
        btnHeaderCatalog.setOnClickListener(v -> navigateToSpeciesCatalog());
        btnHeaderProfile.setOnClickListener(v -> navigateToSettings());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (suppressNavListener) return true;
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadRootFragment(new HomeFragment(), "PlantCare");
                return true;
            } else if (id == R.id.nav_add) {
                loadRootFragment(new AddPlantFragment(), "PlantCare");
                return true;
            } else if (id == R.id.nav_sensor) {
                loadRootFragment(new LightSensorFragment(), "Sensor & Clima");
                return true;
            } else if (id == R.id.nav_settings) {
                loadRootFragment(new SettingsFragment(), "Configurações");
                return true;
            }
            return false;
        });

        getSupportFragmentManager().addOnBackStackChangedListener(this::updateHeaderForCurrentFragment);

        if (savedInstanceState == null) {
            loadRootFragment(new HomeFragment(), "PlantCare");
        }
    }

    private void loadRootFragment(Fragment fragment, String title) {
        getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
        tvHeaderTitle.setText(title);
        btnHeaderBack.setVisibility(View.GONE);
    }

    public void navigateToHome() {
        suppressNavListener = true;
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        suppressNavListener = false;
        loadRootFragment(new HomeFragment(), "PlantCare");
    }

    public void navigateToAddPlant() {
        suppressNavListener = true;
        bottomNavigationView.setSelectedItemId(R.id.nav_add);
        suppressNavListener = false;
        loadRootFragment(new AddPlantFragment(), "PlantCare");
    }

    public void navigateToLightSensor() {
        suppressNavListener = true;
        bottomNavigationView.setSelectedItemId(R.id.nav_sensor);
        suppressNavListener = false;
        loadRootFragment(new LightSensorFragment(), "Sensor & Clima");
    }

    public void navigateToSettings() {
        suppressNavListener = true;
        bottomNavigationView.setSelectedItemId(R.id.nav_settings);
        suppressNavListener = false;
        loadRootFragment(new SettingsFragment(), "Configurações");
    }

    public void navigateToPlantDetail(int plantId) {
        plantViewModel.setSelectedPlantId(plantId);
        PlantDetailFragment detailFragment = PlantDetailFragment.newInstance(plantId);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, detailFragment)
                .addToBackStack("plant_detail")
                .commit();
        tvHeaderTitle.setText("Detalhes Da Planta");
        btnHeaderBack.setVisibility(View.VISIBLE);
    }

    public void navigateToAddCareLog(int plantId, String plantName) {
        AddCareLogFragment fragment = AddCareLogFragment.newInstance(plantId, plantName);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("add_care_log")
                .commit();
        tvHeaderTitle.setText("Diário de Cuidados");
        btnHeaderBack.setVisibility(View.VISIBLE);
    }

    public void navigateToSpeciesCatalog() {
        SpeciesCatalogFragment fragment = new SpeciesCatalogFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack("species_catalog")
                .commit();
        tvHeaderTitle.setText("Catálogo Botânico");
        btnHeaderBack.setVisibility(View.VISIBLE);
    }

    private void updateHeaderForCurrentFragment() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (current instanceof PlantDetailFragment) {
            tvHeaderTitle.setText("Detalhes Da Planta");
            btnHeaderBack.setVisibility(View.VISIBLE);
        } else if (current instanceof AddCareLogFragment) {
            tvHeaderTitle.setText("Diário de Cuidados");
            btnHeaderBack.setVisibility(View.VISIBLE);
        } else if (current instanceof SpeciesCatalogFragment) {
            tvHeaderTitle.setText("Catálogo Botânico");
            btnHeaderBack.setVisibility(View.VISIBLE);
        } else {
            tvHeaderTitle.setText("PlantCare");
            btnHeaderBack.setVisibility(View.GONE);
        }
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void startWateringBackgroundService() {
        Intent serviceIntent = new Intent(this, WateringReminderService.class);
        startService(serviceIntent);
    }
}

