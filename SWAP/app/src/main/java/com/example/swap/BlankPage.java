package com.example.swap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entry point of the app where user will be Signed-in first
 */
public class BlankPage extends AppCompatActivity {

    private final int RC_SIGN_IN = 1822;
    private final FirebaseFirestore DB = FirebaseFirestore.getInstance();
    private final String USERS_COLLECTION = "users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank_page);

        startSignIn();
    }

    /**
     * Attempt to Sign user in
     * If user is not in database, user email will be automatically registered
     * If user is already "registered", user will be signed in
     */
    private void startSignIn() {
        // only Google sign-in
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    /**
     * When the app is forced to stop, the user will be signed out
     */
    @Override
    protected void onStop() {
        super.onStop();
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    /**
     * Sign in logic
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Log.e("SIGN-IN", "User not found");
                    finish();
                }
                final String userEmail = user.getEmail();
                final String userName = user.getDisplayName();
                final String userPic = user.getPhotoUrl().toString();
                Log.i("EMAIL", userEmail);

                final DocumentReference userRef = DB.collection(USERS_COLLECTION).document(userEmail);
                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(userEmail, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(userEmail, "No user document");
                                Map<String, Object> data = new HashMap<>();
                                data.put("avatar", userPic);
                                data.put("email", userEmail);
                                data.put("post_ids", Collections.EMPTY_LIST);
                                data.put("rating", 0.0d);
                                data.put("user_name", userName);
                                userRef.set(data)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(userEmail, "Failed to add to Database");
                                            }
                                        })
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(userEmail, "User data successfully written!");
                                            }
                                        });
                            }
                            Intent intent = new Intent(BlankPage.this, NearbySwaps.class);
                            /**
                             * User's info is passed to the browsing page
                             */
                            intent.putExtra("userEmail", userEmail);
                            intent.putExtra("userName", userName);
                            intent.putExtra("userPic", userPic);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Log.e(userEmail, "get failed with ", task.getException());
                        }
                    }
                });
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                // exit app if sign-in is unsuccessful
            }
            finish();
        }
    }
}