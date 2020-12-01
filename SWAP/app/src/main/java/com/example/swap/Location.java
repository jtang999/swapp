package com.example.swap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
//import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class Location {
    public static double LAT;
    public static double LON;
    public String address;
    public static final String geoCode = "https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s";
    //This is Josh's API Key. I already pushed it to my project02 repo lol,
    // so it's fine to just use it here since it'll be public after this semester anyways
    private static final String API_KEY = "AIzaSyCxlreP0Pp8_9LJKztpSxpwne5WMkV2o1w";

    public Location(String LAT, String LON, Context context){

    }

    public static double euclidianDistance(double LAT1, double LON1, double LAT2, double LON2){
        return Math.sqrt(Math.pow( (LAT1 - LAT2), 2) + Math.pow( (LON1 - LON2), 2));
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation(final Activity activity, final LocationCallback locationCallback){

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(activity).requestLocationUpdates(locationRequest, new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                super.onLocationResult(locationResult);
                //need a locationCallback (this)
                //need an activity MainActivity
                LocationServices.getFusedLocationProviderClient(activity).removeLocationUpdates(locationCallback);
                if(locationResult != null && locationResult.getLocations().size() > 0){
                    int latestLocationIndex = locationResult.getLocations().size() - 1;
                    //JUST GOING TO STORE THE UPDATED LATITUDE AND LONGITUDE AS LOCATION ATTRIBUTE OF LOCATION CLASS
                    Location.LAT = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                    Location.LON = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                }

//                latLonToAddress();
            }
        }, Looper.getMainLooper());
    }

    private void latLonToAddress(Context context, double LAT, double LON){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = String.format(Location.geoCode, LAT, LON, Location.API_KEY);
        System.out.println(url);
        // Request a string response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the first 500 characters of the response string.
                        //textView.setText("Response is: "+ response.substring(0,500));
                        try {
                            System.out.println(response);
                            JSONArray results = (JSONArray) response.get("results");
                            //WE SHOULD PROBABLY USE THE ZIP CODE OR SOMETHING LESS SPECIFIC HERE
                            //WE DON'T WANT TO SHARE USER'S ACTUAL CURRENT LOCATION WITH OTHERS
                            JSONObject formatted_address = (JSONObject) results.get(0);

                            //HOW SHOULD WE ASSIGN THE ADDRESS FOR THE POST
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //WHAT SHOULD WE DO IF IT FAILS??
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
    }
}
