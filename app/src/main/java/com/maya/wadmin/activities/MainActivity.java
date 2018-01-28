package com.maya.wadmin.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;
import com.maya.wadmin.R;
import com.maya.wadmin.constants.Constants;
import com.maya.wadmin.fragments.LoginFragment;
import com.maya.wadmin.fragments.TrackingFragment;
import com.maya.wadmin.interfaces.IActivity;
import com.maya.wadmin.utilities.Utility;

public class MainActivity extends AppCompatActivity implements IActivity{

    CoordinatorLayout coordinatorLayout;
    Toolbar toolbar;
    public SharedPreferences sharedPreferences;
    MenuItem logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(activity(),R.color.white));
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);


        if(!Utility.getBoolen(sharedPreferences,Constants.LOGIN))
        {
            setTitle("Sign In");
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, LoginFragment.newInstance(null,null)).commit();
        }
        else
        {
            setTitle("Tracking");
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,TrackingFragment.newInstance(null,null)).commit();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 7777);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(logout!=null)
                {
                    if (Utility.getBoolen(sharedPreferences, Constants.LOGIN)) {
                        logout.setVisible(true);
                        setTitle("Tracking");
                    } else {
                        logout.setVisible(false);
                        setTitle("Sign In");
                    }
                }
            }
        },500);

    }




    @Override
    public void changeTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void showSnackBar(String snackBarText, int type) {
        Utility.showSnackBar(this,coordinatorLayout,snackBarText,type);
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        if(logout!=null)
        {
            if (Utility.getBoolen(sharedPreferences, Constants.LOGIN)) {
                logout.setVisible(true);
                setTitle("Tracking");
            } else {
                logout.setVisible(false);
                setTitle("Sign In");
            }
        }
    }


    @Override
    public Activity activity() {
        return this;
    }

    public void changeView()
    {
        if(Utility.getBoolen(sharedPreferences,Constants.LOGIN))
        {
            logout.setVisible(true);
            setTitle("Tracking");
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, TrackingFragment.newInstance(null,null)).commit();
        }
        else
        {
            logout.setVisible(false);
            setTitle("Sign In");
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, LoginFragment.newInstance(null,null)).commit();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        logout = menu.findItem(R.id.action_logout);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.action_logout)
        {
            if(!Utility.getBoolen(sharedPreferences,Constants.TRACKING))
            {
                logout();
            }
            else
            {
                showSnackBar("Trip is recording",2);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }


    public void logout()
    {

        android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(activity());
        alert.setMessage("Do you want to logout from Tracking?");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                Utility.del(sharedPreferences,Constants.LOGIN);
                Utility.del(sharedPreferences,Constants.TRACKING);
                Utility.del(sharedPreferences,Constants.TRIP_DATA);
                changeView();
            }
        });

        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog dialog = alert.create();
        //TextView textView = dialog.FindViewById<TextView>(Android.Resource.Id.Message);
        //textView.Typeface = Typeface.CreateFromAsset(Assets, "Regular.ttf");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == 7777 && !(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) == false){
                    Toast.makeText(this, "Open permissions and give all the permissions in order to access the app", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", "com.maya.wadmin", null);
                    intent.setData(uri);
                    startActivity(intent);
                }
                else
                {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 7777);
                        return;
                    }
                }
            }
        }
    }
}
