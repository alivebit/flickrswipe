package me.android.flickrswipe;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private RecentPhotosFragment mRecentPhotosFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        // get the retained fragment if activity restarts
        mRecentPhotosFragment = (RecentPhotosFragment) fragmentManager.findFragmentByTag(RecentPhotosFragment.TAG);
        if (mRecentPhotosFragment == null) {
            mRecentPhotosFragment = new RecentPhotosFragment();
        }

        fragmentManager.beginTransaction()
                .replace(R.id.root_fragment_container, mRecentPhotosFragment, RecentPhotosFragment.TAG).commit();
    }


}
