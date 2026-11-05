package br.udesc.ddm.plantcare.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import br.udesc.ddm.plantcare.data.local.entity.CareLogEntity;

@Dao
public interface CareLogDao {

    @Query("SELECT * FROM care_logs WHERE plantId = :plantId ORDER BY id DESC")
    LiveData<List<CareLogEntity>> getLogsForPlant(int plantId);

    @Query("SELECT COUNT(*) FROM care_logs")
    LiveData<Integer> getTotalLogsCount();

    @Insert
    void insertLog(CareLogEntity log);
}

