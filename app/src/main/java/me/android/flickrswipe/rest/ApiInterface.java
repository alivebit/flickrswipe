package me.android.flickrswipe.rest;

import me.android.flickrswipe.model.RecentPhotoModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("services/rest/")
    public Call<RecentPhotoModel> getSearchResults(@Query("method") String type, @Query("format") String format, @Query("api_key") String apiKey, @Query("nojsoncallback") int noJson, @Query("page") int page);

}