package me.android.flickrswipe.rest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("services/rest/")
    public Call<ResponseBody> getSearchResults(@Query("method") String type, @Query("format") String format, @Query("api_key") String apiKey, @Query("nojsoncallback") int noJson, @Query("page") int page);

}