package com.example.teamdraft.ui.home.workSpace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.example.teamdraft.R;
import com.example.teamdraft.ui.home.workSpace.cardActivity.users.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapter extends ArrayAdapter<UserRole> {
    Context context;
    String boardId;
    FragmentManager fragmentManager;

    public UsersAdapter(@NonNull Context context, ArrayList<UserRole> users, String boardId, FragmentManager fragmentManager) {
        super(context, R.layout.workspace_activity_users_item, users);
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.boardId = boardId;
    }

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
        Picasso.get().load(user.getPhoto()).into(image);

        TextView name = convertView.findViewById(R.id.UsersActivityName);
        name.setText(user.getName());

        ConstraintLayout roleLayout = convertView.findViewById(R.id.UsersActivityRoleLayout);
        roleLayout.setOnClickListener(v -> {
            UsersSetRoleDialog dialog = UsersSetRoleDialog.newInstance(role, boardId, user.getId());
            dialog.show(fragmentManager, "setUserRole");
        });

        ImageView roleImage = convertView.findViewById(R.id.UsersActivityRoleImage);
        TextView roleText = convertView.findViewById(R.id.UsersActivityRoleText);
        if (role.equals("owner")){
            roleText.setText("Владелец");
            roleText.setTextColor(ContextCompat.getColor(context, R.color.black));
            roleLayout.setElevation(0);
            roleImage.setVisibility(View.GONE);
        } else if (role.equals("admin")){
            roleText.setText("Администратор");
        } else {
            roleText.setText("Участник");
        }

        return convertView;
    }
}