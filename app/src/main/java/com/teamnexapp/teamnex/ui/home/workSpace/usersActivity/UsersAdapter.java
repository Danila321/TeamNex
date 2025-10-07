package com.teamnexapp.teamnex.ui.home.workSpace.usersActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.teamnexapp.teamnex.R;
import com.teamnexapp.teamnex.ui.home.workSpace.cardActivity.users.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

public class UsersAdapter extends ArrayAdapter<UserRole> {
    Context context;
    String boardId;
    FragmentManager fragmentManager;
    boolean isOwner;

    public UsersAdapter(@NonNull Context context, ArrayList<UserRole> users, String boardId, FragmentManager fragmentManager, boolean isOwner) {
        super(context, R.layout.workspace_activity_users_item, users);
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.boardId = boardId;
        this.isOwner = isOwner;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.workspace_activity_users_item, null);
        }

        UserRole userRole = getItem(position);
        User user = userRole.getUser();
        String role = userRole.getRole();

        ImageView image = convertView.findViewById(R.id.UsersActivityImage);
        Glide.with(context).load(user.getPhoto()).into(image);

        TextView name = convertView.findViewById(R.id.UsersActivityName);
        name.setText(user.getName());

        ConstraintLayout roleLayout = convertView.findViewById(R.id.UsersActivityRoleLayout);
        roleLayout.setOnClickListener(v -> {
            UsersSetRoleDialog dialog = UsersSetRoleDialog.newInstance(role, boardId, user.getId());
            dialog.show(fragmentManager, "setUserRole");
        });

        ImageButton delete = convertView.findViewById(R.id.UsersActivityDelete);
        delete.setOnClickListener(v -> {
            DeleteUserDialog dialog = DeleteUserDialog.newInstance(boardId, user.getId());
            dialog.show(fragmentManager, "deleteUser");
        });

        ImageView roleImage = convertView.findViewById(R.id.UsersActivityRoleImage);
        TextView roleText = convertView.findViewById(R.id.UsersActivityRoleText);

        if (Objects.equals(FirebaseAuth.getInstance().getUid(), user.getId())){
            name.setText(name.getText() + " (Вы)");
        }

        if (isOwner){
            if (role.equals("owner")){
                roleText.setText("Владелец");
                roleText.setTextColor(ContextCompat.getColor(context, R.color.black));
                roleLayout.setElevation(0);
                roleLayout.setClickable(false);
                roleImage.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
            } else if (role.equals("admin")){
                roleText.setText("Админ");
            } else {
                roleText.setText("Участник");
            }
        } else {
            if (role.equals("owner")){
                roleText.setText("Владелец");
            } else if (role.equals("admin")){
                roleText.setText("Админ");
            } else {
                roleText.setText("Участник");
            }
            roleText.setTextColor(ContextCompat.getColor(context, R.color.black));
            roleLayout.setElevation(0);
            roleLayout.setClickable(false);
            roleImage.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
        }

        return convertView;
    }
}