package com.example.swap;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.squareup.picasso.Picasso;
import com.example.swap.ViewSwap;

import org.json.JSONException;
import org.json.JSONObject;

public class PostCardView extends CardView {
    public static final String XTR_MESSAGE = "com.example.SWAP.NearbySwaps.MESSAGE";
    String offered;
    String needed;
    String profileImageURL;
    JSONObject jsonData;
    Context context;
    public PostCardView(@NonNull Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.post_card_view, this);
        this.offered="DEFAULT";
        this.needed="DEFAULT";
        updateInfo();
    }

    public PostCardView(@NonNull Context context, AttributeSet attr) {
        super(context);

        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.post_card_view, this);
        this.offered="Auto repair - help me change the oil in my car";
        this.needed="Any skill found on my profile page";
        updateInfo();
    }

    /**
     * Creates a postCardView. Takes in the app context and a JSONObject with the data for the post.
     *
     * @param jsonData A JSONObject containing ALL of the data for the post.
     */
    public PostCardView(@NonNull Context context, JSONObject jsonData) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.post_card_view, this);

        parseData(jsonData);
        updateInfo();
    }

    /**
     * Parses the data given to a post object and
     * adds the name, swap, image-URL, and jsonObject
     * as instance attributes.
     * MAYBE NEED MODIFICATIONS DEPENDING ON STRUCTURE OF DATABASE RESPONSES
     *
     * @param jsonData A JSONObject containing ALL of the data for the post.
     */
    private void parseData(JSONObject jsonData) {
        this.jsonData = jsonData;
        try {
            this.needed = jsonData.getString("need");
        } catch (JSONException e) {
            this.needed = "ERROR";
            e.printStackTrace();
        }

        try {
            this.offered = jsonData.getString("offer");
        }catch (JSONException e) {
            this.offered = "ERROR";
            e.printStackTrace();
        }

        try {
            this.profileImageURL = jsonData.getString("image_url");
            if (this.profileImageURL == null || this.profileImageURL.equals("")){
                this.profileImageURL = "NONE";
            }
        } catch (JSONException e) {
            this.profileImageURL = "ERROR";
            e.printStackTrace();
        }

    }

    private void updateInfo(){
        TextView offered = findViewById(R.id.offered);
        if (this.offered != null && !this.offered.equals("")){
            String wantString = "<b>" + "Offered:" + "</b> " + this.offered;
            offered.setText(Html.fromHtml(wantString));
        }else{
            offered.setVisibility(GONE);
        }

        TextView need = findViewById(R.id.needed);
        if (this.needed != null && !this.needed.equals("")){
            String needString = "<b>" + "Needed:" + "</b> " + this.needed;
            need.setText(Html.fromHtml(needString));
        }else{
            need.setVisibility(GONE);
        }

        if(this.profileImageURL != null){
            ImageView profileImage = findViewById(R.id.profileImage);
            Picasso.with(this.context).load(this.profileImageURL).placeholder(R.mipmap.default_profile_alt).error(R.mipmap.default_profile_alt).into(profileImage);
            //nothing here yet because there are no images to download
        }
    }

    public boolean setOnClickListener(final Context packageContext) {
        Button button = findViewById(R.id.button);

        try {
            final String postID = this.jsonData.getString("post_ID");

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(packageContext, ViewSwap.class);
                    i.putExtra("POST_ID", postID);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    packageContext.startActivity(i);
                }
            });
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

    }
}
