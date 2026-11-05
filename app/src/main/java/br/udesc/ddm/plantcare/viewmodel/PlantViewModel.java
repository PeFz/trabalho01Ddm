package br.udesc.ddm.plantcare.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import br.udesc.ddm.plantcare.data.local.entity.CareLogEntity;
import br.udesc.ddm.plantcare.data.local.entity.PlantEntity;
import br.udesc.ddm.plantcare.data.local.entity.SpeciesModel;
import br.udesc.ddm.plantcare.data.remote.WeatherResponse;
import br.udesc.ddm.plantcare.data.repository.PlantRepository;

public class PlantViewModel extends AndroidViewModel {

    private final PlantRepository repository;
    private final LiveData<List<PlantEntity>> allPlants;
    private final MutableLiveData<Integer> selectedPlantId = new MutableLiveData<>(1);
    private final MutableLiveData<SpeciesModel> preselectedSpecies = new MutableLiveData<>(null);

    public PlantViewModel(@NonNull Application application) {
        super(application);
        repository = new PlantRepository(application);
        allPlants = repository.getAllPlants();
        // Busca clima inicial do Alto Vale / Ibirama (-27.05, -49.52) via Retrofit
        repository.fetchWeatherFromApi(-27.05, -49.52);
    }

    public LiveData<List<PlantEntity>> getAllPlants() {
        return allPlants;
    }

    public LiveData<PlantEntity> getPlantById(int plantId) {
        return repository.getPlantById(plantId);
    }

    public LiveData<List<CareLogEntity>> getLogsForPlant(int plantId) {
        return repository.getLogsForPlant(plantId);
    }

    public LiveData<Integer> getTotalLogsCount() {
        return repository.getTotalLogsCount();
    }

    public LiveData<WeatherResponse.CurrentWeather> getCurrentWeather() {
        return repository.getCurrentWeather();
    }

    public void refreshWeather(double lat, double lon) {
        repository.fetchWeatherFromApi(lat, lon);
    }

    public void setSelectedPlantId(int id) {
        selectedPlantId.setValue(id);
    }

    public LiveData<Integer> getSelectedPlantId() {
        return selectedPlantId;
    }

    public void setPreselectedSpecies(SpeciesModel species) {
        preselectedSpecies.setValue(species);
    }

    public LiveData<SpeciesModel> getPreselectedSpecies() {
        return preselectedSpecies;
    }

    public void insertPlant(PlantEntity plant, String initialLogDesc) {
        repository.insertPlant(plant, initialLogDesc);
    }

    public void waterPlantNow(PlantEntity plant) {
        repository.waterPlantNow(plant);
    }

    public void updatePlant(PlantEntity plant) {
        repository.updatePlant(plant);
    }

    public void deletePlant(PlantEntity plant) {
        repository.deletePlant(plant);
    }

    public void insertCareLog(CareLogEntity log, boolean resetHydration, int plantId) {
        repository.insertCareLog(log, resetHydration, plantId);
    }

    public List<SpeciesModel> getCatalogSpecies() {
        return repository.getCatalogSpecies();
    }
}

