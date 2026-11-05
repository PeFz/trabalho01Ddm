package br.udesc.ddm.plantcare.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Locale;

import br.udesc.ddm.plantcare.R;
import br.udesc.ddm.plantcare.viewmodel.PlantViewModel;

public class LightSensorFragment extends Fragment implements SensorEventListener {

    private PlantViewModel viewModel;
    private SensorManager sensorManager;
    private Sensor lightSensor;

    private TextView tvSensorHardwareStatus;
    private TextView tvLuxValue;
    private TextView tvLuxClassification;
    private TextView tvLuxPlantRecommendation;
    private ProgressBar pbLuxGauge;
    private SeekBar seekBarLuxSimulator;
    private ToggleButton toggleSimulateSensor;

    private TextView tvApiTemperature;
    private TextView tvApiHumidity;
    private TextView tvApiBotanicalAdvice;
    private Spinner spinnerWeatherCity;

    private final String[] cityNames = {
            "Ibirama - SC (UDESC Alto Vale)",
            "Florianópolis - SC",
            "Blumenau - SC",
            "Joinville - SC",
            "Curitiba - PR"
    };
    private final double[][] cityCoords = {
            {-27.0569, -49.5178},
            {-27.5954, -48.5480},
            {-26.9194, -49.0661},
            {-26.3044, -48.8487},
            {-25.4284, -49.2733}
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_light_sensor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(PlantViewModel.class);

        tvSensorHardwareStatus = view.findViewById(R.id.tvSensorHardwareStatus);
        tvLuxValue = view.findViewById(R.id.tvLuxValue);
        tvLuxClassification = view.findViewById(R.id.tvLuxClassification);
        tvLuxPlantRecommendation = view.findViewById(R.id.tvLuxPlantRecommendation);
        pbLuxGauge = view.findViewById(R.id.pbLuxGauge);
        seekBarLuxSimulator = view.findViewById(R.id.seekBarLuxSimulator);
        toggleSimulateSensor = view.findViewById(R.id.toggleSimulateSensor);

        tvApiTemperature = view.findViewById(R.id.tvApiTemperature);
        tvApiHumidity = view.findViewById(R.id.tvApiHumidity);
        tvApiBotanicalAdvice = view.findViewById(R.id.tvApiBotanicalAdvice);
        spinnerWeatherCity = view.findViewById(R.id.spinnerWeatherCity);
        Button btnRefreshWeatherApi = view.findViewById(R.id.btnRefreshWeatherApi);

        // Inicializa Sensor Físico de Luz (Sensor.TYPE_LIGHT)
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }

        if (lightSensor != null) {
            tvSensorHardwareStatus.setText("Sensor óptico ativo: " + lightSensor.getName());
        } else {
            tvSensorHardwareStatus.setText("Sensor óptico ausente no emulador (Modo Simulador ativo)");
            toggleSimulateSensor.setChecked(true);
        }

        updateLuxUI(450f);

        seekBarLuxSimulator.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    toggleSimulateSensor.setChecked(true);
                    updateLuxUI(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Configura Spinner de Cidades para a API Retrofit
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                cityNames
        );
        spinnerWeatherCity.setAdapter(cityAdapter);

        spinnerWeatherCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                double[] coords = cityCoords[position];
                viewModel.refreshWeather(coords[0], coords[1]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Observa resposta da API REST via Retrofit
        viewModel.getCurrentWeather().observe(getViewLifecycleOwner(), weather -> {
            if (weather != null) {
                tvApiTemperature.setText(String.format(Locale.getDefault(), "%.1f °C", weather.getTemperature()));
                tvApiHumidity.setText(weather.getHumidity() + "%");
                tvApiBotanicalAdvice.setText(weather.getBotanicalAdvice());
            }
        });

        // Botão Sincronizar Clima via Retrofit com ProgressDialog
        btnRefreshWeatherApi.setOnClickListener(v -> {
            int pos = spinnerWeatherCity.getSelectedItemPosition();
            double[] coords = cityCoords[Math.max(0, pos)];

            @SuppressWarnings("deprecation")
            ProgressDialog pd = new ProgressDialog(requireContext());
            pd.setTitle("API Meteorológica (Retrofit)");
            pd.setMessage("Consultando temperatura e umidade em Open-Meteo...");
            pd.setCancelable(false);
            pd.show();

            viewModel.refreshWeather(coords[0], coords[1]);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (pd.isShowing()) pd.dismiss();
                if (isAdded()) {
                    Toast.makeText(requireContext(),
                            "🌤️ Dados climáticos sincronizados via Retrofit!",
                            Toast.LENGTH_SHORT).show();
                }
            }, 600);
        });
    }

    private void updateLuxUI(float lux) {
        int luxInt = Math.round(lux);
        tvLuxValue.setText(luxInt + " lx");
        pbLuxGauge.setProgress(Math.min(luxInt, 2000));
        seekBarLuxSimulator.setProgress(Math.min(luxInt, 2000));

        if (lux < 200) {
            tvLuxClassification.setText("Sombra / Pouca Luz (< 200 lx)");
            tvLuxPlantRecommendation.setText(
                    "Ambiente de baixa luminosidade. Ideal para Zamioculca (Zamioculcas zamiifolia) e plantas de sombra. Evite colocar Cacto Ouriço aqui."
            );
        } else if (lux <= 1000) {
            tvLuxClassification.setText("Luz Indireta Brilhante (200 - 1000 lx)");
            tvLuxPlantRecommendation.setText(
                    "Iluminação excelente! Perfeita para Costela-de-Adão (Monstera deliciosa), Jiboia e Samambaia."
            );
        } else {
            tvLuxClassification.setText("Sol Pleno / Luz Intensa (> 1000 lx)");
            tvLuxPlantRecommendation.setText(
                    "Alta incidência solar! Perfeito para Cacto Ouriço (Echinocactus grusonii) e suculentas. Pode queimar folhas de Samambaia."
            );
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null && lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT && !toggleSimulateSensor.isChecked()) {
            float lux = event.values[0];
            updateLuxUI(lux);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Não requer ação adicional
    }
}

