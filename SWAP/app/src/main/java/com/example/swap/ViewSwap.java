package com.example.swap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ViewSwap extends AppCompatActivity {
    public static final String XTR_MESSAGE = "com.example.SWAP.ViewSwap.MESSAGE";

    TextView username;
    TextView needs;
    TextView offers;
    TextView details;
    TextView location;
    TextView post_date;
    TextView need_time;

    ImageButton avatar_btn;
    ImageButton phone_call;
    ImageButton send_email;
    Button close_btn;



    Button edit_btn;
    Button resolve_btn;
    Button delete_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_swap);

        initializePage();

        //Intent intent = getIntent();
        String post_id ="TsG6Gv9mkyaea7MvBnTB"; // intent.getStringExtra(XTR_MESSAGE);

        //HashMap<String, Object> post_data = new HashMap<>();
        getPostDataFromDB(getIntent().getStringExtra("POST_ID"));

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goNearybySwapsIntent();
            }
        });


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
                        //DONE: CANNOT FIND SUCH DOCUMENT: POST DOES NOT EXIST ANY MORE
                        displayError("POST NO LONGER AVAILABLE!");
                    }

                } else {
                    //TODO: Fail with task: DocumentSnapshot
                    displayError(task.getException().toString());
                }
            }
        });
    }

    private void displayError(String err) {
        Toast.makeText(ViewSwap.this,
                err, Toast.LENGTH_SHORT).show();
        /*username.setText("ERROR: INVALID POST");
        needs.setText("ERROR: INVALID POST");
        offers.setText("ERROR: INVALID POST");
        details.setText("ERROR: INVALID POST");
        //contact.setText("ERROR: INVALID POST");
        location.setText("ERROR: INVALID POST");
        post_date.setText("ERROR: INVALID POST");
        need_time.setText("ERROR: INVALID POST");*/
        username.setText("");
        needs.setText("");
        offers.setText("");
        details.setText("");
        //contact.setText("");
        location.setText("");
        post_date.setText("");
        need_time.setText("");
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
        need_time.setText((String)data.get("time"));

        /*String contact_info = "";
        if (data.get("phone") != null && data.get("phone").equals("")) {
            contact_info = "Phone: " + (String)data.get("phone") + "\n";
        }
        if (data.get("email") != null && data.get("email").equals("")) {
            contact_info += "Email: " + (String)data.get("email");
        }
        contact.setText(contact_info);*/
        final String phone = (String)data.get("phone");
        final String email = (String)data.get("email");

        phone_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall(phone);
                Toast.makeText(ViewSwap.this,
                        "Re-direct to phone call", Toast.LENGTH_SHORT).show();
            }
        });

        send_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail(email);
                Toast.makeText(ViewSwap.this,
                        "Re-direct to email", Toast.LENGTH_SHORT).show();
            }
        });


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
                        HashMap<String, Object> user_data;
                        user_data = (HashMap<String, Object>) document.getData();
                        display_user_info(user_data);
                    } else {
                        //TODO: CANNOT FIND SUCH DOCUMENT: POST DOES NOT EXIST ANY MORE
                        username.setText("");
                        Toast.makeText(ViewSwap.this,
                                "Error: INVALID USER", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //TODO: Fail with task: DocumentSnapshot
                    username.setText("");
                    Toast.makeText(ViewSwap.this,
                            task.getException().toString(), Toast.LENGTH_SHORT).show();
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
        //contact = findViewById(R.id.post_contact);
        location = findViewById(R.id.post_location);
        post_date = findViewById(R.id.user_posted_on);
        need_time = findViewById(R.id.user_time_info);

        avatar_btn = findViewById(R.id.user_profile_button);
        phone_call = findViewById(R.id.user_call);
        send_email = findViewById(R.id.user_email);
        close_btn = findViewById(R.id.close_button);

        edit_btn = findViewById(R.id.edit_button);
        resolve_btn = findViewById(R.id.resolve_post);
        delete_btn = findViewById(R.id.mark_resolved);


        //TODO: remove edit, resolve, delete functions for other users.
        edit_btn.setVisibility(View.INVISIBLE);
        resolve_btn.setVisibility(View.INVISIBLE);
        delete_btn.setVisibility(View.INVISIBLE);

    }

    private void display_user_info(final HashMap<String, Object> user_data) {
        //TODO: PASS IN IMG LINK
        String user_name = (String) user_data.get("user_name");
        username.setText(user_name);
        if (user_data.get("avatar")==null || user_data.get("avatar").equals("")) {
            avatar_btn.setImageResource(R.drawable.avatar);
        } else {
            String url = (String) user_data.get("avatar");
            Picasso.with(this).load(url).placeholder(R.drawable.avatar).resize(200, 200).into(avatar_btn);
        }
        avatar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid_msg = (String)user_data.get("email");
                goProfileIntent(uid_msg);
            }
        });
        //avatar_btn.setImageDrawable(R.drawable.);
    }

    private void goProfileIntent(String uid_msg) {
        Intent profile_intent = new Intent(this, ProfilePage.class);
        profile_intent.putExtra("UID", uid_msg);
        startActivity(profile_intent);
        finish();

    }

    private void goNearybySwapsIntent() {
        Intent profile_intent = new Intent(this, NearbySwaps.class);
        startActivity(profile_intent);
        finish();
    }

    private void makePhoneCall(String phoneNum) {
        if (phoneNum == null || phoneNum.equals("")) {
            Toast.makeText(ViewSwap.this,
                    "Owner does not provide a phone contact.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:01-" + phoneNum));
        startActivity(callIntent);
        finish();
    }



    private void sendEmail(String email) {
        if (email == null || email.equals("")) {
            Toast.makeText(ViewSwap.this,
                    "Owner does not provide an email.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SWAP");
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "SWAP service");

        startActivity(Intent.createChooser(emailIntent, "Send your email in:"));
        finish();
    }
}
