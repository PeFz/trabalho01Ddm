package br.udesc.ddm.plantcare.data.local.entity;

public class SpeciesModel {
    private final String name;
    private final String scientificName;
    private final int frequencyDays;
    private final String lightLabel;
    private final String difficultyBadge;
    private final String careSummary;
    private final String humidityLabel;
    private final String toxicityLabel;
    private final String imageKey;

    public SpeciesModel(String name, String scientificName, int frequencyDays,
                        String lightLabel, String difficultyBadge, String careSummary,
                        String humidityLabel, String toxicityLabel, String imageKey) {
        this.name = name;
        this.scientificName = scientificName;
        this.frequencyDays = frequencyDays;
        this.lightLabel = lightLabel;
        this.difficultyBadge = difficultyBadge;
        this.careSummary = careSummary;
        this.humidityLabel = humidityLabel;
        this.toxicityLabel = toxicityLabel;
        this.imageKey = imageKey;
    }

    public String getName() { return name; }
    public String getScientificName() { return scientificName; }
    public int getFrequencyDays() { return frequencyDays; }
    public String getLightLabel() { return lightLabel; }
    public String getDifficultyBadge() { return difficultyBadge; }
    public String getCareSummary() { return careSummary; }
    public String getHumidityLabel() { return humidityLabel; }
    public String getToxicityLabel() { return toxicityLabel; }
    public String getImageKey() { return imageKey; }

    @Override
    public String toString() {
        return name + " (" + scientificName + ")";
    }
}

