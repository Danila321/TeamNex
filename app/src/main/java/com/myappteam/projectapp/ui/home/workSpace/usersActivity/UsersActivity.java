package com.myappteam.projectapp.ui.home.workSpace.usersActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.myappteam.projectapp.GetUserRole;
import com.myappteam.projectapp.R;
import com.myappteam.projectapp.ui.home.workSpace.cardActivity.users.User;
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

            GetUserRole.getUserRole(boardId, this::roleManager);
        }
    }

    void roleManager(String role) {
        ListView listView = findViewById(R.id.workspaceUsersListView);
        switch (role) {
            case "owner":
                adapter = new UsersAdapter(this, users, boardId, getSupportFragmentManager(), true);
                break;
            case "admin":
            case "user":
                adapter = new UsersAdapter(this, users, boardId, getSupportFragmentManager(), false);
                break;
        }
        listView.setAdapter(adapter);

        //Получаем данные из БД
        getData(boardId);
    }

    void getData(String boardId) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("boards").child(boardId).child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();

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