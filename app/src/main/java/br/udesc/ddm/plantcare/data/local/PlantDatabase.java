package br.udesc.ddm.plantcare.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.udesc.ddm.plantcare.data.local.dao.CareLogDao;
import br.udesc.ddm.plantcare.data.local.dao.PlantDao;
import br.udesc.ddm.plantcare.data.local.entity.CareLogEntity;
import br.udesc.ddm.plantcare.data.local.entity.PlantEntity;

@Database(entities = {PlantEntity.class, CareLogEntity.class}, version = 1, exportSchema = false)
public abstract class PlantDatabase extends RoomDatabase {

    public abstract PlantDao plantDao();
    public abstract CareLogDao careLogDao();

    private static volatile PlantDatabase INSTANCE;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static PlantDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PlantDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    PlantDatabase.class,
                                    "plantcare_database"
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                    seedInitialDataIfNeeded(INSTANCE);
                }
            }
        }
        return INSTANCE;
    }

    private static void seedInitialDataIfNeeded(PlantDatabase db) {
        databaseWriteExecutor.execute(() -> {
            PlantDao plantDao = db.plantDao();
            CareLogDao careLogDao = db.careLogDao();

            if (plantDao.getPlantCountSync() == 0) {
                // 1. Costela-de-Adão (Exatamente como no protótipo)
                PlantEntity monstera = new PlantEntity(
                        "Costela-de-Adão",
                        "Monstera deliciosa",
                        "Monstera da Sala",
                        "Sala de estar",
                        6,
                        15,
                        "Necessita de água hoje!",
                        "Solo quase seco",
                        "24/10/2026",
                        "Luz Brilhante",
                        "Sol matinal filtrado",
                        "Indireta",
                        "Alta (60-80%)",
                        "Borrife folhas 2x/semana",
                        "Tóxica",
                        "Mantenha longe de cães/gatos",
                        "Volume moderado (~400ml)",
                        "monstera",
                        "",
                        true,
                        true
                );
                long monsteraId = plantDao.insertPlant(monstera);

                // Logs do Diário de Cuidados da Costela-de-Adão (Ordem cronológica reversa ao exibir)
                careLogDao.insertLog(new CareLogEntity((int) monsteraId, "Há 26 dias • 04 Out",
                        "Rega matinal realizada com água filtrada", "Rotina", "WATER"));
                careLogDao.insertLog(new CareLogEntity((int) monsteraId, "Há 20 dias • 10 Out",
                        "Limpeza de folhas e poda leve de folhas secas", "Higiene", "PRUNE"));
                careLogDao.insertLog(new CareLogEntity((int) monsteraId, "Há 14 dias • 16 Out",
                        "Adubação orgânica NPK 10-10-10 aplicada", "Nutrição", "NUTRITION"));
                careLogDao.insertLog(new CareLogEntity((int) monsteraId, "Há 6 dias • 24 Out",
                        "Rega completa realizada (400ml)", "Por Você", "WATER"));

                // 2. Zamioculca
                PlantEntity zamioculca = new PlantEntity(
                        "Zamioculca",
                        "Zamioculcas zamiifolia",
                        "Guardiã do Escritório",
                        "Escritório",
                        14,
                        85,
                        "Hidratada (faltam 9 dias)",
                        "Solo nutrido",
                        "29/10/2026",
                        "Meia-sombra",
                        "Tolera ambientes internos",
                        "Pouca luz",
                        "Moderada (40-60%)",
                        "Evite encharcar o rizoma",
                        "Tóxica",
                        "Oxalato de cálcio nas folhas",
                        "Volume leve (~250ml)",
                        "zamioculca",
                        "",
                        false,
                        true
                );
                long zamioId = plantDao.insertPlant(zamioculca);
                careLogDao.insertLog(new CareLogEntity((int) zamioId, "Há 5 dias • 29 Out",
                        "Rega controlada realizada (250ml)", "Por Você", "WATER"));

                // 3. Samambaia
                PlantEntity samambaia = new PlantEntity(
                        "Samambaia",
                        "Nephrolepis exaltata",
                        "Verdejante da Varanda",
                        "Varanda",
                        2,
                        5,
                        "Rega urgente!",
                        "Substrato seco",
                        "30/10/2026",
                        "Luz Difusa",
                        "Sem vento forte direto",
                        "Meia-sombra",
                        "Muito Alta (70-85%)",
                        "Borrifar água diariamente",
                        "Segura",
                        "Pet Friendly para cães e gatos",
                        "Volume abundante (~500ml)",
                        "samambaia",
                        "",
                        true,
                        true
                );
                long samambaiaId = plantDao.insertPlant(samambaia);
                careLogDao.insertLog(new CareLogEntity((int) samambaiaId, "Há 4 dias • 30 Out",
                        "Nebulização das frondes e rega", "Rotina", "WATER"));

                // 4. Cacto Ouriço
                PlantEntity cacto = new PlantEntity(
                        "Cacto Ouriço",
                        "Echinocactus grusonii",
                        "Sol do Quarto",
                        "Janela do quarto",
                        15,
                        95,
                        "Excelente (faltam 12 dias)",
                        "Sol pleno aproveitado",
                        "01/11/2026",
                        "Sol Pleno",
                        "Mínimo 4h de sol direto",
                        "Direta",
                        "Baixa (20-40%)",
                        "Substrato arenoso bem drenado",
                        "Atenção",
                        "Espinhos rígidos afiados",
                        "Volume mínimo (~100ml)",
                        "cacto",
                        "",
                        false,
                        true
                );
                long cactoId = plantDao.insertPlant(cacto);
                careLogDao.insertLog(new CareLogEntity((int) cactoId, "Há 3 dias • 01 Nov",
                        "Rega quinzenal de cactáceas", "Por Você", "WATER"));
            }
        });
    }
}

