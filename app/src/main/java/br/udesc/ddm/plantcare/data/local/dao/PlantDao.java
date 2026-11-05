package br.udesc.ddm.plantcare.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.udesc.ddm.plantcare.data.local.entity.PlantEntity;

@Dao
public interface PlantDao {

    @Query("SELECT * FROM plants ORDER BY id ASC")
    LiveData<List<PlantEntity>> getAllPlants();

    @Query("SELECT * FROM plants ORDER BY id ASC")
    List<PlantEntity> getAllPlantsSync();

    @Query("SELECT * FROM plants WHERE id = :plantId LIMIT 1")
    LiveData<PlantEntity> getPlantById(int plantId);

    @Query("SELECT * FROM plants WHERE id = :plantId LIMIT 1")
    PlantEntity getPlantByIdSync(int plantId);

    @Query("SELECT COUNT(*) FROM plants")
    int getPlantCountSync();

    @Insert
    long insertPlant(PlantEntity plant);

    @Update
    void updatePlant(PlantEntity plant);

    @Delete
    void deletePlant(PlantEntity plant);
}

