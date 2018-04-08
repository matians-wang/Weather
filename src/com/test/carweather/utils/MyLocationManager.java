package com.test.carweather.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

/**
 * A class that handles everything about location.
 */
public class MyLocationManager {
    private static final String TAG = MyLocationManager.class.getSimpleName();

    private Context mContext;
    private Listener mListener;
    private android.location.LocationManager mLocationManager;
    private boolean mRecordLocation;

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(android.location.LocationManager.GPS_PROVIDER),
            new LocationListener(android.location.LocationManager.NETWORK_PROVIDER) };

    public interface Listener {
        void onLocationReceived(Location location);
        void onLocationError();
    }

    public MyLocationManager(Context context) {
        mContext = context;
    }

    public MyLocationManager(Context context, Listener listener) {
        mContext = context;
        mListener = listener;
    }

    public Location getCurrentLocation() {
        if (!mRecordLocation)
            return null;

        // go in best to worst order
        for (int i = 0; i < mLocationListeners.length; i++) {
            Location l = mLocationListeners[i].current();
            if (l != null)
                return l;
        }
        Log.d(TAG, "No location received yet.");
        return null;
    }

    public void recordLocation(boolean recordLocation) {
        if (mRecordLocation != recordLocation) {
            mRecordLocation = recordLocation;
            if (recordLocation) {
                startReceivingLocationUpdates();
            } else {
                stopReceivingLocationUpdates();
            }
        }
    }

    private void startReceivingLocationUpdates() {
        if (mLocationManager == null) {
            mLocationManager = (android.location.LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        }
        if (mLocationManager != null) {
            try {
                mLocationManager.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER, 1000, 0F,
                        mLocationListeners[1]);
            } catch (SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "provider does not exist " + ex.getMessage());
            }
            try {
                mLocationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, 1000, 0F,
                        mLocationListeners[0]);
            } catch (SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "provider does not exist " + ex.getMessage());
            }
            Log.d(TAG, "startReceivingLocationUpdates");
        }
    }

    private void stopReceivingLocationUpdates() {
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
            Log.d(TAG, "stopReceivingLocationUpdates");
        }
    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;
        boolean mValid = false;
        String mProvider;

        public LocationListener(String provider) {
            mProvider = provider;
            mLastLocation = new Location(mProvider);
        }

        @Override
        public void onLocationChanged(Location newLocation) {
            Log.d(TAG, "onLocationChanged");
            if (newLocation.getLatitude() == 0.0 && newLocation.getLongitude() == 0.0) {
                // Hack to filter out 0.0,0.0 locations
                return;
            }

            if (mListener != null && mRecordLocation) {
                mListener.onLocationReceived(newLocation);
            }

            if (!mValid) {
                Log.d(TAG, "Got first location.");
            }
            mLastLocation.set(newLocation);
            mValid = true;
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
            mValid = false;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
            case LocationProvider.TEMPORARILY_UNAVAILABLE: {
                mValid = false;
                break;
            }
            }
        }

        public Location current() {
            return mValid ? mLastLocation : null;
        }
    }
}
