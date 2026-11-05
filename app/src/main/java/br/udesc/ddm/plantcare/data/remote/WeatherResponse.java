package br.udesc.ddm.plantcare.data.remote;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {

    @SerializedName("current")
    private CurrentWeather current;

    public CurrentWeather getCurrent() {
        return current;
    }

    public void setCurrent(CurrentWeather current) {
        this.current = current;
    }

    public static class CurrentWeather {
        @SerializedName("temperature_2m")
        private double temperature;

        @SerializedName("relative_humidity_2m")
        private int humidity;

        @SerializedName("precipitation")
        private double precipitation;

        public CurrentWeather(double temperature, int humidity, double precipitation) {
            this.temperature = temperature;
            this.humidity = humidity;
            this.precipitation = precipitation;
        }

        public double getTemperature() { return temperature; }
        public int getHumidity() { return humidity; }
        public double getPrecipitation() { return precipitation; }

        public String getBotanicalAdvice() {
            if (humidity < 45) {
                return "Ar seco (" + humidity + "%): Borrife água nas folhas de Samambaia e Costela-de-Adão.";
            } else if (temperature > 28) {
                return "Calor elevado (" + String.format("%.1f°C", temperature) + "): Evapotranspiração acelerada hoje.";
            } else {
                return "Clima ameno (" + String.format("%.1f°C", temperature) + " • " + humidity + "% umid.): Condição ideal para o seu refúgio verde.";
            }
        }
    }
}

