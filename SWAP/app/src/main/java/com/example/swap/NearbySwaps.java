package com.example.swap;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;

public class NearbySwaps extends AppCompatActivity {
    public static int toggle = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_swaps);
        initializeToggles();


    }

    public void initializeToggles(){
        Button freeServices = findViewById(R.id.freeServicesToggle); //Option 0
        freeServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setToggle(0);
            }
        });
        Button swap = findViewById(R.id.swapToggle); //Option 1
        freeServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setToggle(1);
            }
        });
        Button needsFree = findViewById(R.id.needsFreeToggle); //Option 2
        freeServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setToggle(2);
            }
        });
    }

    public void setToggle(int newToggle){
        NearbySwaps.toggle = newToggle;
        Button freeServices = findViewById(R.id.freeServicesToggle); //Option 0
        Button swap = findViewById(R.id.swapToggle); //Option 1
        Button needsFree = findViewById(R.id.needsFreeToggle); //Option 2
        if(NearbySwaps.toggle == 0){
            freeServices.setBackground(Drawable.createFromPath("@drawable/createbutton"));
            swap.setBackground(Drawable.createFromPath("@drawable/modetoggle"));
            needsFree.setBackground(Drawable.createFromPath("@drawable/modetoggle"));
        }else if (NearbySwaps.toggle == 1){
            freeServices.setBackground(Drawable.createFromPath("@drawable/modetoggle"));
            swap.setBackground(Drawable.createFromPath("@drawable/createbutton"));
            needsFree.setBackground(Drawable.createFromPath("@drawable/modetoggle"));
        }else{
            freeServices.setBackground(Drawable.createFromPath("@drawable/modetoggle"));
            swap.setBackground(Drawable.createFromPath("@drawable/modetoggle"));
            needsFree.setBackground(Drawable.createFromPath("@drawable/createbutton"));
        }
    }
}