package com.example.covidtracker;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiInterface {
    String BASE_URL="https://disease.sh/v3/covid-19/";

    @GET("countries")
    Call<List<ModelClass>>getCountrydata();

}
