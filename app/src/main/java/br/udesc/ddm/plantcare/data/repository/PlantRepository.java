package br.udesc.ddm.plantcare.data.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.udesc.ddm.plantcare.data.local.PlantDatabase;
import br.udesc.ddm.plantcare.data.local.dao.CareLogDao;
import br.udesc.ddm.plantcare.data.local.dao.PlantDao;
import br.udesc.ddm.plantcare.data.local.entity.CareLogEntity;
import br.udesc.ddm.plantcare.data.local.entity.PlantEntity;
import br.udesc.ddm.plantcare.data.local.entity.SpeciesModel;
import br.udesc.ddm.plantcare.data.remote.RetrofitClient;
import br.udesc.ddm.plantcare.data.remote.WeatherResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlantRepository {

    private final PlantDao plantDao;
    private final CareLogDao careLogDao;
    private final LiveData<List<PlantEntity>> allPlants;
    private final MutableLiveData<WeatherResponse.CurrentWeather> currentWeatherLiveData = new MutableLiveData<>();

    public PlantRepository(Application application) {
        PlantDatabase db = PlantDatabase.getDatabase(application);
        this.plantDao = db.plantDao();
        this.careLogDao = db.careLogDao();
        this.allPlants = plantDao.getAllPlants();
    }

    public LiveData<List<PlantEntity>> getAllPlants() {
        return allPlants;
    }

    public LiveData<PlantEntity> getPlantById(int plantId) {
        return plantDao.getPlantById(plantId);
    }

    public LiveData<List<CareLogEntity>> getLogsForPlant(int plantId) {
        return careLogDao.getLogsForPlant(plantId);
    }

    public LiveData<Integer> getTotalLogsCount() {
        return careLogDao.getTotalLogsCount();
    }

    public LiveData<WeatherResponse.CurrentWeather> getCurrentWeather() {
        return currentWeatherLiveData;
    }

    public void fetchWeatherFromApi(double latitude, double longitude) {
        RetrofitClient.getWeatherApi()
                .getCurrentWeather(latitude, longitude, "temperature_2m,relative_humidity_2m,precipitation")
                .enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getCurrent() != null) {
                            currentWeatherLiveData.postValue(response.body().getCurrent());
                        } else {
                            postFallbackWeather();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                        postFallbackWeather();
                    }
                });
    }

    private void postFallbackWeather() {
        if (currentWeatherLiveData.getValue() == null) {
            currentWeatherLiveData.postValue(new WeatherResponse.CurrentWeather(23.5, 68, 0.0));
        }
    }

    public void insertPlant(PlantEntity plant, String initialLogDesc) {
        PlantDatabase.databaseWriteExecutor.execute(() -> {
            long id = plantDao.insertPlant(plant);
            String todayFormatted = new SimpleDateFormat("dd MMM", new Locale("pt", "BR")).format(new Date());
            careLogDao.insertLog(new CareLogEntity(
                    (int) id,
                    "Hoje • " + todayFormatted,
                    initialLogDesc != null ? initialLogDesc : "Planta cadastrada no Santuário Botânico",
                    "Cadastro",
                    "ROUTINE"
            ));
        });
    }

    public void waterPlantNow(PlantEntity plant) {
        PlantDatabase.databaseWriteExecutor.execute(() -> {
            plant.setHydrationPercent(100);
            plant.setStatusText("Hidratada (faltam " + plant.getWateringFrequencyDays() + " dias)");
            plant.setSoilStatus("Solo úmido e nutrido");
            String dateStr = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR")).format(new Date());
            plant.setLastWateredDate(dateStr);
            plantDao.updatePlant(plant);

            String todayShort = new SimpleDateFormat("dd MMM", new Locale("pt", "BR")).format(new Date());
            careLogDao.insertLog(new CareLogEntity(
                    plant.getId(),
                    "Hoje • " + todayShort,
                    "Rega completa realizada (" + plant.getWaterVolumeTip() + ")",
                    "Por Você",
                    "WATER"
            ));
        });
    }

    public void updatePlant(PlantEntity plant) {
        PlantDatabase.databaseWriteExecutor.execute(() -> plantDao.updatePlant(plant));
    }

    public void deletePlant(PlantEntity plant) {
        PlantDatabase.databaseWriteExecutor.execute(() -> plantDao.deletePlant(plant));
    }

    public void insertCareLog(CareLogEntity log, boolean resetHydration, int plantId) {
        PlantDatabase.databaseWriteExecutor.execute(() -> {
            careLogDao.insertLog(log);
            if (resetHydration) {
                PlantEntity plant = plantDao.getPlantByIdSync(plantId);
                if (plant != null) {
                    plant.setHydrationPercent(100);
                    plant.setStatusText("Hidratada (faltam " + plant.getWateringFrequencyDays() + " dias)");
                    plant.setSoilStatus("Solo renovado e úmido");
                    plantDao.updatePlant(plant);
                }
            }
        });
    }

    public List<SpeciesModel> getCatalogSpecies() {
        List<SpeciesModel> list = new ArrayList<>();
        list.add(new SpeciesModel(
                "Zamioculca",
                "Zamioculcas zamiifolia",
                14,
                "Pouca luz",
                "Muito Fácil",
                "Rega a cada 14 dias • Ideal para salas e escritórios com sombra parcial.",
                "Moderada (40-60%)",
                "Tóxica para pets",
                "zamioculca"
        ));
        list.add(new SpeciesModel(
                "Jiboia",
                "Epipremnum aureum",
                6,
                "Difusa",
                "Fácil",
                "Rega a cada 6 dias • Crescimento rápido e pendente, purifica o ar.",
                "Alta (60-75%)",
                "Tóxica para pets",
                "jiboia"
        ));
        list.add(new SpeciesModel(
                "Costela-de-Adão",
                "Monstera deliciosa",
                6,
                "Luz Brilhante",
                "Fácil",
                "Rega a cada 6-7 dias • Gosta de luz indireta brilhante e borrifadas nas folhas.",
                "Alta (60-80%)",
                "Tóxica para pets",
                "monstera"
        ));
        list.add(new SpeciesModel(
                "Samambaia",
                "Nephrolepis exaltata",
                2,
                "Meia-sombra",
                "Moderado",
                "Rega a cada 2 dias • Exige substrato sempre levemente úmido e abrigo de vento.",
                "Muito Alta (70-85%)",
                "Segura (Pet Friendly)",
                "samambaia"
        ));
        list.add(new SpeciesModel(
                "Cacto Ouriço",
                "Echinocactus grusonii",
                15,
                "Sol Pleno",
                "Muito Fácil",
                "Rega a cada 15 dias • Necessita de sol direto e drenagem rápida.",
                "Baixa (20-40%)",
                "Atenção (Espinhos)",
                "cacto"
        ));
        return list;
    }
}

