package com.maya.wadmin.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.maya.wadmin.R;
import com.maya.wadmin.constants.Constants;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gokul Kalagara on 1/26/2018.
 */

public class Utility
{
    public static boolean isNetworkAvailable(Activity activity)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected())
        {
            /* show a toast, we cannot use the internet right now */
            //showToast(activity, "network_unavailable_message", Constants.NO_INTERNET_CONNECTION, true);
            /*if(HomeScreenActivity.mHomeScreenActivity != null){
                HomeScreenActivity.mHomeScreenActivity.tvInternetStatus.setText(Constants.NO_INTERNET_CONNECTION);
            }*/
            return false;
            /* aka, do nothing */
        }
        return true;
    }


    public static void setString(SharedPreferences sharedPreferences, String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void setBoolen(SharedPreferences sharedPreferences, String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void setInt(SharedPreferences sharedPreferences,String key, int value)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static String getString(SharedPreferences sharedPreferences,String key)
    {
        return sharedPreferences.getString(key,null);
    }

    public static boolean getBoolen(SharedPreferences sharedPreferences,String key)
    {
        return sharedPreferences.getBoolean(key,false);
    }

    public static int getInt(SharedPreferences sharedPreferences,String key)
    {
        return sharedPreferences.getInt(key,0);
    }

    public static void del(SharedPreferences sharedPreferences, String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }



    public static ProgressDialog generateProgressDialog(Activity activity)
    {
        try {

            ProgressDialog progressDialog = new ProgressDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.show();
            progressDialog.setContentView(R.layout.progressdialog);
            return progressDialog;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static void closeProgressDialog(ProgressDialog progressDialog)
    {
        try
        {
            if(progressDialog!=null&&progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static void showToast(Context c, String s, boolean duration) {
        if (c == null) return;
        Toast tst = Toast.makeText(c, s, duration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        tst.show();
    }
    public static void showToast(Context c, String t, String s, boolean duration) {
        if (c == null) return;
        Toast tst = Toast.makeText(c, t, duration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        tst.setText(s);
        tst.show();
    }

    public static void showToast(Context c, String t, String s, boolean duration, String response) {
        if (c == null) return;
        Toast tst = Toast.makeText(c, t, duration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("error")) {
                tst.setText(jsonObject.getString("error").toString());
            }
            else if(jsonObject.has("message")){
                tst.setText(jsonObject.getString("message").toString());
            }
            else {
                tst.setText(s);
            }
            tst.show();
        } catch (Exception e) {
            Logger.d("[Exception]", e.toString());
        }



    }

    public static void clearTrip(SharedPreferences sharedPreferences)
    {
        setString(sharedPreferences, Constants.TRIP_DATA, "");
        setBoolen(sharedPreferences, Constants.TRACKING, false);
    }

    public static void createtrip(SharedPreferences sharedPreferences)
    {
        setString(sharedPreferences, Constants.TRIP_DATA, "");
        setBoolen(sharedPreferences, Constants.TRACKING, true);
    }



















    public static void showSnackBar(Activity activity, CoordinatorLayout coordinatorLayout, String text, int type)
    {
        if(coordinatorLayout==null|| text==null)
        {
            return;
        }
        Snackbar snackBar = Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_SHORT);
        TextView txtMessage = (TextView) snackBar.getView().findViewById(R.id.snackbar_text);
        txtMessage.setTextColor(ContextCompat.getColor(activity,R.color.white));
        if (type==2)
            snackBar.getView().setBackgroundColor(ContextCompat.getColor(activity,R.color.black));
        else if(type==1)
            snackBar.getView().setBackgroundColor(ContextCompat.getColor(activity,R.color.app_snack_bar_true));
        else
        {
            snackBar.getView().setBackgroundColor(ContextCompat.getColor(activity,R.color.mainColorPrimary));
        }
        snackBar.show();
    }

    public static boolean saveTripPoint(LatLng tripPoint,SharedPreferences sharedPreferences)
    {
        Gson gson = new Gson();
        Type type = new TypeToken<List<LatLng>>() {}.getType();
        if (tripPoint != null)
        {
            String tripData = Utility.getString(sharedPreferences, Constants.TRIP_DATA);
            if(tripData!=null && tripData.length()>0)
            {
                List<LatLng> list = gson.fromJson(tripData,type);
                list.add(tripPoint);
                Utility.setString(sharedPreferences, Constants.TRIP_DATA, gson.toJson(list,type));
            }
            else
            {
                List<LatLng> list = new ArrayList<LatLng>();
                list.add(tripPoint);
                Utility.setString(sharedPreferences, Constants.TRIP_DATA, gson.toJson(list,type));
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public static List<LatLng> generateLatLng(SharedPreferences sharedPreferences)
    {
        Gson gson = new Gson();
        Type type = new TypeToken<List<LatLng>>() {}.getType();
        return gson.fromJson(getString(sharedPreferences,Constants.TRIP_DATA),type);
    }





}
