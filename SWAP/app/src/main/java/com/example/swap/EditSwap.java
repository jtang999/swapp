package com.example.swap;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditSwap extends AppCompatActivity {

    EditText needs;
    EditText offers;
    EditText details;
    EditText needBy;
    EditText phone;

    Button close_btn;
    Button save_btn;
    Button cancel_btn;

    //String post_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_swap);

        Intent intent = getIntent();
        final String post_id = intent.getStringExtra("POST_ID");
        initializePage(intent);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_post(post_id);
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void save_post(String post_id) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        needs = findViewById(R.id.edit_need);
        offers = findViewById(R.id.edit_offer);
        details = findViewById(R.id.edit_details);
        needBy = findViewById(R.id.edit_needed_by);
        phone = findViewById(R.id.edit_contact);

        db.collection("posts").document(post_id).update("need", needs.getText().toString());
        db.collection("posts").document(post_id).update("offer", offers.getText().toString());
        db.collection("posts").document(post_id).update("details", details.getText().toString());
        db.collection("posts").document(post_id).update("expiration", needBy.getText().toString());
        db.collection("posts").document(post_id).update("contact", phone.getText().toString());

        Toast.makeText(EditSwap.this,
                "Post Updated!", Toast.LENGTH_SHORT).show();

        goViewSwaps(post_id);

    }

    private void initializePage(Intent intent) {

        needs = findViewById(R.id.edit_need);
        offers = findViewById(R.id.edit_offer);
        details = findViewById(R.id.edit_details);
        needBy = findViewById(R.id.edit_needed_by);

        needs.setText(intent.getCharSequenceExtra("edit_need"));
        offers.setText(intent.getCharSequenceExtra("edit_offer"));
        details.setText(intent.getCharSequenceExtra("edit_details"));
        needBy.setText(intent.getCharSequenceExtra("edit_need_time"));


        close_btn = findViewById(R.id.close_button2);
        save_btn = findViewById(R.id.profileButton);
        cancel_btn = findViewById(R.id.mark_resolved);
    }

    /** Copied from CreateSwap. Sets the exit button.
     */
    private void goViewSwaps(String post_id) {
        Intent intent = new Intent(EditSwap.this, ViewSwap.class);
        intent.putExtra("POST_ID", post_id);
        startActivity(intent);
    }
}