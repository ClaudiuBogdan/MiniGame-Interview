package com.example.bogdan.sortspeed.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetConnectionStatus {
    private final Context mContext;

    public InternetConnectionStatus(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Method that check the internet connection.
     */
    public boolean isConnectedToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager)mContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }
}
