package com.example.swap;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.DrawableWrapper;
import android.os.Bundle;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfilePage extends AppCompatActivity {
    private static int numPastPosts;
    private static String user_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        initializePage();

        Intent intent = getIntent();
        String user_id  = intent.getStringExtra("UID");
        if (user_id != null && !user_id.equals("")){
            retrieveUserInfo(user_id);
            retrieveUserPosts(user_id, 0);
            intializeToggles(user_id);
        }else {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                user_id = currentUser.getEmail();
                retrieveUserInfo(user_id);
                retrieveUserPosts(user_id, 0);
                intializeToggles(currentUser.getEmail());
            }
        }



        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener(){
                                            @Override
                                            public void onClick(View view){
                // FILL OUT WITH FUNCTION TO LOG USER OUT THEN DIRECT THEM TO THE LOGIN PAGE/BlankPage
                AuthUI.getInstance()
                        .signOut(getApplicationContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                            }
                        });
                Intent blankPage = new Intent(ProfilePage.this, BlankPage.class);
                startActivity(blankPage);
                finish(); // close this activity because we just logged out
            }
        });

        Button profileCloseButton = findViewById(R.id.profile_close_button);
        profileCloseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // FILL OUT WITH FUNCTION TO LOG USER OUT THEN DIRECT THEM TO THE LOGIN PAGE/BlankPage
//                Intent i = new Intent(ProfilePage.this, NearbySwaps.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(i);
                 finish(); // does the same thing as above but more succinctly. - Jericho
                 // close this activity because we just logged out
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
                test.put("need", NearbySwaps.needed[rand.nextInt(NearbySwaps.needed.length)]);
                test.put("offer", NearbySwaps.wanted[rand.nextInt(NearbySwaps.wanted.length)]);
                test.put("need_image", "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/dog-puppy-on-garden-royalty-free-image-1586966191.jpg?crop=1.00xw:0.669xh;0,0.190xh&resize=768:*");

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
     * Initializes the toggles on the profile page for switching between current posts and user history
     * Also sets the user stats to be visible
     */
    private void intializeToggles(final String uid){
        Button currentPosts = findViewById(R.id.activePosts);
        Button history = findViewById(R.id.history);

        currentPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setToggle(0);
                retrieveUserPosts(uid, 0);
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setToggle(1);
                retrieveUserPosts(uid, 1);
            }
        });

        TextView stat = findViewById(R.id.userStat);

        if (this.numPastPosts < 10){
            stat.setText("This user is new to Swap. They have completed " + ProfilePage.numPastPosts + " swaps!");
        }else if(this.numPastPosts < 30){
            stat.setText("This user is a Swap regular. They have completed " + ProfilePage.numPastPosts + " swaps!");
        }else{
            stat.setText("This user is super Swap user. They have completed " + ProfilePage.numPastPosts + " swaps!");
        }
    }

    private void setToggle(int toggle){
        Button currentPosts = findViewById(R.id.activePosts);
        Button history = findViewById(R.id.history);
        TextView stat = findViewById(R.id.userStat);
        if (toggle == 0){
            currentPosts.setBackground(getResources().getDrawable(R.drawable.toggledmode));
            currentPosts.setTextColor(getResources().getColor(R.color.white));
            history.setBackground(getResources().getDrawable(R.drawable.modetoggle));
            history.setTextColor(getResources().getColor(R.color.colorBodyLight));
            // stat.setVisibility(View.GONE);
        }else{
            currentPosts.setBackground(getResources().getDrawable(R.drawable.modetoggle));
            currentPosts.setTextColor(getResources().getColor(R.color.colorBodyLight));
            history.setBackground(getResources().getDrawable(R.drawable.toggledmode));
            history.setTextColor(getResources().getColor(R.color.white));
            // stat.setVisibility(View.VISIBLE);
//            if (this.numPastPosts < 10){
//                stat.setText("This user is new to Swap. They have completed " + ProfilePage.numPastPosts + " swaps!");
//            }else if(this.numPastPosts < 30){
//                stat.setText("This user is a Swap regular. They have completed " + ProfilePage.numPastPosts + " swaps!");
//            }else{
//                stat.setText("This user is super Swap user. They have completed " + ProfilePage.numPastPosts + " swaps!");
//            }
        }
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
        if (newPost.setOnClickListener(ProfilePage.this)) {
            posts.addView(newPost);
        }
    }

    private boolean isUser(String UID, JSONObject jsonObject){
        String userID;

        try {
            userID = jsonObject.getString("user_id");
        } catch (JSONException e) {
            userID = "";
        }
        return UID.equals(userID);
    }

    /**
     * Retrieves the current user's previous posts from the database and returns them in json form
     * (we might not need this function)
     *
     * @return      JSONObject with user's posts
     */
    private JSONObject retrieveUserPosts(final String uid, final int toggle){
        //remove old posts and add set the page to show loading bar
        final ProgressBar pBar = findViewById(R.id.progressBar);
        final LinearLayout postsLayout = findViewById(R.id.PostLinearLayout);

        postsLayout.removeAllViews();
        postsLayout.setVisibility(View.INVISIBLE);
        pBar.setVisibility(View.VISIBLE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                    JSONObject currentPost = new JSONObject(document.getData());
                                    //only add it if we are able to get a post _ID

                                    try {
                                        currentPost.put("post_ID", document.getId());
                                        String status = currentPost.getString("status");
                                        String check;
                                        String checkBackup;
                                        if (toggle == 0){
                                            check = "false";
                                            checkBackup = "open";
                                        }else{
                                            check = "true";
                                            checkBackup = "closed";
                                        }

                                        if (!document.getId().equals("") && document.getId() != null && isUser(uid, currentPost)) {
                                            if (status.equals("true") || status.equals("closed")){
                                                i++;
                                            }
                                            if ((status.equals(check) || status.equals(checkBackup))) {
                                                addPost(currentPost);
                                            }
                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                            }

                            TextView stat = findViewById(R.id.userStat);
                            if ( i < 3){
                                stat.setText("This user is new to Swap. They have completed " + i + " swaps!");
                            }else if(i < 5){
                                stat.setText("This user is a Swap regular. They have completed " + i + " swaps!");
                            }else{
                                stat.setText("This user is super Swap user. They have completed " + i + " swaps!");
                            }
                        } else {
                            System.out.println("Error getting documents" + task.getException());
                        }

                        //reveal updated posts once retrieved
                        postsLayout.setVisibility(View.VISIBLE);
                        pBar.setVisibility(View.INVISIBLE);
                    }
                });

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

    /**
     * Set's the current users profile picture and
     * @param uid the userID for the post
     *
     * @return      nothing
     */
    private void retrieveUserInfo(String uid) {
        //TODO: get user avatar and display
        final FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference cref = database.collection("users");
        DocumentReference dref = cref.document(uid);
        dref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        HashMap<String, Object> user_data;
                        user_data = (HashMap<String, Object>) document.getData();

                        String userName = (String) user_data.get("user_name");
                        TextView userNameView = findViewById(R.id.profile_username);
                        userNameView.setText(userName);
                        setProfilePicture((String) user_data.get("avatar"));
//                        display_user_info(user_data);
                    } else {
                        Toast.makeText(ProfilePage.this,
                                "Error: INVALID USER", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //TODO: Fail with task: DocumentSnapshot
                    Toast.makeText(ProfilePage.this,
                            "Error: COULD NOT FETCH USER", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}