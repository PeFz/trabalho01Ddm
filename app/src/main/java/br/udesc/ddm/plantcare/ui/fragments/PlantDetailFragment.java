package br.udesc.ddm.plantcare.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import br.udesc.ddm.plantcare.R;
import br.udesc.ddm.plantcare.data.local.entity.PlantEntity;
import br.udesc.ddm.plantcare.ui.MainActivity;
import br.udesc.ddm.plantcare.ui.adapter.CareLogAdapter;
import br.udesc.ddm.plantcare.utils.PlantImageHelper;
import br.udesc.ddm.plantcare.viewmodel.PlantViewModel;

public class PlantDetailFragment extends Fragment {

    private static final String ARG_PLANT_ID = "arg_plant_id";

    private int plantId = 1;
    private PlantViewModel viewModel;
    private PlantEntity currentPlant;
    private CareLogAdapter careLogAdapter;

    private ImageView ivDetailHero;
    private TextView tvDetailRoomTag, tvDetailNickname, tvDetailPlantName, tvDetailScientificName;
    private LinearLayout cardDetailHydrationAlert;
    private TextView tvDetailHydrationHeadline, tvDetailUrgentBadge;
    private Button btnDetailMarkWatered;
    private TextView tvDetailVitalFreq, tvDetailVitalVolume, tvDetailVitalLightBadge, tvDetailVitalLight, tvDetailVitalLightSub;
    private TextView tvDetailVitalHumidity, tvDetailVitalHumidityTip, tvDetailVitalToxicity, tvDetailVitalToxicityTip;
    private TextView tvDetailSensorRing, tvDetailSubstrateTitle, tvDetailSubstrateSub, tvDiagnosticSubtitle;
    private ImageButton btnDetailFavorite;

    public static PlantDetailFragment newInstance(int plantId) {
        PlantDetailFragment fragment = new PlantDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PLANT_ID, plantId);
        fragment.setArguments(args);
        return fragment;
    }

    private final ActivityResultLauncher<Intent> updatePhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && currentPlant != null) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
                        Bitmap bitmap = (Bitmap) extras.get("data");
                        if (bitmap != null) {
                            String savedPath = PlantImageHelper.saveCameraBitmapToInternalStorage(requireContext(), bitmap);
                            currentPlant.setCustomPhotoUri(savedPath);
                            viewModel.updatePlant(currentPlant);
                            Toast.makeText(requireContext(), "📷 Foto da planta atualizada!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            plantId = getArguments().getInt(ARG_PLANT_ID, 1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plant_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(PlantViewModel.class);

        ivDetailHero = view.findViewById(R.id.ivDetailHero);
        tvDetailRoomTag = view.findViewById(R.id.tvDetailRoomTag);
        tvDetailNickname = view.findViewById(R.id.tvDetailNickname);
        tvDetailPlantName = view.findViewById(R.id.tvDetailPlantName);
        tvDetailScientificName = view.findViewById(R.id.tvDetailScientificName);
        cardDetailHydrationAlert = view.findViewById(R.id.cardDetailHydrationAlert);
        tvDetailHydrationHeadline = view.findViewById(R.id.tvDetailHydrationHeadline);
        tvDetailUrgentBadge = view.findViewById(R.id.tvDetailUrgentBadge);
        btnDetailMarkWatered = view.findViewById(R.id.btnDetailMarkWatered);
        btnDetailFavorite = view.findViewById(R.id.btnDetailFavorite);

        tvDetailVitalFreq = view.findViewById(R.id.tvDetailVitalFreq);
        tvDetailVitalVolume = view.findViewById(R.id.tvDetailVitalVolume);
        tvDetailVitalLightBadge = view.findViewById(R.id.tvDetailVitalLightBadge);
        tvDetailVitalLight = view.findViewById(R.id.tvDetailVitalLight);
        tvDetailVitalLightSub = view.findViewById(R.id.tvDetailVitalLightSub);
        tvDetailVitalHumidity = view.findViewById(R.id.tvDetailVitalHumidity);
        tvDetailVitalHumidityTip = view.findViewById(R.id.tvDetailVitalHumidityTip);
        tvDetailVitalToxicity = view.findViewById(R.id.tvDetailVitalToxicity);
        tvDetailVitalToxicityTip = view.findViewById(R.id.tvDetailVitalToxicityTip);

        tvDetailSensorRing = view.findViewById(R.id.tvDetailSensorRing);
        tvDetailSubstrateTitle = view.findViewById(R.id.tvDetailSubstrateTitle);
        tvDetailSubstrateSub = view.findViewById(R.id.tvDetailSubstrateSub);
        tvDiagnosticSubtitle = view.findViewById(R.id.tvDiagnosticSubtitle);

        TextView btnUpdateDetailPhoto = view.findViewById(R.id.btnUpdateDetailPhoto);
        ImageButton btnDeletePlant = view.findViewById(R.id.btnDeletePlant);
        TextView btnDetailMeasureLight = view.findViewById(R.id.btnDetailMeasureLight);
        TextView btnOpenAddCareLog = view.findViewById(R.id.btnOpenAddCareLog);
        RecyclerView rvCareLogs = view.findViewById(R.id.rvCareLogs);

        // RecyclerView #3: Diário de Cuidados
        careLogAdapter = new CareLogAdapter();
        rvCareLogs.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCareLogs.setAdapter(careLogAdapter);

        // Observa dados da planta no Room via LiveData
        viewModel.getPlantById(plantId).observe(getViewLifecycleOwner(), plant -> {
            if (plant != null) {
                currentPlant = plant;
                bindPlantDetails(plant);
            }
        });

        // Observa logs do Diário de Cuidados
        viewModel.getLogsForPlant(plantId).observe(getViewLifecycleOwner(), logs -> {
            careLogAdapter.setLogs(logs);
        });

        // Botão Marcar como Regada Hoje
        btnDetailMarkWatered.setOnClickListener(v -> {
            if (currentPlant != null) {
                viewModel.waterPlantNow(currentPlant);
                Toast.makeText(requireContext(),
                        "💧 " + currentPlant.getName() + " marcada como regada hoje!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Favoritar planta
        btnDetailFavorite.setOnClickListener(v -> {
            if (currentPlant != null) {
                currentPlant.setFavorite(!currentPlant.isFavorite());
                viewModel.updatePlant(currentPlant);
                Toast.makeText(requireContext(),
                        currentPlant.isFavorite() ? "❤️ Adicionada às favoritas!" : "Removida das favoritas",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Atualizar foto via Câmera
        btnUpdateDetailPhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    updatePhotoLauncher.launch(intent);
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Câmera indisponível.", Toast.LENGTH_SHORT).show();
                }
            } else {
                ActivityCompatPermissionHelper();
            }
        });

        // Medir luz com Sensor (Abre Tela 4 LightSensorFragment)
        btnDetailMeasureLight.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToLightSensor();
            }
        });

        // Abrir Tela 5 (Registrar novo cuidado no Diário)
        btnOpenAddCareLog.setOnClickListener(v -> {
            if (currentPlant != null && getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToAddCareLog(currentPlant.getId(), currentPlant.getName());
            }
        });

        // Opção de Excluir Planta
        btnDeletePlant.setOnClickListener(v -> {
            if (currentPlant == null) return;
            new AlertDialog.Builder(requireContext())
                    .setTitle("Remover Planta")
                    .setMessage("Deseja remover " + currentPlant.getName() + " do seu Santuário Botânico?")
                    .setPositiveButton("Remover", (d, w) -> {
                        viewModel.deletePlant(currentPlant);
                        Toast.makeText(requireContext(), "Planta removida.", Toast.LENGTH_SHORT).show();
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).navigateToHome();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void ActivityCompatPermissionHelper() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, 202);
    }

    private void bindPlantDetails(PlantEntity plant) {
        PlantImageHelper.loadPlantImage(ivDetailHero, plant.getImageKey(), plant.getCustomPhotoUri());
        tvDetailRoomTag.setText("🛋️ " + plant.getRoom());
        tvDetailNickname.setText("\"" + plant.getNickname() + "\"");
        tvDetailPlantName.setText(plant.getName());
        tvDetailScientificName.setText(plant.getScientificName());

        btnDetailFavorite.setAlpha(plant.isFavorite() ? 1.0f : 0.45f);

        if (plant.needsWater()) {
            cardDetailHydrationAlert.setBackgroundResource(R.drawable.bg_alert_peach);
            tvDetailHydrationHeadline.setText("Precisa de água hoje");
            tvDetailUrgentBadge.setText("Urgente");
            tvDetailUrgentBadge.setBackgroundResource(R.drawable.bg_pill_urgent_red);
            btnDetailMarkWatered.setText("💧 Marcar como Regada Hoje");

            int displayMoisture = Math.max(plant.getHydrationPercent(), 28);
            tvDetailSensorRing.setText(displayMoisture + "%");
            tvDetailSubstrateTitle.setText("Substrato Seco");
            tvDetailSubstrateSub.setText("Ideal realizar rega lenta até escoar no prato.");
        } else {
            cardDetailHydrationAlert.setBackgroundResource(R.drawable.bg_mint_box);
            tvDetailHydrationHeadline.setText(plant.getStatusText());
            tvDetailUrgentBadge.setText("Em dia");
            tvDetailUrgentBadge.setBackgroundResource(R.drawable.bg_pill_green_filled);
            btnDetailMarkWatered.setText("✓ Registrar Nova Rega Hoje");

            tvDetailSensorRing.setText(plant.getHydrationPercent() + "%");
            tvDetailSubstrateTitle.setText("Substrato Úmido e Nutrido");
            tvDetailSubstrateSub.setText("Umidade do vaso equilibrada. Próxima rega em " + plant.getWateringFrequencyDays() + " dias.");
        }

        tvDetailVitalFreq.setText("A cada " + plant.getWateringFrequencyDays() + "-" + (plant.getWateringFrequencyDays() + 1) + " dias");
        tvDetailVitalVolume.setText(plant.getWaterVolumeTip());
        tvDetailVitalLightBadge.setText(plant.getLightBadge());
        tvDetailVitalLight.setText(plant.getLightRequirement());
        tvDetailVitalLightSub.setText(plant.getLightSubtext());
        tvDetailVitalHumidity.setText(plant.getHumidityRequirement());
        tvDetailVitalHumidityTip.setText(plant.getHumidityTip());
        tvDetailVitalToxicity.setText(plant.getPetToxicity());
        tvDetailVitalToxicityTip.setText(plant.getPetToxicityTip());
        tvDiagnosticSubtitle.setText("O que as folhas da sua " + plant.getName() + " estão dizendo?");
    }
}

