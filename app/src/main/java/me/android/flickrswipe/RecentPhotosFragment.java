package me.android.flickrswipe;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import me.android.flickrswipe.model.Photo;
import me.android.flickrswipe.rest.ApiClient;
import me.android.flickrswipe.rest.ApiInterface;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecentPhotosFragment extends Fragment {

    public static final String TAG = RecentPhotosFragment.class.getSimpleName();
    private final static String API_KEY = "930bdc4daade4a690d70d5206b0edf7f";
    private static int LOAD_THRESHOLD = 20; // load more data if you have an inventory of 20 items or less

    private ProgressBar mProgressBar;
    private ImageView mImageView;
    private List<Photo> mPhotoList = new ArrayList<>();
    private Photo mPhotoObject = null;
    private static int mPageCount = 1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.recent_photos, container, false);
        mProgressBar = (ProgressBar) layout.findViewById(R.id.progressBar);
        mImageView = (ImageView) layout.findViewById(R.id.recent_image);
        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new CustomSwipeDetector(mImageView).setOnSwipeListener(new CustomSwipeDetector.onSwipeEvent() {
            @Override
            public void SwipeEventDetected(View v, CustomSwipeDetector.SwipeTypeEnum swipeType) {
                if (swipeType == CustomSwipeDetector.SwipeTypeEnum.RIGHT_TO_LEFT) {
                    updateUIWithNextPhoto();
                    // check if you need to load more
                    loadMoreDataIfNeeded();
                }

            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            mPhotoList.clear(); // init list
            mProgressBar.setVisibility(View.VISIBLE);
            // loading data for the first time on app launch or when process is killed
            mPageCount = 1;
            triggerDataSync(mPageCount);
        } else {
            // activity must have been recreated on orientation change or other reasons
            // only setup the imageView again
            setupImageWithUrl();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // handle configuration changes
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // reset data to avoid holding unwanted memory
        if (mPhotoList != null) {
            mPhotoList.clear();
            mPhotoList = null;
        }
        mPhotoObject = null;
        mPageCount = 1;
    }

    private void updateUIWithNextPhoto() {
        if (mPhotoList.size() == 0) {
            Log.d(TAG, "photoList is empty as of now");
            return;
        }
        mPhotoObject = mPhotoList.get(0);
        //NOTE: for this specific implementation we are not worried about photos in the past
        // so as soon as a photo is consumed, we REMOVE it from the data list to avoid unwanted memory usage!
        mPhotoList.remove(0);
        // ANOTHER RELATED IDEA:
        // there's also another way to implement this feature: let's say we also want to show older items (for example, when swiping from left to right),
        // then we need to keep hold of ALL items. keep a integer counter for currentIndex and increment/decrement according to swipe
        // and mainly we need to make sure to clear the list once it reaches Integer.MAX_VALUE to avoid counter overflow

        setupImageWithUrl(); // form url and set for image
    }

    private void loadMoreDataIfNeeded() {
        // load up next set
        if (mPhotoList.size() < LOAD_THRESHOLD) {
            mPageCount++; // next page
            triggerDataSync(mPageCount);
        }
    }

    private void updateUIAndPhotoList(List<Photo> photoList) {
        if (getActivity() == null || getActivity().isFinishing()) {
            // fragment no longer in valid state, go away
            return;
        }
        updatePhotoList(photoList);
        updateUIWithNextPhoto();
    }

    private void updatePhotoList(List<Photo> photoList) {
        if (photoList != null && photoList.size() > 0) {
            mPhotoList.addAll(photoList);
        }
    }

    private void setupImageWithUrl() {
        mProgressBar.setVisibility(View.GONE);
        String imageUrl = "http://farm" + mPhotoObject.getFarm() + ".static.flickr.com/" + mPhotoObject.getServer() + "/" + mPhotoObject.getId() + "_" + mPhotoObject.getSecret() + ".jpg";
        Picasso.with(getActivity()).load(imageUrl).placeholder(R.drawable.placeholder).fit().into(mImageView);
    }

    private void triggerDataSync(int pageCount) {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiService.getSearchResults("flickr.photos.getRecent", "json", API_KEY, 1, pageCount);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray photosArray = jsonObject.getJSONObject("photos").getJSONArray("photo");
                    Log.d(TAG, "Number of recent photos received: " + photosArray.length());
                    Type collectionType = new TypeToken<List<Photo>>() {
                    }.getType();
                    List<Photo> photoList = new Gson().fromJson(
                            photosArray.toString(),
                            collectionType);
                    if (mPageCount == 1) {
                        updateUIAndPhotoList(photoList); // first time, so update data and UI
                    } else {
                        updatePhotoList(photoList); // update only the data content
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "Oops! Problem fetching images", Toast.LENGTH_LONG).show();
            }

        });
    }

}
