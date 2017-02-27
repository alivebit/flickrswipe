package me.android.flickrswipe.rest;

import retrofit2.Retrofit;


public class ApiClient {

    public static final String BASE_URL = "https://api.flickr.com/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .build();
        }
        return retrofit;
    }


}