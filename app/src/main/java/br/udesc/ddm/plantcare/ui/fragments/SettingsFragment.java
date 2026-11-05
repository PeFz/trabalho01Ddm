package br.udesc.ddm.plantcare.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import br.udesc.ddm.plantcare.R;
import br.udesc.ddm.plantcare.service.WateringReminderService;
import br.udesc.ddm.plantcare.ui.MainActivity;
import br.udesc.ddm.plantcare.viewmodel.PlantViewModel;

public class SettingsFragment extends Fragment {

    private PlantViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(PlantViewModel.class);

        EditText etGardenerName = view.findViewById(R.id.etGardenerName);
        TextView tvStatsTotalPlants = view.findViewById(R.id.tvStatsTotalPlants);
        TextView tvStatsTotalLogs = view.findViewById(R.id.tvStatsTotalLogs);
        Button btnSaveGardenerProfile = view.findViewById(R.id.btnSaveGardenerProfile);

        ToggleButton toggleBackgroundService = view.findViewById(R.id.toggleBackgroundService);
        CheckBox cbIncludeWeatherInAlert = view.findViewById(R.id.cbIncludeWeatherInAlert);
        Button btnTriggerServiceNow = view.findViewById(R.id.btnTriggerServiceNow);

        Button btnQuickNavCatalog = view.findViewById(R.id.btnQuickNavCatalog);
        Button btnQuickNavSensor = view.findViewById(R.id.btnQuickNavSensor);

        SharedPreferences prefs = requireContext().getSharedPreferences("plantcare_prefs", Context.MODE_PRIVATE);
        etGardenerName.setText(prefs.getString("gardener_name", "Jardineiro"));
        toggleBackgroundService.setChecked(prefs.getBoolean("bg_service_enabled", true));
        cbIncludeWeatherInAlert.setChecked(prefs.getBoolean("include_weather_alert", true));

        viewModel.getAllPlants().observe(getViewLifecycleOwner(), plants -> {
            if (plants != null) {
                tvStatsTotalPlants.setText(String.valueOf(plants.size()));
            }
        });

        viewModel.getTotalLogsCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                tvStatsTotalLogs.setText(String.valueOf(count));
            }
        });

        btnSaveGardenerProfile.setOnClickListener(v -> {
            String name = etGardenerName.getText().toString().trim();
            if (name.isEmpty()) name = "Jardineiro";
            prefs.edit()
                    .putString("gardener_name", name)
                    .putBoolean("include_weather_alert", cbIncludeWeatherInAlert.isChecked())
                    .apply();
            Toast.makeText(requireContext(), "✓ Perfil salvo: Bom dia, " + name + "!", Toast.LENGTH_SHORT).show();
        });

        toggleBackgroundService.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("bg_service_enabled", isChecked).apply();
            Intent serviceIntent = new Intent(requireContext(), WateringReminderService.class);
            if (isChecked) {
                requireContext().startService(serviceIntent);
                Toast.makeText(requireContext(), "Serviço de monitoramento em background ativado.", Toast.LENGTH_SHORT).show();
            } else {
                requireContext().stopService(serviceIntent);
                Toast.makeText(requireContext(), "Serviço em background pausado.", Toast.LENGTH_SHORT).show();
            }
        });

        btnTriggerServiceNow.setOnClickListener(v -> {
            Intent checkIntent = new Intent(requireContext(), WateringReminderService.class);
            checkIntent.setAction(WateringReminderService.ACTION_CHECK_NOW);
            requireContext().startService(checkIntent);
            Toast.makeText(requireContext(),
                    "🔔 WateringReminderService executado em background! Verifique a barra de notificações.",
                    Toast.LENGTH_LONG).show();
        });

        btnQuickNavCatalog.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToSpeciesCatalog();
            }
        });

        btnQuickNavSensor.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToLightSensor();
            }
        });
    }
}

