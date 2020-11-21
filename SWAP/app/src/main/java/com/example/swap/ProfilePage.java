package com.example.swap;

import android.content.Intent;
import android.os.Bundle;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfilePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        initializePage();
        setProfilePicture("https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/dog-puppy-on-garden-royalty-free-image-1586966191.jpg?crop=1.00xw:0.669xh;0,0.190xh&resize=768:*");

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener(){
                                            @Override
                                            public void onClick(View view){
                //FILL OUT WITH FUNCTION TO LOG USER OUT THEN DIRECT THEM TO THE LOGIN PAGE
            }
        });
    }

    /**
     * Loads the data into the page on activity start
     *
     * @return      nothing
     */
    private void initializePage(){
        ProgressBar pBar = findViewById(R.id.progressBar);
        LinearLayout posts = findViewById(R.id.PostLinearLayout);

        //remove any existing views and load results
        posts.removeAllViews();
        posts.setVisibility(View.INVISIBLE);
        pBar.setVisibility(View.VISIBLE);

        //simulates the results loading from backend
        for (int i = 0; i < 12; i ++) {
            JSONObject test = new JSONObject();
            Random rand = new Random();
            try {
                test.put("looking for", NearbySwaps.needed[rand.nextInt(NearbySwaps.needed.length)]);
                test.put("in exchange for", NearbySwaps.wanted[rand.nextInt(NearbySwaps.wanted.length)]);
                test.put("profileImageURL", "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/dog-puppy-on-garden-royalty-free-image-1586966191.jpg?crop=1.00xw:0.669xh;0,0.190xh&resize=768:*");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            addPost(test);
        }

        //make posts visible once loaded
        posts.setVisibility(View.VISIBLE);
        pBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Takes in post information in JSON format. Creates a PostView and adds it to the activity
     *
     * @param  jsonObject a JSONObject with the post information
     * @return      nothing
     */
    public void addPost(JSONObject jsonObject){
        LinearLayout posts = findViewById(R.id.PostLinearLayout);
        PostCardView newPost = new PostCardView(getApplicationContext(), jsonObject);
        posts.addView(newPost);
    }

    /**
     * Retrieves the current user's profile information from the database and returns it in json form
     * (we might not need this function)
     *
     * @return      JSONObject with profile data
     */
    private JSONObject retrieveProfileData(){
        return null;
    }

    /**
     * Retrieves the current user's previous posts from the database and returns them in json form
     * (we might not need this function)
     *
     * @return      JSONObject with user's posts
     */
    private JSONObject retrieveUserPosts(){
        return null;
    }

    /**
     * Sets the user's profile picture to the image at the given url.
     *
     */
    private void setProfilePicture(String url){
        if (null == url){
            return;
        }
        ImageView profileImage = findViewById(R.id.profilePicture);
        Picasso.with(this).load(url).placeholder(R.mipmap.default_profile_alt_dark).error(R.mipmap.default_profile_alt_dark).into(profileImage);
    }
}