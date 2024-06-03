package com.example.teamdraft.ui.home.workSpace;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamdraft.R;
import com.example.teamdraft.ui.home.workSpace.cardActivity.users.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity {
    String boardId;
    UsersAdapter adapter;
    ArrayList<UserRole> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workspace_activity_users);

        ImageButton backButton = findViewById(R.id.backButtonUsers);
        backButton.setOnClickListener(v -> finish());

        Bundle data = getIntent().getExtras();
        if (data != null) {
            boardId = data.getString("boardId");

            getData(boardId);
            ListView listView = findViewById(R.id.workspaceUsersListView);
            adapter = new UsersAdapter(this, users, boardId, getSupportFragmentManager());
            listView.setAdapter(adapter);
        }
    }

    void getData(String boardId) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("boards").child(boardId).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    String role = userSnapshot.getValue(String.class);

                    mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserRole user = new UserRole(snapshot.getValue(User.class), role);
                            users.add(user);

                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}