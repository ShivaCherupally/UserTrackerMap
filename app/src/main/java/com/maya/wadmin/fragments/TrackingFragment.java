package com.maya.wadmin.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.maya.wadmin.R;
import com.maya.wadmin.broadcasts.CommunicationBroadcastReceiver;
import com.maya.wadmin.constants.Constants;
import com.maya.wadmin.interfaces.IFragment;
import com.maya.wadmin.services.LocationServices;
import com.maya.wadmin.utilities.Logger;
import com.maya.wadmin.utilities.Utility;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrackingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrackingFragment extends Fragment implements IFragment , OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    LatLng currentLocation = null, previousPoint = null, startPoint = null;
    Marker currentEmp = null, TripStartPoint = null;

    List<LatLng> allLatLngs;

    public static TrackingFragment mTrackingFragment;
    CoordinatorLayout coordinatorLayout;
    GoogleMap map;
    MapView mapView;
    FloatingActionButton fab;
    boolean isRunning;
    SharedPreferences sharedPreferences;
    CommunicationBroadcastReceiver broadcastReceiver;




    public TrackingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrackingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrackingFragment newInstance(String param1, String param2) {
        TrackingFragment fragment = new TrackingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tracking, container, false);
        mTrackingFragment = this;
        broadcastReceiver = new CommunicationBroadcastReceiver();
        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        mapView= view.findViewById(R.id.mapView);
        fab =  view.findViewById(R.id.fab);
        sharedPreferences = activity().getSharedPreferences(Constants.PREFS,Context.MODE_PRIVATE);


        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        isRunning = Utility.getBoolen(sharedPreferences,Constants.TRACKING);

        if(isRunning)
        {
            fab.setImageResource(R.drawable.ic_stop);

        }
        else
        {
            fab.setImageResource(R.drawable.ic_start);
        }



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(Utility.getBoolen(sharedPreferences, Constants.TRACKING)==false)
                {
                    if (isGpsPersent())
                    {
                        if(currentLocation!=null)
                            startTrip();
                        else
                        {
                            showSnackBar( "Please wait getting location", 2);
                        }
                    }
                    else
                    {

                        showSnackBar( "Please on gps", 2);
                    }
                }
                else
                {
                    stopTrip();
                }

            }
        });

        return view;
    }

    @Override
    public void changeTitle(String title) {

    }

    @Override
    public void showSnackBar(String snackBarText, int type) {
        Utility.showSnackBar(activity(),coordinatorLayout,snackBarText,0);
    }

    @Override
    public Activity activity() {
        return getActivity();
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                broadcastReceiver, new IntentFilter("BROADCAST_MESSAGE"));
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
                broadcastReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        try
        {

            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            activity(), R.raw.style_json));

        }
        catch (Resources.NotFoundException e)
        {

        }
        // Position the map's camera near Sydney, Australia.
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-17, 79)));
        activity().startService(new Intent(activity(), LocationServices.class));
        if (ContextCompat.checkSelfPermission(activity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            //map.setMyLocationEnabled(true);
        }


        if (isRunning)
        {
            fab.setImageResource(R.drawable.ic_stop);
            addActionBasedOnItem();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if(!isGpsPersent())
            checkGps();
    }

    public boolean isGpsPersent()
    {
        LocationManager manager = (LocationManager) activity().getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void checkGps()
    {

        android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(activity());
        alert.setTitle("GPS Permission");
        alert.setMessage("You need GPS for tracking. Please switch on the GPS");

        alert.setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isGpsPersent())
                {

                }
                else
                {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    activity().startActivityForResult(myIntent, 0);

                }
            }
        });

        alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                activity().finish();
            }
        });

        Dialog dialog = alert.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void addCurrent(final LatLng location)
    {
        if(location == null)
        {
            return;
        }
        currentLocation = location;



        if(previousPoint == null)
        {
            previousPoint = location;
        }
        if (currentEmp == null)
        {
            MarkerOptions marker2 = new MarkerOptions();
            marker2.title("Current");
            marker2.icon(getBitmapDescriptor(R.drawable.ic_navigation));
            marker2.anchor(.5f, .5f);
            marker2.position(location);
            currentEmp = map.addMarker(marker2);
        }
        else
        {
            currentEmp.setRotation((float) bearingBetweenLocations(previousPoint, location));
            currentEmp.setPosition(location);
            previousPoint = location;
        }

        if(location!=null)
        {
            addActionBasedOnItem();
            zoomToPostion(location);
        }
    }




    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2)
    {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;
        double dLon = (long2 - long1);
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }

    public void zoomToPostion(final LatLng location)
    {

        activity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CameraPosition.Builder builder = CameraPosition.builder();
                builder.target(location);
                builder.zoom(16);
                CameraPosition cameraPosition = builder.build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                map.animateCamera(cameraUpdate);
            }
        });

    }


    private BitmapDescriptor getBitmapDescriptor(int id)
    {
        Drawable vectorDrawable = ContextCompat.getDrawable(activity(),id);
        vectorDrawable.setBounds(0, 0, 80, 100);
        Bitmap bm = Bitmap.createBitmap(80, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bm);
    }


    public void startTrip()
    {
        if(isRunning == false)
        {
            Utility.setBoolen(sharedPreferences, Constants.TRACKING, true);
            isRunning = Utility.getBoolen(sharedPreferences, Constants.TRACKING);
            startPoint = currentLocation;
            Utility.saveTripPoint(startPoint, sharedPreferences);
            fab.setImageResource(R.drawable.ic_stop);
            addActionBasedOnItem();
        }



    }

    public void stopTrip()
    {
        fab.setImageResource(R.drawable.ic_start);
        if (isRunning == true)
        {
            Utility.setBoolen(sharedPreferences, Constants.TRACKING, false);
            isRunning = Utility.getBoolen(sharedPreferences, Constants.TRACKING);
            Utility.clearTrip(sharedPreferences);
            map.clear();
            currentEmp = null;
            addCurrent(currentLocation);
        }
    }

    public void addActionBasedOnItem()
    {
        try
        {
            if (isRunning == true)
            {



               activity().runOnUiThread(new Runnable() {
                   @Override
                   public void run() {

                       allLatLngs = Utility.generateLatLng(sharedPreferences);
                       PolylineOptions lineOptions = new PolylineOptions();
                       lineOptions.addAll(allLatLngs);
                       lineOptions.width(12);
                       lineOptions.color(Color.parseColor("#33aaFF"));
                       if (allLatLngs != null && allLatLngs.size() > 0)
                       {
                           map.addPolyline(lineOptions);
                       }

                   }
               });


                if (startPoint == null && allLatLngs != null && allLatLngs.size() > 0)
                {
                    startPoint = allLatLngs.get(0);
                }
                if (TripStartPoint == null && startPoint!= null)
                {
                    MarkerOptions marker2 = new MarkerOptions();

                    float logicalDensity = getResources().getDisplayMetrics().density;
                    int thicknessPoints = (int)Math.ceil(20 * logicalDensity + .5f);

                    BitmapDrawable b = (BitmapDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.green_circle1)  ;
                    Bitmap finalIcon = Bitmap.createScaledBitmap(b.getBitmap(), thicknessPoints, thicknessPoints, false);


                    marker2.icon(BitmapDescriptorFactory.fromBitmap(finalIcon));
                    marker2.anchor(.5f, .5f);
                    marker2.position(startPoint);
                    TripStartPoint = map.addMarker(marker2);
                }

            }
        }
        catch(Exception e)
        {

        }

    }



}
