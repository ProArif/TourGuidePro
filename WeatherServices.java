package com.arifulislam.tourguidepro.services;




import com.arifulislam.tourguidepro.responses.CurrentWeatherResponse;
import com.arifulislam.tourguidepro.responses.ForecastResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;



public interface WeatherServices {

    @GET()
    Call<CurrentWeatherResponse> getCurrentWeatherData(@Url String url);

    @GET()
    Call<ForecastResponse> getForecastWeatherData(@Url String url);
}
