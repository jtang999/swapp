package com.example.swap;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Attr;

public class PostCardView extends CardView {
    String wanted;
    String needed;
    String profileImageURL;
    JSONObject jsonData;
    public PostCardView(@NonNull Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.post_card_view, this);
        this.wanted="DEFAULT";
        this.needed="DEFAULT";
        updateInfo();
    }

    public PostCardView(@NonNull Context context, AttributeSet attr) {
        super(context);

        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.post_card_view, this);
        this.wanted="Auto repair - help me change the oil in my car";
        this.needed="Any skill found on my profile page";
        updateInfo();
    }

    public PostCardView(@NonNull Context context, JSONObject jsonData) {
        super(context);

        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.post_card_view, this);

        parseData(jsonData);
        updateInfo();

    }

    private void parseData(JSONObject jsonData) {
        this.jsonData = jsonData;
        try {
            this.needed = jsonData.getString("looking for");
            this.wanted = jsonData.getString("in exchange for");
            this.profileImageURL = jsonData.getString("profileImageURL");
        } catch (JSONException e) {
            this.needed = "ERROR";
            this.wanted = "ERROR";
            this.profileImageURL = "ERROR";
            e.printStackTrace();
        }
    }

    private void updateInfo(){
        if (this.wanted != null){
            TextView want = findViewById(R.id.Wanted);
            String wantString = "<b>" + "Looking For:" + "</b> " + this.wanted;
            want.setText(Html.fromHtml(wantString));
        }
        if (this.needed != null){
            TextView need = findViewById(R.id.Needed);
            String needString = "<b>" + "In Exchange For:" + "</b> " + this.needed;
            need.setText(Html.fromHtml(needString));
        }
        if(this.profileImageURL != null){
            //nothing here yet because there are no images to download
        }
    }
}
