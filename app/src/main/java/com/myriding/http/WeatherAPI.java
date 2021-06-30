package com.myriding.http;


import com.myriding.model.Weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPI {
    @GET("weather?")
    Call<Weather> getWeather(
        @Query("lat") double lat,
        @Query("lon") double lon,
        @Query("appid") String key,
        @Query("units") String type
    );
}
