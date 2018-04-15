package com.arifulislam.tourguidepro.services;

import com.arifulislam.tourguidepro.responses.PlaceResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;


public interface PlaceService {
    @GET
    Call<PlaceResponse> getPlacesResponse(@Url String url);
}
