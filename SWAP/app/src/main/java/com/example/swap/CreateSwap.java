package com.example.swap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;

public class CreateSwap extends AppCompatActivity {
    private static final String TAG = "CreateSwap";
    private static int toggle = 0;
    private static final String API_KEY = "AIzaSyCxlreP0Pp8_9LJKztpSxpwne5WMkV2o1w";
    public String address = "";
    Map<String, Object> post = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_swap);
        // initializeToggles();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final EditText need = (EditText)findViewById(R.id.user_need);
        final EditText offer = (EditText)findViewById(R.id.user_offer);
        final EditText details = (EditText)findViewById(R.id.user_details);
        // final EditText location= (EditText)findViewById(R.id.user_location);
        final EditText expiration= (EditText)findViewById(R.id.user_needed_by);
        final EditText contact= (EditText)findViewById(R.id.user_contact);

        Button cancel_post_btn = findViewById(R.id.mark_resolved);
        cancel_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goNearybySwapsIntent();
            }
        });
        Intent intent = getIntent();
        final String user_id  = intent.getStringExtra("UID");

        Button exit_btn = findViewById(R.id.close_button2);
        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goNearybySwapsIntent();
            }
        });

        Button resolvePostButton = findViewById(R.id.profileButton);
        resolvePostButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (getToggle() == 1 || getToggle() == 2) {
                    post.put("need", need.getText().toString());
                } else {
                    post.put("need", "");
                }
                post.put("need_image", "");
                //If Toggle 2 (No offer)
                if (getToggle() == 0 || getToggle() == 1) {
                    post.put("offer", offer.getText().toString());
                } else {
                    post.put("offer", "");
                }
                post.put("details", details.getText().toString());
                post.put("expiration", expiration.getText().toString());
                post.put("contact", contact.getText().toString());
                post.put("user_id", user_id);
                //false means open, true means closed
                post.put("status", false);
                String[] date = Calendar.getInstance().getTime().toString().split(" ");
                post.put("creation_time", date[0] + " " + date[1] + " " + date[2] + ", " + date[5]);

                NearbySwaps nearby = new NearbySwaps();
                Double latitude = nearby.LAT;
                Double longitude = nearby.LON;

                RequestQueue queue = Volley.newRequestQueue(CreateSwap.this);
                String url = String.format(Location.geoCode, latitude, longitude, CreateSwap.API_KEY);

                // Request a string response from the provided URL.
                JsonObjectRequest jsonRequest = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null,
                        new com.android.volley.Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Display the first 500 characters of the response string.
                                //textView.setText("Response is: "+ response.substring(0,500));
                                try {
                                    //gets City, State instead of entire address to protect user privacy.
                                    JSONArray results = (JSONArray) response.get("results");
                                    JSONObject result = (JSONObject) results.get(0);
                                    JSONArray address_components = result.getJSONArray("address_components");
                                    System.out.println(address_components);
                                    String city = "";
                                    String state = "";
                                    for(int i = 0; i < address_components.length(); i++){
                                        JSONObject component = address_components.getJSONObject(i);
                                        JSONArray types = component.getJSONArray("types");
                                        System.out.println(types);
                                        if(types.toString().contains("locality")){
                                            city = component.getString("long_name");
                                        }
                                        if(types.toString().contains("administrative_area_level_1")){
                                            state = component.getString("short_name");
                                        }
                                    }
                                    post.put("location", city + ", " + state);
                                    // Add a new document with a generated ID
                                    db.collection("posts")
                                            .add(post)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                                    goViewSwap(documentReference.getId());
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error adding document", e);
                                                }
                                            });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast=Toast.makeText(getApplicationContext(),"Location Error",Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
                queue.add(jsonRequest);
            }
        });

        // INITIALIZE TABS
        final EditText user_offer_input = findViewById(R.id.user_offer);
        final TextView user_offer_text = findViewById(R.id.text_useroffer);
        final EditText user_need_input = findViewById(R.id.user_need);
        final TextView user_need_text = findViewById(R.id.text_userneed);

        TabLayout tabs = findViewById(R.id.tabLayout2);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabPosition = tab.getPosition();
                if (tabPosition == 0) {
                    //turn off what I need
                    user_need_input.setText("");
                    user_need_input.setVisibility(View.GONE);
                    user_need_text.setVisibility(View.GONE);
                    //make sure what you're offering is visible
                    user_offer_input.setVisibility(View.VISIBLE);
                    user_offer_text.setVisibility(View.VISIBLE);
                } else if (tabPosition == 1) {
                    user_need_input.setVisibility(View.VISIBLE);
                    user_need_text.setVisibility(View.VISIBLE);
                    user_offer_input.setVisibility(View.VISIBLE);
                    user_offer_text.setVisibility(View.VISIBLE);
                } else if (tabPosition == 2) {
                    user_offer_input.setText("");
                    user_offer_input.setVisibility(View.GONE);
                    user_offer_text.setVisibility(View.GONE);
                    //turn on what you need
                    user_need_input.setVisibility(View.VISIBLE);
                    user_need_text.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                user_need_input.setVisibility(View.VISIBLE);
                user_need_text.setVisibility(View.VISIBLE);
                user_offer_input.setVisibility(View.VISIBLE);
                user_offer_text.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int tabPosition = tab.getPosition();
                if (tabPosition == 0) {
                    //turn off what I need
                    user_need_input.setText("");
                    user_need_input.setVisibility(View.GONE);
                    user_need_text.setVisibility(View.GONE);
                    //make sure what you're offering is visible
                    user_offer_input.setVisibility(View.VISIBLE);
                    user_offer_text.setVisibility(View.VISIBLE);
                } else if (tabPosition == 1) {
                    user_need_input.setVisibility(View.VISIBLE);
                    user_need_text.setVisibility(View.VISIBLE);
                    user_offer_input.setVisibility(View.VISIBLE);
                    user_offer_text.setVisibility(View.VISIBLE);
                } else if (tabPosition == 2) {
                    user_offer_input.setText("");
                    user_offer_input.setVisibility(View.GONE);
                    user_offer_text.setVisibility(View.GONE);
                    //turn on what you need
                    user_need_input.setVisibility(View.VISIBLE);
                    user_need_text.setVisibility(View.VISIBLE);
                }
            }
        });
        tabs.getTabAt(1).select();
    }

    private void goNearybySwapsIntent() {
        Intent i = new Intent(CreateSwap.this, NearbySwaps.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private void goViewSwap(String post_id) {
        Intent i = new Intent(CreateSwap.this, ViewSwap.class);
        i.putExtra("POST_ID", post_id);
        startActivity(i);
        finish();
    }

    public int getToggle() {
        TabLayout tabs = findViewById(R.id.tabLayout2);
        return tabs.getSelectedTabPosition();
    }
}