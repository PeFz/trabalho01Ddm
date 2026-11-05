package br.udesc.ddm.plantcare.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import br.udesc.ddm.plantcare.R;
import br.udesc.ddm.plantcare.data.local.entity.PlantEntity;
import br.udesc.ddm.plantcare.data.local.entity.SpeciesModel;
import br.udesc.ddm.plantcare.ui.MainActivity;
import br.udesc.ddm.plantcare.ui.adapter.PopularSpeciesAdapter;
import br.udesc.ddm.plantcare.utils.PlantImageHelper;
import br.udesc.ddm.plantcare.viewmodel.PlantViewModel;

public class AddPlantFragment extends Fragment {

    private PlantViewModel viewModel;
    private List<SpeciesModel> catalogSpecies;
    private SpeciesModel selectedSpecies;

    private ImageView ivNewPlantPreview;
    private TextView tvPhotoHint;
    private Spinner spinnerSpecies;
    private AutoCompleteTextView autoCompleteSpecies;
    private TextView tvSelectedSpeciesDifficulty;
    private TextView tvSelectedSpeciesCareInfo;
    private EditText etPlantNickname;
    private EditText etCustomWaterDate;
    private CheckBox cbEnablePlantReminder;

    private TextView btnRoomSala, btnRoomQuarto, btnRoomVaranda, btnRoomCozinha, btnRoomEscritorio;
    private TextView btnWaterDateToday, btnWaterDateYesterday, btnWaterDate3Days;

    private String selectedRoom = "Sala de estar";
    private String selectedWaterDateOption = "Hoje";
    private int initialHydrationPercent = 95;
    private String customPhotoPath = "";
    private final String[] illustrationKeys = {"samambaia", "monstera", "zamioculca", "cacto", "jiboia"};
    private int currentIllustrationIndex = 0;

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        if (imageBitmap != null) {
                            ivNewPlantPreview.setImageBitmap(imageBitmap);
                            customPhotoPath = PlantImageHelper.saveCameraBitmapToInternalStorage(requireContext(), imageBitmap);
                            tvPhotoHint.setText("✓ Foto real capturada pela câmera com sucesso!");
                            Toast.makeText(requireContext(), "📷 Foto da planta capturada!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    private final ActivityResultLauncher<String> cameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openCameraIntent();
                } else {
                    Toast.makeText(requireContext(), "Permissão de câmera necessária para tirar foto.", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_plant, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(PlantViewModel.class);
        catalogSpecies = viewModel.getCatalogSpecies();
        selectedSpecies = catalogSpecies.get(0);

        ivNewPlantPreview = view.findViewById(R.id.ivNewPlantPreview);
        tvPhotoHint = view.findViewById(R.id.tvPhotoHint);
        Button btnCapturePhoto = view.findViewById(R.id.btnCapturePhoto);
        ImageButton btnCycleIllustration = view.findViewById(R.id.btnCycleIllustration);
        TextView tvOpenCatalogLink = view.findViewById(R.id.tvOpenCatalogLink);
        TextView btnViewAllSpecies = view.findViewById(R.id.btnViewAllSpecies);

        spinnerSpecies = view.findViewById(R.id.spinnerSpecies);
        autoCompleteSpecies = view.findViewById(R.id.autoCompleteSpecies);
        tvSelectedSpeciesDifficulty = view.findViewById(R.id.tvSelectedSpeciesDifficulty);
        tvSelectedSpeciesCareInfo = view.findViewById(R.id.tvSelectedSpeciesCareInfo);
        etPlantNickname = view.findViewById(R.id.etPlantNickname);
        etCustomWaterDate = view.findViewById(R.id.etCustomWaterDate);
        cbEnablePlantReminder = view.findViewById(R.id.cbEnablePlantReminder);

        btnRoomSala = view.findViewById(R.id.btnRoomSala);
        btnRoomQuarto = view.findViewById(R.id.btnRoomQuarto);
        btnRoomVaranda = view.findViewById(R.id.btnRoomVaranda);
        btnRoomCozinha = view.findViewById(R.id.btnRoomCozinha);
        btnRoomEscritorio = view.findViewById(R.id.btnRoomEscritorio);

        btnWaterDateToday = view.findViewById(R.id.btnWaterDateToday);
        btnWaterDateYesterday = view.findViewById(R.id.btnWaterDateYesterday);
        btnWaterDate3Days = view.findViewById(R.id.btnWaterDate3Days);

        Button btnSaveNewPlant = view.findViewById(R.id.btnSaveNewPlant);
        RecyclerView rvPopularSpecies = view.findViewById(R.id.rvPopularSpecies);

        // Configura Spinner e AutoCompleteTextView de Espécies
        ArrayAdapter<SpeciesModel> speciesAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                catalogSpecies
        );
        spinnerSpecies.setAdapter(speciesAdapter);
        autoCompleteSpecies.setAdapter(speciesAdapter);

        spinnerSpecies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                applySpeciesSelection(catalogSpecies.get(position), false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        autoCompleteSpecies.setOnItemClickListener((parent, v, position, id) -> {
            SpeciesModel chosen = (SpeciesModel) parent.getItemAtPosition(position);
            if (chosen != null) {
                applySpeciesSelection(chosen, true);
            }
        });

        // Observa se o usuário selecionou uma espécie vinda da Tela 6 (Catálogo Botânico)
        viewModel.getPreselectedSpecies().observe(getViewLifecycleOwner(), preselected -> {
            if (preselected != null) {
                applySpeciesSelection(preselected, true);
                viewModel.setPreselectedSpecies(null);
            }
        });

        // Botão Câmera
        btnCapturePhoto.setOnClickListener(v -> checkCameraPermissionAndCapture());

        // Botão alternar ilustração botânica
        btnCycleIllustration.setOnClickListener(v -> {
            customPhotoPath = "";
            currentIllustrationIndex = (currentIllustrationIndex + 1) % illustrationKeys.length;
            String key = illustrationKeys[currentIllustrationIndex];
            ivNewPlantPreview.setImageResource(PlantImageHelper.getDrawableForKey(key));
            tvPhotoHint.setText("Ilustração botânica selecionada: " + key);
        });

        // Links para abrir a Tela 6 (Catálogo de Espécies)
        View.OnClickListener openCatalogListener = v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToSpeciesCatalog();
            }
        };
        tvOpenCatalogLink.setOnClickListener(openCatalogListener);
        btnViewAllSpecies.setOnClickListener(openCatalogListener);

        // Seleção de cômodo
        setupRoomButtons();

        // Seleção de data da última rega
        setupWaterDateButtons();

        // RecyclerView #2 Horizontal: Espécies Populares
        PopularSpeciesAdapter popularAdapter = new PopularSpeciesAdapter(catalogSpecies, species -> {
            applySpeciesSelection(species, true);
            Toast.makeText(requireContext(),
                    "🌱 " + species.getName() + " selecionada!",
                    Toast.LENGTH_SHORT).show();
        });
        rvPopularSpecies.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPopularSpecies.setAdapter(popularAdapter);

        // Salvar Nova Planta com ProgressDialog e Toast
        btnSaveNewPlant.setOnClickListener(v -> saveNewPlant());
    }

    private void applySpeciesSelection(SpeciesModel species, boolean syncSpinner) {
        selectedSpecies = species;
        tvSelectedSpeciesDifficulty.setText(species.getDifficultyBadge());
        tvSelectedSpeciesCareInfo.setText(species.getCareSummary());

        if (customPhotoPath.isEmpty()) {
            ivNewPlantPreview.setImageResource(PlantImageHelper.getDrawableForKey(species.getImageKey()));
        }

        if (syncSpinner) {
            for (int i = 0; i < catalogSpecies.size(); i++) {
                if (catalogSpecies.get(i).getName().equalsIgnoreCase(species.getName())) {
                    spinnerSpecies.setSelection(i);
                    break;
                }
            }
        }
    }

    private void checkCameraPermissionAndCapture() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCameraIntent();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            cameraLauncher.launch(takePictureIntent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Câmera indisponível neste emulador.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRoomButtons() {
        TextView[] roomButtons = {btnRoomSala, btnRoomQuarto, btnRoomVaranda, btnRoomCozinha, btnRoomEscritorio};
        String[] roomNames = {"Sala de estar", "Quarto", "Varanda", "Cozinha", "Escritório"};

        for (int i = 0; i < roomButtons.length; i++) {
            final int index = i;
            roomButtons[i].setOnClickListener(v -> {
                selectedRoom = roomNames[index];
                Context ctx = requireContext();
                for (TextView b : roomButtons) {
                    b.setBackgroundResource(R.drawable.bg_card_warm);
                    b.setTextColor(ContextCompat.getColor(ctx, R.color.text_primary));
                }
                roomButtons[index].setBackgroundResource(R.drawable.bg_pill_green_filled);
                roomButtons[index].setTextColor(ContextCompat.getColor(ctx, R.color.white));
            });
        }
    }

    private void setupWaterDateButtons() {
        btnWaterDateToday.setOnClickListener(v -> {
            selectedWaterDateOption = "Hoje";
            initialHydrationPercent = 95;
            highlightDateButton(btnWaterDateToday);
        });

        btnWaterDateYesterday.setOnClickListener(v -> {
            selectedWaterDateOption = "Ontem";
            initialHydrationPercent = 75;
            highlightDateButton(btnWaterDateYesterday);
        });

        btnWaterDate3Days.setOnClickListener(v -> {
            selectedWaterDateOption = "Há 3 dias";
            initialHydrationPercent = 20;
            highlightDateButton(btnWaterDate3Days);
        });

        etCustomWaterDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        String dateFormatted = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                        etCustomWaterDate.setText(dateFormatted);
                        selectedWaterDateOption = dateFormatted;
                        initialHydrationPercent = 40;
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });
    }

    private void highlightDateButton(TextView activeBtn) {
        Context ctx = requireContext();
        TextView[] btns = {btnWaterDateToday, btnWaterDateYesterday, btnWaterDate3Days};
        for (TextView b : btns) {
            b.setBackgroundResource(R.drawable.bg_card_warm);
            b.setTextColor(ContextCompat.getColor(ctx, R.color.text_primary));
        }
        activeBtn.setBackgroundResource(R.drawable.bg_day_selected);
        activeBtn.setTextColor(ContextCompat.getColor(ctx, R.color.white));
    }

    @SuppressWarnings("deprecation")
    private void saveNewPlant() {
        String nicknameInput = etPlantNickname.getText().toString().trim();
        String nickname = nicknameInput.isEmpty()
                ? selectedSpecies.getName() + " da " + selectedRoom
                : nicknameInput;

        boolean needsWater = initialHydrationPercent <= 30;
        String statusText = needsWater
                ? "Necessita de água hoje!"
                : "Hidratada (faltam " + selectedSpecies.getFrequencyDays() + " dias)";
        String soilStatus = needsWater ? "Solo quase seco" : "Solo nutrido e úmido";

        PlantEntity newPlant = new PlantEntity(
                selectedSpecies.getName(),
                selectedSpecies.getScientificName(),
                nickname,
                selectedRoom,
                selectedSpecies.getFrequencyDays(),
                initialHydrationPercent,
                statusText,
                soilStatus,
                selectedWaterDateOption,
                selectedSpecies.getLightLabel(),
                "Luz natural calibrada",
                selectedSpecies.getLightLabel(),
                selectedSpecies.getHumidityLabel(),
                "Mantenha ventilação suave",
                selectedSpecies.getToxicityLabel(),
                "Cuidado ao manusear folhas",
                "Volume moderado (~350ml)",
                selectedSpecies.getImageKey(),
                customPhotoPath,
                false,
                cbEnablePlantReminder.isChecked()
        );

        // Uso de ProgressDialog conforme listado na especificação do trabalho
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Santuário Botânico");
        progressDialog.setMessage("Calibrando evapotranspiração e salvando no banco Room...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        viewModel.insertPlant(newPlant, "Planta cadastrada no cômodo " + selectedRoom + " (Última rega: " + selectedWaterDateOption + ")");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (isAdded()) {
                Toast.makeText(requireContext(),
                        "🌿 " + selectedSpecies.getName() + " adicionada ao seu Santuário!",
                        Toast.LENGTH_LONG).show();
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).navigateToHome();
                }
            }
        }, 650);
    }
}

