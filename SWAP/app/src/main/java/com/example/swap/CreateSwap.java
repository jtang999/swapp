package com.example.swap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.widget.SwitchCompat;

public class CreateSwap extends AppCompatActivity {
    private static final String TAG = "CreateSwap";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_swap);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final EditText need = (EditText)findViewById(R.id.user_need);
        final EditText offer = (EditText)findViewById(R.id.user_offer);
        final EditText details = (EditText)findViewById(R.id.user_details);
        final EditText location= (EditText)findViewById(R.id.user_location);
        final EditText expiration= (EditText)findViewById(R.id.user_needed_by);
        final EditText contact= (EditText)findViewById(R.id.user_contact);

        Button resolvePostButton = findViewById(R.id.button2);
        resolvePostButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                //Create New Post
                Map<String, Object> post = new HashMap<>();
                post.put("need", need.getText().toString());
                post.put("need_image", "");
                post.put("offer", offer.getText().toString());
                post.put("details", details.getText().toString());
                post.put("location", location.getText().toString());
                post.put("expiration", expiration.getText().toString());
                post.put("contact", contact.getText().toString());

                // Add a new document with a generated ID
                db.collection("posts")
                        .add(post)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });

//                Intent i = new Intent(NearbySwaps.this, CreateSwap.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(i);
            }
        });
    }
}