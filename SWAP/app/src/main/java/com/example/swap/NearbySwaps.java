package com.example.swap;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Random;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class NearbySwaps extends AppCompatActivity implements  OnMapReadyCallback {
    private static int mapToggle = 0;
    private static GoogleMap map;
    private String query = ""; // the query string
    private static int toggle = 0;
    public static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    public static double LAT = 37.871;
    public static double LON = -122.273;
    public static final String[] wanted = new String[]{"Auto repair - help me change the oil in my car",
            "Transportation to drive me to work in the mornings","--", "Help me fix my leaking sink"};
    public static final String[] needed = new String[]{"Any skill on my profile", "Leftover food from my restaurant job",
            "Giving out free haircuts!", "Any skill on my profile"};
    public static final String[] images = new String[]{"https://hips.hearstapps.com/ghk.h-cdn.co/assets/17/30/pembroke-welsh-corgi.jpg?crop=1xw:0.9997114829774957xh;center,top&resize=980:*",
    "https://hips.hearstapps.com/ghk.h-cdn.co/assets/16/08/gettyimages-149263578.jpg?crop=0.4444444444444445xw:1xh;center,top&resize=980:*",
    "https://hips.hearstapps.com/ghk.h-cdn.co/assets/17/30/2560x3839/dachshund.jpg?resize=980:*",
    "https://hips.hearstapps.com/ghk.h-cdn.co/assets/17/30/1500912970-shiba-inu.jpg?crop=1.0xw:1xh;center,top&resize=980:*"};

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        NearbySwaps.map = googleMap;
        LatLng berk = new LatLng(37.871666, -122.2730);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(berk));

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(37.871666, -122.2730))
                .title("Berkeley"));

        googleMap.animateCamera( CameraUpdateFactory.zoomTo( 16.0f ) );
        createMapPostCard();
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),16));
                PostCardView mapPost = findViewById(R.id.mapPost);
                mapPost.setVisibility(View.VISIBLE);
                mapPost.enterInfo((JSONObject) marker.getTag(), NearbySwaps.this);
                return true;
            }
        });

        retrievePosts(1);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_swaps);
        findViewById(R.id.postProgressBar).setVisibility(View.VISIBLE);
        initializeToggles();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            retrieveUserInfo(currentUser.getEmail());
        }

        //get location and wifi permissions when page loads if necessary
        if (ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(getApplicationContext(), INTERNET) != PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NearbySwaps.this, new String[]{ACCESS_FINE_LOCATION, INTERNET, ACCESS_NETWORK_STATE}, REQUEST_CODE_LOCATION_PERMISSION);
        }
//        retrievePosts(1);
        getCurrentLocation(); //this calls retrieve Posts again when it finishes, but it takes a second

        Button postButton = findViewById(R.id.createPost);
        postButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(NearbySwaps.this, CreateSwap.class);
                i.putExtra( "UID", currentUser.getEmail());
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        final ImageButton mapToggle = findViewById(R.id.mapToggle);
        mapToggle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                toggleMap();
            }
        });

        ImageButton goToProfileButton = findViewById(R.id.profileButton);
        //CHANGE THIS AS NEEDED. SHOULD LEAD TO THE USER'S OWN PROFILE PAGE
        goToProfileButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(NearbySwaps.this, ProfilePage.class);
                i.putExtra( "UID", currentUser.getEmail());
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        SearchView search = findViewById(R.id.searchView);
        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                query = "";
                retrievePosts(toggle);
                return false;
            }
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                query = s;
                retrievePosts(toggle);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void toggleMap(){
        //0 means map is toggled OFF
        //1 means map is toggled ON
        ScrollView posts = findViewById(R.id.Posts);
        PostCardView mapPost = findViewById(R.id.mapPost);
        LinearLayout mapView = findViewById(R.id.mapView);
        ImageButton mapToggle = findViewById(R.id.mapToggle);

        if (NearbySwaps.mapToggle == 0){
            NearbySwaps.mapToggle = 1;
            posts.setVisibility(View.GONE);
            mapView.setVisibility(View.VISIBLE);
            mapToggle.setImageResource(R.mipmap.map_icon_dark);
        }else{
            NearbySwaps.mapToggle = 0;
            posts.setVisibility(View.VISIBLE);
            mapPost.setVisibility(View.GONE);
            mapView.setVisibility(View.GONE);
            mapToggle.setImageResource(R.mipmap.map_icon);
        }
    }

    private void createMapPostCard(){
        PostCardView post = new PostCardView(NearbySwaps.this);
        post.setId(R.id.mapPost);
        ConstraintLayout layout = findViewById(R.id.coordinatorLayout);
        layout.addView(post);


        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) post.getLayoutParams();
        layoutParams.leftToLeft = R.id.coordinatorLayout;
        layoutParams.rightToRight = R.id.coordinatorLayout;
        layoutParams.bottomToBottom = R.id.coordinatorLayout;
        layoutParams.width = ActionBar.LayoutParams.MATCH_PARENT;
        post.setLayoutParams(layoutParams);

        post.setVisibility(View.INVISIBLE);

    }

    /**
     * Gets the phone's current location. Sets the class attributes accordingly
     *
     */
    @SuppressLint("MissingPermission")
    public void getCurrentLocation(){

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(NearbySwaps.this).requestLocationUpdates(locationRequest, new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                super.onLocationResult(locationResult);
                //need a locationCallback (this)
                //need an activity MainActivity
                LocationServices.getFusedLocationProviderClient(NearbySwaps.this).removeLocationUpdates(this);
                if(locationResult != null && locationResult.getLocations().size() > 0){
                    int latestLocationIndex = locationResult.getLocations().size() - 1;
                    //JUST GOING TO STORE THE UPDATED LATITUDE AND LONGITUDE AS ATTRIBUTE OF NEARBYSWAPS CLASS
                    NearbySwaps.LAT = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                    NearbySwaps.LON = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                }
                //this is only called on page initiation so it's fine to hardcode toggle as 1
                retrievePosts(1);
            }
        }, Looper.getMainLooper());
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
        Picasso.with(this).load(url).transform(new CircleTransform()).fit().placeholder(R.mipmap.default_profile_alt_dark).error(R.mipmap.default_profile_alt_dark).into(profileImage);
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
                test.put("need", NearbySwaps.needed[rand.nextInt(NearbySwaps.needed.length)]);
                test.put("offer", NearbySwaps.wanted[rand.nextInt(NearbySwaps.wanted.length)]);
                test.put("image_need", NearbySwaps.images[rand.nextInt(NearbySwaps.images.length)]);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            addPost(test);
        }
    }

    /**
     * Retrieves posts from the database which meet the toggled and searchTerm requirements
     *
     * @param toggle The mode toggle. 0 -> Free Services. 1 -> Swaps. 2 -> Needs Free Service
     *
     * @return      JSONObject with the posts meeting the requirements
     */
    private void retrievePosts(int toggle){
        //remove old posts and add set the page to show loading bar
        final ProgressBar pBar = findViewById(R.id.postProgressBar);
        final LinearLayout postsLayout = findViewById(R.id.PostLinearLayout);
        removeMarkers();
        NearbySwaps.toggle = toggle;

        postsLayout.removeAllViews();
        postsLayout.setVisibility(View.INVISIBLE);
        pBar.setVisibility(View.VISIBLE);

        //make call to the backend
        //and set the on response listener
        //search.getQuery().toString()

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            LinkedList<JSONObject> posts = new LinkedList<JSONObject>(); //new JSONObject[task.getResult().size()];
                            int i = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                JSONObject currentPost = new JSONObject(document.getData());

                                //only add it if we are able to get a post _ID
                                try {
                                    currentPost.put("post_ID", document.getId());
                                    String status = currentPost.getString("status");
                                    //System.out.println("AHHHHHHHH" + status);
                                    if ( (status.equals("false") || status.equals("open")) && !document.getId().equals("") && document.getId() != null && isPostType(NearbySwaps.toggle, currentPost) && includePost(currentPost)) {

                                        posts.add(currentPost);
                                        i++;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            //sort the posts by their lon and lat distance
                            //and creating the comparator here
                            Collections.sort(posts, new Comparator<JSONObject>() {
                                public int compare(JSONObject o1, JSONObject o2) {
                                    // compare two instance of `Score` and return `int` as result.
                                    //return o2.getScores().get(0).compareTo(o1.getScores().get(0));
                                    double[] o1_pos = new double[]{0.0,0.0};
                                    double[] o2_pos = new double[]{0.0,0.0};

                                    try {
                                        o1_pos[0] = o1.getDouble("LAT");
                                        o1_pos[1] = o1.getDouble("LON");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        o2_pos[0] = o2.getDouble("LAT");
                                        o2_pos[1] = o2.getDouble("LON");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                    double distOne = Location.euclidianDistance(NearbySwaps.LAT, NearbySwaps.LON, o1_pos[0], o1_pos[1]);
                                    double distTwo = Location.euclidianDistance(NearbySwaps.LAT, NearbySwaps.LON, o2_pos[0], o2_pos[1]);

                                    if (distOne >= distTwo) {
                                        return 1;
                                    }else{
                                        return -1;
                                    }
                                }
                            });
                            for (JSONObject post : posts){
                                addPost(post);
                            }
                        } else {
                            System.out.println("Error getting documents" + task.getException());
                        }

                        //reveal updated posts once retrieved
                        postsLayout.setVisibility(View.VISIBLE);
                        pBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    /**
     * Takes in post information in JSON format. Creates a PostView and adds it to the activity
     *
     * @param  jsonObject a JSONObject with the post information
     * @return      nothing
     */
    public void addPost(JSONObject jsonObject){
        addMarker(jsonObject);

        LinearLayout posts = findViewById(R.id.PostLinearLayout);
        PostCardView newPost = new PostCardView(getApplicationContext(), jsonObject);
        System.out.println(jsonObject);
        if (newPost.setOnClickListener(NearbySwaps.this)) {
            posts.addView(newPost);
        }
    }

    public void addMarker(JSONObject jsonObject){
        try {
            double lat = jsonObject.getDouble("LAT");
            double lon = jsonObject.getDouble("LON");
            LatLng latLng = new LatLng(lat, lon);
            //setting need and offer appropraitely based on if they exist
            String need = jsonObject.getString("need");
            String offer = jsonObject.getString("offer");
            String name;

            if((!need.equals("") && need != null ) && (!offer.equals("") && offer != null )){
                name = "Swap";
            }else if ( (need.equals("") || need == null ) && (!offer.equals("") && offer != null )){
                name = "Free Offer";
            }else {
                name = "Needs Free Item";
            }

            Random rand = new Random();
            double noise = rand.nextDouble() / 10000;

            Marker marker = NearbySwaps.map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(name));

            marker.setTag(jsonObject);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void removeMarkers(){
        NearbySwaps.map.clear();
    }

    private boolean isPostType(int toggle, JSONObject jsonObject){
        String need;
        String offer;

        try {
            need = jsonObject.getString("need");
        } catch (JSONException e) {
            need = "";
        }

        try {
            offer = jsonObject.getString("offer");
        } catch (JSONException e) {
            offer = "";
        }


        if(toggle == 2){
            //free service offered --> we should have the need field empty
            return need.equals("") && !offer.equals("");
        }else if(toggle == 1){
            //swap --> both shouldn't be empty
            return !need.equals("") && !offer.equals("");
        }else{
            //needs free service --> should be an empty offer field
            return !need.equals("") && offer.equals("");
        }
    }

    private boolean includePost(JSONObject currentPost) {
        if (query.equals("")) return true; // when query is empty, every post is good
        // (?i) is a case-insensitive flag
        String regex = String.format("(?i)(.*)%s(.*)", query);
        String need, offer, contact;

        try {
            need = currentPost.getString("need");
        } catch (JSONException e) {
            need = "";
        }
        try {
            offer = currentPost.getString("offer");
        } catch (JSONException e) {
            offer = "";
        }

        try {
            contact = currentPost.getString("contact");
        } catch (JSONException e) {
            contact = "";
        }

        try {
            String location = currentPost.getString("location");
            String details = currentPost.getString("details");
            String email = currentPost.getString("user_id");
            String[] targets = new String[] {location, details, need, offer, email, contact};
            for (String t : targets) {
                if (t.matches(regex))
                    return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Initializes mode toggles with their OnClickListeners when the activity is created.
     * Modifications should be made here to add calls to backend when switch is toggled
     *
     * @return      nothing
     */
    private void initializeToggles(){
        ImageButton freeServices = findViewById(R.id.freeServicesToggle); //Option 0
        freeServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setToggle(0);
                retrievePosts(0);
            }
        });
        ImageButton swap = findViewById(R.id.swapToggle); //Option 1
        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setToggle(1);
                retrievePosts(1);
            }
        });
        ImageButton needsFree = findViewById(R.id.needsFreeToggle); //Option 2
        needsFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setToggle(2);
                retrievePosts(2);
            }
        });

        setToggle(1);
    }

    /**
     * Set's the toggles colors based on which toggle was pressed
     */
    private void setToggle(int newToggle){
        NearbySwaps.toggle = newToggle;
        ImageButton freeServices = findViewById(R.id.freeServicesToggle); //Option 0
        ImageButton swap = findViewById(R.id.swapToggle); //Option 1
        ImageButton needsFree = findViewById(R.id.needsFreeToggle); //Option 2
        if(NearbySwaps.toggle == 0){
            setToggleSelected(freeServices, 0);
            setToggleDeselected(swap, 1);
            setToggleDeselected(needsFree, 2);
        }else if (NearbySwaps.toggle == 1){
            setToggleDeselected(freeServices, 0);
            setToggleSelected(swap, 1);
            setToggleDeselected(needsFree, 2);
        }else{
            setToggleDeselected(freeServices, 0);
            setToggleDeselected(swap, 1);
            setToggleSelected(needsFree, 2);
        }
    }

    /**
     * Setting Toggle Style Helper Function
     *
     * @return      nothing
     */
    private void setToggleSelected(ImageButton button, int toggle){
        if (toggle == 0) {
            button.setImageResource(R.mipmap.recieve_icon_light);
        }else if(toggle == 1){
            button.setImageResource(R.mipmap.swap_icon_light);
        }else{
            button.setImageResource(R.mipmap.give_icon_light);
        }
//        button.setTextColor( Color.parseColor("#F4F7F9") );
    }

    /**
     * Setting Toggle Style Helper Function
     *
     * @return      nothing
     */
    private void setToggleDeselected(ImageButton button, int toggle){
        if (toggle == 0) {
            button.setImageResource(R.mipmap.recieve_icon_dark);
        }else if(toggle == 1){
            button.setImageResource(R.mipmap.swap_icon_dark);
        }else{
            button.setImageResource(R.mipmap.give_icon_dark);
        }
//        button.setBackground(getResources().getDrawable(R.drawable.modetoggle));
//        button.setTextColor( Color.parseColor("#535353") );
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
                        // TextView userNameView = findViewById(R.id.userName);
                        // userNameView.setText(userName);
                        setProfilePicture((String) user_data.get("avatar"));
//                        display_user_info(user_data);
                    } else {
                        Toast.makeText(NearbySwaps.this,
                                "Error: INVALID USER", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //TODO: Fail with task: DocumentSnapshot
                    Toast.makeText(NearbySwaps.this,
                            "Error: COULD NOT FETCH USER", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}