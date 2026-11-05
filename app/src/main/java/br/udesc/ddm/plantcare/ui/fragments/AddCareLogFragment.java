package br.udesc.ddm.plantcare.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.udesc.ddm.plantcare.R;
import br.udesc.ddm.plantcare.data.local.entity.CareLogEntity;
import br.udesc.ddm.plantcare.viewmodel.PlantViewModel;

public class AddCareLogFragment extends Fragment {

    private static final String ARG_PLANT_ID = "arg_plant_id";
    private static final String ARG_PLANT_NAME = "arg_plant_name";

    private int plantId = 1;
    private String plantName = "Planta";
    private PlantViewModel viewModel;

    public static AddCareLogFragment newInstance(int plantId, String plantName) {
        AddCareLogFragment fragment = new AddCareLogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PLANT_ID, plantId);
        args.putString(ARG_PLANT_NAME, plantName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            plantId = getArguments().getInt(ARG_PLANT_ID, 1);
            plantName = getArguments().getString(ARG_PLANT_NAME, "Planta");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_care_log, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(PlantViewModel.class);

        TextView tvCareLogHeaderPlant = view.findViewById(R.id.tvCareLogHeaderPlant);
        Spinner spinnerCareCategory = view.findViewById(R.id.spinnerCareCategory);
        EditText etCareDescription = view.findViewById(R.id.etCareDescription);
        ToggleButton toggleDrainageChecked = view.findViewById(R.id.toggleDrainageChecked);
        CheckBox cbResetPlantHydration = view.findViewById(R.id.cbResetPlantHydration);
        Button btnSaveCareLog = view.findViewById(R.id.btnSaveCareLog);

        tvCareLogHeaderPlant.setText("Diário: " + plantName);

        String[] categories = {
                "💧 Rega Completa (Por Você)",
                "🌱 Adubação Orgânica NPK (Nutrição)",
                "✂️ Limpeza de Folhas e Poda (Higiene)",
                "🪴 Troca de Substrato / Vaso (Rotina)"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categories
        );
        spinnerCareCategory.setAdapter(adapter);

        btnSaveCareLog.setOnClickListener(v -> {
            int pos = spinnerCareCategory.getSelectedItemPosition();
            String categoryBadge;
            String iconType;
            String defaultDesc;

            switch (pos) {
                case 1:
                    categoryBadge = "Nutrição";
                    iconType = "NUTRITION";
                    defaultDesc = "Adubação orgânica NPK 10-10-10 aplicada";
                    break;
                case 2:
                    categoryBadge = "Higiene";
                    iconType = "PRUNE";
                    defaultDesc = "Limpeza de folhas e poda leve de folhas secas";
                    break;
                case 3:
                    categoryBadge = "Rotina";
                    iconType = "ROUTINE";
                    defaultDesc = "Renovação de substrato e verificação de raízes";
                    break;
                case 0:
                default:
                    categoryBadge = "Por Você";
                    iconType = "WATER";
                    defaultDesc = "Rega completa realizada (400ml)";
                    break;
            }

            String descInput = etCareDescription.getText().toString().trim();
            String finalDesc = descInput.isEmpty() ? defaultDesc : descInput;
            if (toggleDrainageChecked.isChecked()) {
                finalDesc += " • Vaso drenado";
            }

            String todayFormatted = new SimpleDateFormat("dd MMM", new Locale("pt", "BR")).format(new Date());
            CareLogEntity log = new CareLogEntity(
                    plantId,
                    "Hoje • " + todayFormatted,
                    finalDesc,
                    categoryBadge,
                    iconType
            );

            viewModel.insertCareLog(log, cbResetPlantHydration.isChecked(), plantId);
            Toast.makeText(requireContext(), "✓ Registro salvo no Diário de Cuidados!", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }
}

