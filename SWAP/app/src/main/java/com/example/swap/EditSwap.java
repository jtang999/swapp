package com.example.swap;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditSwap extends AppCompatActivity {

    EditText needs;
    EditText offers;
    EditText details;
    EditText needBy;

    Button close_btn;

    // String postID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_swap);

        initializePage();
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goViewSwaps();
            }
        });
    }

    private void initializePage() {
        Intent intent = getIntent();
        needs = findViewById(R.id.edit_need);
        offers = findViewById(R.id.edit_offer);
        details = findViewById(R.id.edit_details);
        needBy = findViewById(R.id.edit_needed_by);

        needs.setText(intent.getCharSequenceExtra("edit_need"));
        offers.setText(intent.getCharSequenceExtra("edit_offer"));
        details.setText(intent.getCharSequenceExtra("edit_details"));
        needBy.setText(intent.getCharSequenceExtra("edit_need_time"));
        // postID = intent.getStringExtra("POST_ID");

        close_btn = findViewById(R.id.close_button2);
    }

    /** Copied from CreateSwap. Sets the exit button.
     */
    private void goViewSwaps() {
        finish();
    }
}