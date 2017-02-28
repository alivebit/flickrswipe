package me.android.flickrswipe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStateReceiver extends BroadcastReceiver {

    private NetworkChangeEventListener mNetworkChangeListener;

    public NetworkStateReceiver(NetworkChangeEventListener listener) {
        mNetworkChangeListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        mNetworkChangeListener.onReceive(netInfo != null
                && netInfo.isConnected());
    }

    public interface NetworkChangeEventListener {
        void onReceive(boolean isConnected);
    }
}
