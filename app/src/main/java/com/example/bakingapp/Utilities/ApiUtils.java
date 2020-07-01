package com.example.bakingapp.Utilities;

import com.example.bakingapp.Modal.Recipe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class ApiUtils {
    private static final String TAG = ApiUtils.class.getSimpleName();

    private static Retrofit retrofit = null;

    // Base URL of API endpoint
    public static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";

    // Interface
    public interface RecipeApiUtils {

        // Create a call with method GET with the given endpoint
        @GET("baking.json")
        Call<ArrayList<Recipe>> fetchRecipes();

    }


    public static Call<ArrayList<Recipe>> getRecipes() {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RecipeApiUtils apiUtils = retrofit.create(RecipeApiUtils.class);
        Call<ArrayList<Recipe>> call = apiUtils.fetchRecipes();
        return call;

    }

}