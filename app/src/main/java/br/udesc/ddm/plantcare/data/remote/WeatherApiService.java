package br.udesc.ddm.plantcare.data.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {

    // Exemplo: https://api.open-meteo.com/v1/forecast?latitude=-27.05&longitude=-49.52&current=temperature_2m,relative_humidity_2m,precipitation
    @GET("v1/forecast")
    Call<WeatherResponse> getCurrentWeather(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("current") String currentFields
    );
}

