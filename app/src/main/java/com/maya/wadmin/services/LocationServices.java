package com.maya.wadmin.services;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.maps.model.LatLng;
import com.maya.wadmin.constants.Constants;
import com.maya.wadmin.utilities.Logger;
import com.maya.wadmin.utilities.Utility;

/**
 * Created by Gokul Kalagara on 1/27/2018.
 */

public class LocationServices extends Service {
    public static Context context;
    public static SharedPreferences sharedPreferences;
    private static final String TAG = "LocationService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 4000;
    private static final float LOCATION_DISTANCE = 0;

    public class LocationListenerMain implements LocationListener {
        Location mLastLocation;
        Context context;

        public LocationListenerMain(String provider,Context context) {
            mLastLocation = new Location(provider);
            this.context = context;
            sendBroadcastMessage(mLastLocation);
        }

        @Override
        public void onLocationChanged(Location location) {
            mLastLocation.set(location);
            Logger.e(TAG, "onLocationChanged: " + location);
            sendBroadcastMessage(location);
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            Logger.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Logger.e(TAG, "onStatusChanged: " + provider);
        }


    }

    /*
    LocationListenerMain[] mLocationListeners = new LocationListenerMain[]{
            new LocationListenerMain(LocationManager.GPS_PROVIDER),
            new LocationListenerMain(LocationManager.NETWORK_PROVIDER)
    };
    */

    LocationListenerMain[] mLocationListeners;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = this;

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {

        context = this;
        sharedPreferences = getSharedPreferences(Constants.PREFS,Context.MODE_PRIVATE);
        mLocationListeners = new LocationListenerMain[]{
                new LocationListenerMain(LocationManager.PASSIVE_PROVIDER,this)
        };

        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mLocationListeners[0]
            );
        } catch (java.lang.SecurityException ex) {
            Logger.i(TAG, "fail to request location update, ignore");
        } catch (IllegalArgumentException ex) {
            Logger.d(TAG, "network provider does not exist, " + ex.getMessage());
        }

    }

    @Override
    public void onDestroy() {
        Logger.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Logger.i(TAG, "fail to remove location listener, ignore");
                }
            }
        }
    }

    private void initializeLocationManager() {
        Logger.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: "+ LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void sendBroadcastMessage(Location location)
    {
        if (Utility.getBoolen(sharedPreferences,Constants.TRACKING) && location!=null && sharedPreferences!=null  && location.getLongitude()>0)
        {
            Utility.saveTripPoint(new LatLng(location.getLatitude(),location.getLongitude()), sharedPreferences);
        }

        if (location != null && location.getLongitude()>0)
        {
            Intent intent = new Intent("BROADCAST_MESSAGE");
            intent.putExtra("latitude", ""+location.getLatitude());
            intent.putExtra("longitude", ""+location.getLongitude());
            LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(intent);
        }
    }
}