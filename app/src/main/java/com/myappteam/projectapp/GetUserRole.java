package com.myappteam.projectapp;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class GetUserRole {
    public interface RoleCallback {
        void onRoleReceived(String role);
    }

    public static void getUserRole(String boardName, RoleCallback callback) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("boards")
                .child(boardName)
                .child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                if (Objects.equals(data.getKey(), FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    callback.onRoleReceived(String.valueOf(data.getValue()));
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
