package com.arifulislam.tourguidepro.services;



import com.arifulislam.tourguidepro.responses.DirectionResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;



public interface DirectionService {

    @GET
    Call<DirectionResponse> getDirections(@Url String urlString);
}

