package com.example.massa.luxvilla.utils;

import android.support.design.widget.TextInputLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by massa on 06/07/2017.
 */

public class firebaseutils {

    public static void checkusername(String username, final TextInputLayout textInputLayout){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        myRef.orderByChild("user_name").equalTo(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    textInputLayout.setError("User name já está em uso.");
                }else{
                    textInputLayout.setError("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void setuserfirstdata(String userid,String username){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        Map<String,Object> data = new HashMap<>();
        data.put("user_name",username);
        data.put("user_bio","Para adicionar uma bio edite o prefil");
        myRef.child(userid).setValue(data);
    }
}
