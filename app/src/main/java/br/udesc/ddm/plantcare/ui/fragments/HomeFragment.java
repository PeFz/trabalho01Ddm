package br.udesc.ddm.plantcare.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.udesc.ddm.plantcare.R;
import br.udesc.ddm.plantcare.data.local.entity.PlantEntity;
import br.udesc.ddm.plantcare.ui.MainActivity;
import br.udesc.ddm.plantcare.ui.adapter.PlantAdapter;
import br.udesc.ddm.plantcare.viewmodel.PlantViewModel;

public class HomeFragment extends Fragment {

    private static final int FILTER_ALL = 0;
    private static final int FILTER_NEEDS_WATER = 1;
    private static final int FILTER_HYDRATED = 2;

    private PlantViewModel viewModel;
    private PlantAdapter plantAdapter;
    private List<PlantEntity> cachedPlants = new ArrayList<>();
    private int currentFilter = FILTER_ALL;

    private TextView tvGreetingTitle;
    private TextView tvReminderHeadline;
    private TextView tvReminderSubtext;
    private TextView tvWeatherHomeSummary;
    private TextView chipFilterAll, chipFilterNeedsWater, chipFilterHydrated;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(PlantViewModel.class);

        tvGreetingTitle = view.findViewById(R.id.tvGreetingTitle);
        tvReminderHeadline = view.findViewById(R.id.tvReminderHeadline);
        tvReminderSubtext = view.findViewById(R.id.tvReminderSubtext);
        tvWeatherHomeSummary = view.findViewById(R.id.tvWeatherHomeSummary);
        chipFilterAll = view.findViewById(R.id.chipFilterAll);
        chipFilterNeedsWater = view.findViewById(R.id.chipFilterNeedsWater);
        chipFilterHydrated = view.findViewById(R.id.chipFilterHydrated);
        ImageButton btnOpenSensorQuick = view.findViewById(R.id.btnOpenSensorQuick);
        LinearLayout layoutWeatherBanner = view.findViewById(R.id.layoutWeatherBanner);
        RecyclerView rvPlants = view.findViewById(R.id.rvPlants);

        SharedPreferences prefs = requireContext().getSharedPreferences("plantcare_prefs", Context.MODE_PRIVATE);
        String gardenerName = prefs.getString("gardener_name", "Jardineiro");
        tvGreetingTitle.setText("Bom dia, " + gardenerName);

        plantAdapter = new PlantAdapter(new PlantAdapter.OnPlantInteractionListener() {
            @Override
            public void onPlantClick(PlantEntity plant) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).navigateToPlantDetail(plant.getId());
                }
            }

            @Override
            public void onWaterClick(PlantEntity plant) {
                viewModel.waterPlantNow(plant);
                Toast.makeText(requireContext(),
                        "💧 " + plant.getName() + " foi regada e registrada no Diário!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        rvPlants.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPlants.setAdapter(plantAdapter);

        btnOpenSensorQuick.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToLightSensor();
            }
        });

        layoutWeatherBanner.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToLightSensor();
            }
        });

        chipFilterAll.setOnClickListener(v -> {
            currentFilter = FILTER_ALL;
            updateFilterUI();
            applyFilter();
        });

        chipFilterNeedsWater.setOnClickListener(v -> {
            currentFilter = FILTER_NEEDS_WATER;
            updateFilterUI();
            applyFilter();
        });

        chipFilterHydrated.setOnClickListener(v -> {
            currentFilter = FILTER_HYDRATED;
            updateFilterUI();
            applyFilter();
        });

        // Observa a lista de plantas do Room via MVVM LiveData
        viewModel.getAllPlants().observe(getViewLifecycleOwner(), plants -> {
            cachedPlants = plants != null ? plants : new ArrayList<>();
            updateSummaryAndCounts();
            applyFilter();
        });

        // Observa o clima retornado pela API via Retrofit
        viewModel.getCurrentWeather().observe(getViewLifecycleOwner(), weather -> {
            if (weather != null) {
                tvWeatherHomeSummary.setText(String.format(
                        "🌤️ Clima API: %.1f°C • Umidade %d%%",
                        weather.getTemperature(), weather.getHumidity()
                ));
            }
        });
    }

    private void updateSummaryAndCounts() {
        List<String> thirstyNames = new ArrayList<>();
        int hydratedCount = 0;

        for (PlantEntity p : cachedPlants) {
            if (p.needsWater()) {
                thirstyNames.add("a " + p.getName());
            } else {
                hydratedCount++;
            }
        }

        int thirstyCount = thirstyNames.size();
        chipFilterAll.setText("Todas (" + cachedPlants.size() + ")");
        chipFilterNeedsWater.setText("💧 Precisam de água ( " + thirstyCount + " )");
        chipFilterHydrated.setText("✓ Hidratadas ( " + hydratedCount + " )");

        if (thirstyCount == 0) {
            tvReminderHeadline.setText("Todas as plantas estão hidratadas!");
            tvReminderSubtext.setText("Seu santuário botânico está em plena harmonia hoje.");
        } else if (thirstyCount == 1) {
            tvReminderHeadline.setText("1 planta precisa de água hoje!");
            tvReminderSubtext.setText( capitalizeFirst(thirstyNames.get(0)) + " anseia por uma hidratação fresca.");
        } else {
            tvReminderHeadline.setText(thirstyCount + " plantas precisam de água hoje!");
            String joined = TextUtils.join(" e ", thirstyNames);
            tvReminderSubtext.setText(capitalizeFirst(joined) + " anseiam por uma hidratação fresca.");
        }
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    private void updateFilterUI() {
        Context ctx = requireContext();
        resetChipStyle(chipFilterAll, ctx);
        resetChipStyle(chipFilterNeedsWater, ctx);
        resetChipStyle(chipFilterHydrated, ctx);

        TextView selected = chipFilterAll;
        if (currentFilter == FILTER_NEEDS_WATER) selected = chipFilterNeedsWater;
        else if (currentFilter == FILTER_HYDRATED) selected = chipFilterHydrated;

        selected.setBackgroundResource(R.drawable.bg_pill_green_filled);
        selected.setTextColor(ContextCompat.getColor(ctx, R.color.white));
    }

    private void resetChipStyle(TextView chip, Context ctx) {
        chip.setBackgroundResource(R.drawable.bg_pill_outline);
        chip.setTextColor(ContextCompat.getColor(ctx, R.color.text_primary));
    }

    private void applyFilter() {
        List<PlantEntity> filtered = new ArrayList<>();
        for (PlantEntity p : cachedPlants) {
            if (currentFilter == FILTER_ALL) {
                filtered.add(p);
            } else if (currentFilter == FILTER_NEEDS_WATER && p.needsWater()) {
                filtered.add(p);
            } else if (currentFilter == FILTER_HYDRATED && !p.needsWater()) {
                filtered.add(p);
            }
        }
        plantAdapter.setPlants(filtered);
    }
}

