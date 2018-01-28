package com.maya.wadmin.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;
import com.maya.wadmin.fragments.TrackingFragment;
import com.maya.wadmin.utilities.Logger;

/**
 * Created by Gokul Kalagara on 1/27/2018.
 */

public class CommunicationBroadcastReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (TrackingFragment.mTrackingFragment != null && TrackingFragment.mTrackingFragment.getActivity() != null)
        {
            if (intent != null && intent.getStringExtra("latitude") != null)
            {

                TrackingFragment.mTrackingFragment.addCurrent(new LatLng(Double.parseDouble(intent.getStringExtra("latitude")), Double.parseDouble(intent.getStringExtra("longitude"))));
            }
        }
    }
}
