package com.example.teamdraft.ui.home.workSpace.cardActivity.users;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.teamdraft.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AddUsersAdapter extends ArrayAdapter<UserType> {
    ArrayList<String> data;
    AddUserDialog dialog;

    public AddUsersAdapter(@NonNull Context context, ArrayList<UserType> users, ArrayList<String> data, AddUserDialog dialog) {
        super(context, R.layout.workspace_card_adduser_custom_item, users);
        this.data = data;
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.workspace_card_adduser_custom_item, null);
        }

        UserType userType = getItem(position);
        User user = userType.getUser();
        boolean type = userType.getType();

        //Показываем фотографию
        ImageView userImage = convertView.findViewById(R.id.cardAddUserImageView);
        Picasso.get().load(Uri.parse(user.getPhoto())).into(userImage);

        //Показываем имя
        TextView userName = convertView.findViewById(R.id.cardUserName);
        userName.setText(user.getName());

        ImageButton button = convertView.findViewById(R.id.imageButtonAddDeleteUser);

        if (type) {
            button.setImageResource(R.drawable.close);
            button.setOnClickListener(v -> deleteUser(user.getId()));
            button.setBackgroundResource(R.drawable.workspace_button_userdelete);
        } else {
            button.setImageResource(R.drawable.add);
            button.setOnClickListener(v -> addUser(user.getId()));
            button.setBackgroundResource(R.drawable.workspace_button_useradd);
        }

        return convertView;
    }

    void deleteUser(String userId) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("boards").child(data.get(0)).child("items").child(data.get(1)).child("cards").child(data.get(2)).child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
                dialog.getUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void addUser(String userId) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("boards").child(data.get(0)).child("items").child(data.get(1)).child("cards").child(data.get(2)).child("users").child(userId).setValue("");
        dialog.getUsers();
    }
}
