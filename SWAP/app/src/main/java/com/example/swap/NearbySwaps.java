package com.example.swap;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class NearbySwaps extends AppCompatActivity {
    private static int toggle = 0;
    public static final String[] wanted = new String[]{"Auto repair - help me change the oil in my car",
            "Transportation to drive me to work in the mornings","--", "Help me fix my leaking sink"};
    public static final String[] needed = new String[]{"Any skill on my profile", "Leftover food from my restaurant job",
            "Giving out free haircuts!", "Any skill on my profile"};
    public static final String[] images = new String[]{"https://hips.hearstapps.com/ghk.h-cdn.co/assets/17/30/pembroke-welsh-corgi.jpg?crop=1xw:0.9997114829774957xh;center,top&resize=980:*",
    "https://hips.hearstapps.com/ghk.h-cdn.co/assets/16/08/gettyimages-149263578.jpg?crop=0.4444444444444445xw:1xh;center,top&resize=980:*",
    "https://hips.hearstapps.com/ghk.h-cdn.co/assets/17/30/2560x3839/dachshund.jpg?resize=980:*",
    "https://hips.hearstapps.com/ghk.h-cdn.co/assets/17/30/1500912970-shiba-inu.jpg?crop=1.0xw:1xh;center,top&resize=980:*"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_swaps);

        initializeToggles();
        updatePostFeed(1);
        generateRandomPosts();
        setProfilePicture("https://hips.hearstapps.com/ghk.h-cdn.co/assets/17/30/pembroke-welsh-corgi.jpg?crop=1xw:0.9997114829774957xh;center,top&resize=980:*");

        Button postButton = findViewById(R.id.createPost);
        postButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(NearbySwaps.this, CreateSwap.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        ImageButton goToProfileButton = findViewById(R.id.profileButton);
        //CHANGE THIS AS NEEDED. SHOULD LEAD TO THE USER'S OWN PROFILE PAGE
        goToProfileButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(NearbySwaps.this, ProfilePage.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }

    private void updatePostFeed(int toggle){
        //remove old posts and add new posts to feed
        ProgressBar pBar = findViewById(R.id.postProgressBar);
        LinearLayout postsLayout = findViewById(R.id.PostLinearLayout);
        postsLayout.removeAllViews();
        postsLayout.setVisibility(View.INVISIBLE);
        pBar.setVisibility(View.VISIBLE);

        //simulates making call to the backend

        //JSONObject postsData = retrievePosts();
        //for post in postsData
        //  addPost(post);
        generateRandomPosts();

        //reveal updated posts once retrieved
        postsLayout.setVisibility(View.VISIBLE);
        pBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Sets the user's profile picture to the image at the given url.
     *
     * @param url a String with the url of the user's profile picture
     */
    private void setProfilePicture(String url){
        if (null == url){
            return;
        }
        ImageView profileImage = findViewById(R.id.profileButton);
        Picasso.with(this).load(url).fit().placeholder(R.mipmap.default_profile_alt_dark).error(R.mipmap.default_profile_alt_dark).into(profileImage);
    }


    /**
     * Generates random posts for demonstration purposes
     *
     */
    private void generateRandomPosts(){
        for (int i = 0; i < 12; i ++) {
            JSONObject test = new JSONObject();
            Random rand = new Random();
            try {
                test.put("looking for", NearbySwaps.needed[rand.nextInt(NearbySwaps.needed.length)]);
                test.put("in exchange for", NearbySwaps.wanted[rand.nextInt(NearbySwaps.wanted.length)]);
                test.put("profileImageURL", NearbySwaps.images[rand.nextInt(NearbySwaps.images.length)]);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            addPost(test);
        }
    }


    /**
     * Retrieves posts from the database which meet the toggled
     *
     * @param toggle The mode toggle. 0 -> Free Services. 1 -> Swaps. 2 -> Needs Free Service
     * @return      JSONObject with the posts meeting the requirements
     */
    private JSONObject retrievePosts(int toggle){
        return null;
    }

    /**
     * Retrieves posts from the database which meet the toggled and searchTerm requirements
     *
     * @param toggle The mode toggle. 0 -> Free Services. 1 -> Swaps. 2 -> Needs Free Service
     * @param searchTerm The string the user has searched for. Can switch this to filter etc as needed OR can implement search term in front end only.
     * @return      JSONObject with the posts meeting the requirements
     */
    private JSONObject retrievePosts(int toggle, String searchTerm){
        return null;
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
     * Initializes mode toggles with their OnClickListeners when the activity is created.
     * Modifications should be made here to add calls to backend when switch is toggled
     *
     * @return      nothing
     */
    private void initializeToggles(){
        Button freeServices = findViewById(R.id.freeServicesToggle); //Option 0
        freeServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setToggle(0);
                updatePostFeed(0);
            }
        });
        Button swap = findViewById(R.id.swapToggle); //Option 1
        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setToggle(1);
                updatePostFeed(1);
            }
        });
        Button needsFree = findViewById(R.id.needsFreeToggle); //Option 2
        needsFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setToggle(2);
                updatePostFeed(2);
            }
        });

        setToggle(1);
    }

    /**
     * Set's the toggles colors based on which toggle was pressed
     */
    private void setToggle(int newToggle){
        NearbySwaps.toggle = newToggle;
        Button freeServices = findViewById(R.id.freeServicesToggle); //Option 0
        Button swap = findViewById(R.id.swapToggle); //Option 1
        Button needsFree = findViewById(R.id.needsFreeToggle); //Option 2
        if(NearbySwaps.toggle == 0){
            setToggleSelected(freeServices);
            setToggleDeselected(swap);
            setToggleDeselected(needsFree);
        }else if (NearbySwaps.toggle == 1){
            setToggleDeselected(freeServices);
            setToggleSelected(swap);
            setToggleDeselected(needsFree);
        }else{
            setToggleDeselected(freeServices);
            setToggleDeselected(swap);
            setToggleSelected(needsFree);
        }
    }

    /**
     * Setting Toggle Style Helper Function
     *
     * @return      nothing
     */
    private void setToggleSelected(Button button){
        button.setBackground(getResources().getDrawable(R.drawable.toggledmode));
        button.setTextColor( Color.parseColor("#F4F7F9") );
    }

    /**
     * Setting Toggle Style Helper Function
     *
     * @return      nothing
     */
    private void setToggleDeselected(Button button){
        button.setBackground(getResources().getDrawable(R.drawable.modetoggle));
        button.setTextColor( Color.parseColor("#535353") );
    }


}