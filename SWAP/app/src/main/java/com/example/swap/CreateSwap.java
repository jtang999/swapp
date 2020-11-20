package com.example.swap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class CreateSwap extends AppCompatActivity {
    private static final String TAG = "CreateSwap";
    private static int toggle = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_swap);
        initializeToggles();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final EditText need = (EditText)findViewById(R.id.user_need);
        final EditText offer = (EditText)findViewById(R.id.user_offer);
        final EditText details = (EditText)findViewById(R.id.user_details);
        final EditText location= (EditText)findViewById(R.id.user_location);
        final EditText expiration= (EditText)findViewById(R.id.user_needed_by);
        final EditText contact= (EditText)findViewById(R.id.user_contact);

        Button resolvePostButton = findViewById(R.id.profileButton);
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

    private void initializeToggles(){
        //These will be set visible or invisible depending on what the users presses
        //Also the input fields will be reset so that posts aren't created with extra data
        final EditText user_offer_input = findViewById(R.id.user_offer);
        final TextView user_offer_text = findViewById(R.id.text_useroffer);
        final EditText user_need_input = findViewById(R.id.user_need);
        final TextView user_need_text = findViewById(R.id.text_userneed);

        Button freeServices = findViewById(R.id.freeServicesToggle2); //Option 0
        freeServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setToggle(0);
                //turn off what I need
                user_need_input.setText("");
                user_need_input.setVisibility(View.GONE);
                user_need_text.setVisibility(View.GONE);
                //make sure what you're offering is visible
                user_offer_input.setVisibility(View.VISIBLE);
                user_offer_text.setVisibility(View.VISIBLE);
            }
        });
        Button swap = findViewById(R.id.swapToggle2); //Option 1
        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setToggle(1);
                //make both visible
                user_need_input.setVisibility(View.VISIBLE);
                user_need_text.setVisibility(View.VISIBLE);
                user_offer_input.setVisibility(View.VISIBLE);
                user_offer_text.setVisibility(View.VISIBLE);
            }
        });
        Button needsFree = findViewById(R.id.needsFreeToggle2); //Option 2
        needsFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setToggle(2);
                //turn off what you can offer
                user_offer_input.setText("");
                user_offer_input.setVisibility(View.GONE);
                user_offer_text.setVisibility(View.GONE);
                //turn on what you need
                user_need_input.setVisibility(View.VISIBLE);
                user_need_text.setVisibility(View.VISIBLE);
            }
        });

        setToggle(1);
    }

    /**
     * Set's the toggles colors based on which toggle was pressed
     */
    private void setToggle(int newToggle){
        CreateSwap.toggle = newToggle;
        Button freeServices = findViewById(R.id.freeServicesToggle2); //Option 0
        Button swap = findViewById(R.id.swapToggle2); //Option 1
        Button needsFree = findViewById(R.id.needsFreeToggle2); //Option 2
        if(CreateSwap.toggle == 0){
            setToggleSelected(freeServices);
            setToggleDeselected(swap);
            setToggleDeselected(needsFree);
        }else if (CreateSwap.toggle == 1){
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