package com.example.swap;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class Location {
    public String LAT;
    public String LON;
    public String address;
    public static final String geoCode = "https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s";
    private static final String API_KEY = "";

    public Location(String LAT, String LON, Context context){

    }

    private void latLonToAddress(Context context){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = String.format(Location.geoCode, this.LAT, this.LON, Location.API_KEY);
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
