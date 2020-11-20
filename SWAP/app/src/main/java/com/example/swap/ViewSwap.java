package com.example.swap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Random;

public class ViewSwap extends AppCompatActivity {
    //TODO: MOVE THE XTR_MESSAGE TO PREVIOUS INTENT
    public static final String XTR_MESSAGE = "com.example.SWAP.ViewSwap.MESSAGE";

    TextView username;
    TextView needs;
    TextView offers;
    TextView details;
    TextView contact;
    TextView location;
    TextView post_date;
    TextView expire_date;

    Button edit_btn;
    Button resolve_btn;
    Button delete_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_swap);

        initializePage();

        Intent intent = getIntent();
        String post_id = intent.getStringExtra(XTR_MESSAGE);

        //HashMap<String, Object> post_data = new HashMap<>();
        getPostDataFromDB(post_id);


    }

    private void getPostDataFromDB(String post_id) {
        final FirebaseFirestore database = FirebaseFirestore.getInstance();

        CollectionReference cref = database.collection("posts");
        DocumentReference dref = cref.document(post_id);
        dref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        HashMap<String, Object> post_data = new HashMap<>();
                        post_data = (HashMap<String, Object>) document.getData();
                        process_display_RawInfo(post_data);
                    } else {
                        //TODO: CANNOT FIND SUCH DOCUMENT: POST DOES NOT EXIST ANY MORE
                        displayError();
                    }

                } else {
                    //TODO: Fail with task: DocumentSnapshot
                    details.setText(task.getException().toString());
                }
            }
        });
    }

    private void displayError() {
        username.setText("ERROR: INVALID POST");
        needs.setText("ERROR: INVALID POST");
        offers.setText("ERROR: INVALID POST");
        details.setText("ERROR: INVALID POST");
        contact.setText("ERROR: INVALID POST");
        location.setText("ERROR: INVALID POST");
        post_date.setText("ERROR: INVALID POST");
    }


    private void process_display_RawInfo(HashMap<String, Object> data) {
        String uid = (String)data.get("user_id");
        process_display_UserInfo(uid);
        post_date.setText((String)data.get("post_date"));

        if (data.get("need") == null) {
            needs.setText("");
        } else {
            needs.setText((String)data.get("need"));
        }

        if (data.get("offer") == null) {
            offers.setText("");
        } else {
            offers.setText((String) data.get("offer"));
        }

        details.setText((String)data.get("detail_text"));
        location.setText((String)data.get("location"));

        String contact_info = "";
        if (data.get("phone") != null && data.get("phone").equals("")) {
            contact_info = "Phone: " + (String)data.get("phone") + "\n";
        }
        if (data.get("email") != null && data.get("email").equals("")) {
            contact_info += "Email: " + (String)data.get("email");
        }
        contact.setText(contact_info);


    }

    private void process_display_UserInfo(String uid) {
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
                        HashMap<String, Object> user_data = new HashMap<>();
                        user_data = (HashMap<String, Object>) document.getData();
                        String user_name = (String) user_data.get("user_name");
                        username.setText(user_name);
                    } else {
                        //TODO: CANNOT FIND SUCH DOCUMENT: POST DOES NOT EXIST ANY MORE
                        username.setText("Error: finding the user");
                    }

                } else {
                    //TODO: Fail with task: DocumentSnapshot
                    username.setText(task.getException().toString());
                }
            }
        });
    }



    /**
     * Loads the data into the page on activity start
     *
     * @return      nothing
     */
    private void initializePage(){
        username = findViewById(R.id.username);
        needs = findViewById(R.id.post_needs);
        offers = findViewById(R.id.post_offers);
        details = findViewById(R.id.post_details);
        contact = findViewById(R.id.post_contact);
        location = findViewById(R.id.post_location);
        post_date = findViewById(R.id.user_posted_on);
        expire_date = findViewById(R.id.text_time_info);


        edit_btn = findViewById(R.id.edit_button);
        resolve_btn = findViewById(R.id.resolve_post);
        delete_btn = findViewById(R.id.mark_resolved);


        //TODO: remove edit, resolve, delete functions for other users.
        edit_btn.setVisibility(View.INVISIBLE);
        resolve_btn.setVisibility(View.INVISIBLE);
        delete_btn.setVisibility(View.INVISIBLE);

        //TODO: add expire date later
        expire_date.setVisibility(View.GONE);
    }
}
