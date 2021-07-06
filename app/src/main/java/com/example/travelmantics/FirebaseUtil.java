package com.example.travelmantics;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    public static FirebaseStorage mFirebaseStorage;
    public static StorageReference mStorageReference;
    private static FirebaseUtil firebaseUtil;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseAuth.AuthStateListener mAuthStateListener;
    public static List<TravelDeal> mDeals;
    private static final int RC_SIGN_IN = 123;
    private static ListActivity caller;
    public static boolean isAdmin;


    public FirebaseUtil() {
    }

    public static void openFbReference(String ref, final ListActivity callerActivity) {
        if (firebaseUtil == null) {
            firebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();

            caller = callerActivity;

            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        FirebaseUtil.signIn();
                    } else {
                        String userId = firebaseAuth.getUid();
                        checkAdmin(userId);
                    }
                    Toast.makeText(callerActivity.getBaseContext(), "Welcome back!", Toast.LENGTH_LONG).show();
                }
            };
            connectStorage();
        }
        mDeals = new ArrayList<>();
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }

    private static void checkAdmin(String uid) {
        FirebaseUtil.isAdmin = false;
        DatabaseReference ref = mFirebaseDatabase.getReference().child("administrators").child(uid);
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("Admin", "You are an administrator!");
                FirebaseUtil.isAdmin = true;
                caller.showMessage();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref.addChildEventListener(listener);
    }

    private static void signIn() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
//                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
//                , new AuthUI.IdpConfig.FacebookBuilder().build()
//                ,new AuthUI.IdpConfig.TwitterBuilder().build()
        );

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        caller.startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    public static void attachListener() {
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public static void detachListener() {
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    public static void connectStorage(){
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("deals_pictures");
    }
}
