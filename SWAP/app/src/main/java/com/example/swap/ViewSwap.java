package com.example.swap;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

    /**
     * Saves post information and starts EditSwap activity
     */
    public void editPost(String post_id) {
        Intent intent = new Intent(ViewSwap.this, EditSwap.class);
        intent.putExtra("POST_ID", post_id);
        intent.putExtra("edit_need", this.needs.getText());
        intent.putExtra("edit_offer", this.offers.getText());
        intent.putExtra("edit_details", this.details.getText());
        intent.putExtra("edit_need_time", this.need_time.getText());
        startActivity(intent);
    }

    private void getPostDataFromDB(final String post_id) {
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
                        process_display_RawInfo(post_data, post_id);

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


    private void process_display_RawInfo(HashMap<String, Object> data, String post_id) {
        final String uid = (String)data.get("user_id");
        process_display_UserInfo(uid, post_id);
        post_date.setText((String)data.get("creation_time"));

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

        details.setText((String)data.get("details"));
        location.setText((String)data.get("location"));
        need_time.setText((String)data.get("expiration"));

        /*String contact_info = "";
        if (data.get("phone") != null && data.get("phone").equals("")) {
            contact_info = "Phone: " + (String)data.get("phone") + "\n";
        }
        if (data.get("email") != null && data.get("email").equals("")) {
            contact_info += "Email: " + (String)data.get("email");
        }
        contact.setText(contact_info);*/
        final String phone = (String)data.get("contact");
        final String email = (String)data.get("email");
        if (!data.get("status").equals("closed") && !data.get("status").equals("false")) {
            phone_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makePhoneCall(phone);

                }
            });

            send_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (email == null || email.isEmpty() || !email.contains("@")) {
                        sendEmail(uid);
                    } else {
                        sendEmail(email);
                    }
                }
            });
        } else {
            phone_call.setVisibility(View.GONE);
            send_email.setVisibility(View.GONE);
        }


    }

    private void process_display_UserInfo(String uid, final String post_id) {
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
                        display_user_info(user_data, post_id);
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
        edit_btn.setVisibility(View.GONE);
        resolve_btn.setVisibility(View.GONE);
        delete_btn.setVisibility(View.GONE);

    }

    private void display_user_info(final HashMap<String, Object> user_data, String post_id) {
        //TODO: PASS IN IMG LINK
        String user_name = (String) user_data.get("user_name");
        final String user_id = (String)user_data.get("email");
        String cur_user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        username.setText(user_name);
        if (user_data.get("avatar")==null || user_data.get("avatar").equals("")) {
            avatar_btn.setImageResource(R.drawable.avatar);
        } else {
            String url = (String) user_data.get("avatar");
            Picasso.with(this).load(url).placeholder(R.drawable.avatar).resize(200, 200).transform(new CircleTransform()).into(avatar_btn);
        }
        displayOwnerView(cur_user, user_id, post_id);
        avatar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid_msg = user_id;
                //Toast.makeText(ViewSjwap.this, "user_id: "+user_id + "\ncur_user: "+cur_user, Toast.LENGTH_SHORT).show();
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
        Toast.makeText(ViewSwap.this,
                "Re-direct to phone call", Toast.LENGTH_SHORT).show();
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:01-" + phoneNum));
        startActivity(callIntent);
        //finish();
    }



    private void sendEmail(String email) {
        if (email == null || email.equals("")) {
            Toast.makeText(ViewSwap.this,
                    "Owner does not provide an email.", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(ViewSwap.this,
                "Re-direct to email", Toast.LENGTH_SHORT).show();
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SWAP");
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "SWAP service");

        startActivity(Intent.createChooser(emailIntent, "Send your email in:"));
        //finish();
    }

    private void displayOwnerView(final String cur_user, String post_owner, final String post_id) {
        if (!isOwner(cur_user, post_owner)) return;
        edit_btn.setVisibility(View.VISIBLE);
        resolve_btn.setVisibility(View.VISIBLE);
        delete_btn.setVisibility(View.VISIBLE);

        //delete function
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_post(post_id, cur_user);
            }
        });

        //resolve function: check status
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").document(post_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        final boolean status = (boolean) document.getData().get("status");
                        if (status) {
                            resolve_btn.setText("RESOLVED");
                            edit_btn.setVisibility(View.GONE);
                        }
                        resolve_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mark_resolved(post_id, status);
                            }
                        });
                        edit_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editPost(post_id);
                            }
                        });
                    } else {
                        Toast.makeText(ViewSwap.this,
                                "Post no longer available!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ViewSwap.this,
                            "Fail retrieving status data!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void mark_resolved(String post_id, boolean status) {
        if (status == false) {
            AlertDialog diaBox = ask_resolved_option(post_id);
            diaBox.show();
        } else {
            Toast.makeText(ViewSwap.this,
                    "Cannot change a resolved post!", Toast.LENGTH_SHORT).show();
        }
    }

    private void mark_resolved_helper(String post_id) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").document(post_id).update("status", true);
        Toast.makeText(ViewSwap.this,
                "Post Resolved!", Toast.LENGTH_SHORT).show();
        goCurSwapIntent(post_id);
        /*Intent i = new Intent(ViewSwap.this, ViewSwap.class);
        i.putExtra("POST_ID", post_id);
        startActivity(i);
        finish();*/
        //resolve_btn.setText("RESOLVED");
        //edit_btn.setVisibility(View.INVISIBLE);
    }

    private void goCurSwapIntent(String post_id) {
        Intent i = new Intent(ViewSwap.this, ViewSwap.class);
        i.putExtra("POST_ID", post_id);
        startActivity(i);
        finish();
    }

    private void delete_post(String post_id, String uid) {
        AlertDialog diaBox = ask_delete_option(post_id, uid);
        diaBox.show();
    }

    private void delete_post_helper(final String post_id, final String uid) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").document(post_id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ViewSwap.this,
                                "Post Deleted!", Toast.LENGTH_SHORT).show();
                        //delete_post_in_user_profile(post_id, uid);
                        goProfileIntent(uid);
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewSwap.this,
                                "Error deleting post " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /*private void delete_post_in_user_profile(String post_id, String uid) {
    }*/

    private AlertDialog ask_delete_option(final String post_id, final String uid)
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to delete this post?\nThis is a permanent action.")
                .setIcon(R.drawable.delete_icon)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        delete_post_helper(post_id, uid);
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();

        return myQuittingDialogBox;
    }

    private AlertDialog ask_resolved_option(final String post_id)
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                // set message, title, and icon
                .setTitle("Resolve")
                .setMessage("Do you want to mark this post as resolved? \nThis is a permanent action.")
                .setIcon(R.drawable.resolve_icon)

                .setPositiveButton("Resolve", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        mark_resolved_helper(post_id);
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

        return myQuittingDialogBox;
    }

    private boolean isOwner(String cur_user, String post_owner) {
        return cur_user.equals(post_owner);
    }
}
