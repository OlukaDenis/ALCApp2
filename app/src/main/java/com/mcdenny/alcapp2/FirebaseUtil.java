package com.mcdenny.alcapp2;

import android.app.Activity;
import android.util.Log;

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
import com.mcdenny.alcapp2.model.TravelDeal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FirebaseUtil {
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    public static FirebaseStorage mStorage;
    public static StorageReference mStorageReference;
    private static FirebaseUtil firebaseUtil;
    public static FirebaseAuth firebaseAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;
    public static ArrayList<TravelDeal> travelDeals;
    public static final int RC_SIGN_IN = 123;
    private static TravelList activity;
    public static boolean isAdmin = false;
    public static String userUid;

    private FirebaseUtil() {};

    public static void openFirebaseReference(String reference, final TravelList callerActivity){
        if (firebaseUtil == null){
            firebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            activity = callerActivity;

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null){
                        FirebaseUtil.signIn();
                    }else {
                        String userID = firebaseAuth.getUid();
                        userUid = firebaseAuth.getUid();
                        TravelList.uid = firebaseAuth.getUid();
                        checkAdmin(userID);
                       // activity.checkAdmin(userID);
                    }
                    Log.i("FirebaseUtil: ", "You are signed in ");
                }
            };

            connectStorage();
        }
        travelDeals = new ArrayList<TravelDeal>();
        mDatabaseReference = mFirebaseDatabase.getReference().child(reference);
    }

    public static void connectStorage(){
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference().child("deals_pictures");
    }

    private static void checkAdmin(String uid) {
        FirebaseUtil.isAdmin = false;
        Log.i("User", "You are not an admin! ");

        DatabaseReference ref = mFirebaseDatabase.getReference().child("administrators").child(uid);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.isAdmin = true;
                activity.showMenu();
                Log.i("Admin", "You are an admin! ");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private static void signIn(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        Log.i("FirebaseUtil", "SigninBuilder called");

    }


    public static void attachListener(){
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    public static void removeListener(){
        firebaseAuth.removeAuthStateListener(mAuthListener);
    }


}
