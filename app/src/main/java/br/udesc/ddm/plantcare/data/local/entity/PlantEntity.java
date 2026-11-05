package br.udesc.ddm.plantcare.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "plants")
public class PlantEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String scientificName;
    private String nickname;
    private String room;
    private int wateringFrequencyDays;
    private int hydrationPercent;
    private String statusText;
    private String soilStatus;
    private String lastWateredDate;
    private String lightRequirement;
    private String lightSubtext;
    private String lightBadge;
    private String humidityRequirement;
    private String humidityTip;
    private String petToxicity;
    private String petToxicityTip;
    private String waterVolumeTip;
    private String imageKey;
    private String customPhotoUri;
    private boolean isFavorite;
    private boolean reminderEnabled;

    public PlantEntity(String name, String scientificName, String nickname, String room,
                       int wateringFrequencyDays, int hydrationPercent, String statusText,
                       String soilStatus, String lastWateredDate, String lightRequirement,
                       String lightSubtext, String lightBadge, String humidityRequirement,
                       String humidityTip, String petToxicity, String petToxicityTip,
                       String waterVolumeTip, String imageKey, String customPhotoUri,
                       boolean isFavorite, boolean reminderEnabled) {
        this.name = name;
        this.scientificName = scientificName;
        this.nickname = nickname;
        this.room = room;
        this.wateringFrequencyDays = wateringFrequencyDays;
        this.hydrationPercent = hydrationPercent;
        this.statusText = statusText;
        this.soilStatus = soilStatus;
        this.lastWateredDate = lastWateredDate;
        this.lightRequirement = lightRequirement;
        this.lightSubtext = lightSubtext;
        this.lightBadge = lightBadge;
        this.humidityRequirement = humidityRequirement;
        this.humidityTip = humidityTip;
        this.petToxicity = petToxicity;
        this.petToxicityTip = petToxicityTip;
        this.waterVolumeTip = waterVolumeTip;
        this.imageKey = imageKey;
        this.customPhotoUri = customPhotoUri;
        this.isFavorite = isFavorite;
        this.reminderEnabled = reminderEnabled;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getScientificName() { return scientificName; }
    public void setScientificName(String scientificName) { this.scientificName = scientificName; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public int getWateringFrequencyDays() { return wateringFrequencyDays; }
    public void setWateringFrequencyDays(int wateringFrequencyDays) { this.wateringFrequencyDays = wateringFrequencyDays; }

    public int getHydrationPercent() { return hydrationPercent; }
    public void setHydrationPercent(int hydrationPercent) { this.hydrationPercent = hydrationPercent; }

    public String getStatusText() { return statusText; }
    public void setStatusText(String statusText) { this.statusText = statusText; }

    public String getSoilStatus() { return soilStatus; }
    public void setSoilStatus(String soilStatus) { this.soilStatus = soilStatus; }

    public String getLastWateredDate() { return lastWateredDate; }
    public void setLastWateredDate(String lastWateredDate) { this.lastWateredDate = lastWateredDate; }

    public String getLightRequirement() { return lightRequirement; }
    public void setLightRequirement(String lightRequirement) { this.lightRequirement = lightRequirement; }

    public String getLightSubtext() { return lightSubtext; }
    public void setLightSubtext(String lightSubtext) { this.lightSubtext = lightSubtext; }

    public String getLightBadge() { return lightBadge; }
    public void setLightBadge(String lightBadge) { this.lightBadge = lightBadge; }

    public String getHumidityRequirement() { return humidityRequirement; }
    public void setHumidityRequirement(String humidityRequirement) { this.humidityRequirement = humidityRequirement; }

    public String getHumidityTip() { return humidityTip; }
    public void setHumidityTip(String humidityTip) { this.humidityTip = humidityTip; }

    public String getPetToxicity() { return petToxicity; }
    public void setPetToxicity(String petToxicity) { this.petToxicity = petToxicity; }

    public String getPetToxicityTip() { return petToxicityTip; }
    public void setPetToxicityTip(String petToxicityTip) { this.petToxicityTip = petToxicityTip; }

    public String getWaterVolumeTip() { return waterVolumeTip; }
    public void setWaterVolumeTip(String waterVolumeTip) { this.waterVolumeTip = waterVolumeTip; }

    public String getImageKey() { return imageKey; }
    public void setImageKey(String imageKey) { this.imageKey = imageKey; }

    public String getCustomPhotoUri() { return customPhotoUri; }
    public void setCustomPhotoUri(String customPhotoUri) { this.customPhotoUri = customPhotoUri; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public boolean isReminderEnabled() { return reminderEnabled; }
    public void setReminderEnabled(boolean reminderEnabled) { this.reminderEnabled = reminderEnabled; }

    public boolean needsWater() {
        return hydrationPercent <= 30;
    }
}

