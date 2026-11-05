package br.udesc.ddm.plantcare.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "care_logs")
public class CareLogEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int plantId;
    private String timeAgoAndDate;
    private String description;
    private String categoryBadge;
    private String iconType;

    public CareLogEntity(int plantId, String timeAgoAndDate, String description, String categoryBadge, String iconType) {
        this.plantId = plantId;
        this.timeAgoAndDate = timeAgoAndDate;
        this.description = description;
        this.categoryBadge = categoryBadge;
        this.iconType = iconType;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPlantId() { return plantId; }
    public void setPlantId(int plantId) { this.plantId = plantId; }

    public String getTimeAgoAndDate() { return timeAgoAndDate; }
    public void setTimeAgoAndDate(String timeAgoAndDate) { this.timeAgoAndDate = timeAgoAndDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategoryBadge() { return categoryBadge; }
    public void setCategoryBadge(String categoryBadge) { this.categoryBadge = categoryBadge; }

    public String getIconType() { return iconType; }
    public void setIconType(String iconType) { this.iconType = iconType; }
}

