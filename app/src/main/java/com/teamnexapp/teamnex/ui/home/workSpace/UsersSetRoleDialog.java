package com.teamnexapp.teamnex.ui.home.workSpace;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.teamnexapp.teamnex.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UsersSetRoleDialog extends DialogFragment {
    private String role, boardId, userId;

    public static UsersSetRoleDialog newInstance(String role, String boardId, String userId) {
        UsersSetRoleDialog dialog = new UsersSetRoleDialog();
        Bundle args = new Bundle();
        args.putString("role", role);
        args.putString("boardId", boardId);
        args.putString("userId", userId);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            role = getArguments().getString("role");
            boardId = getArguments().getString("boardId");
            userId = getArguments().getString("userId");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.workspace_activity_users_dialog, null);
        builder.setView(dialogView);

        ImageButton close = dialogView.findViewById(R.id.UsersRoleDialogClose);
        ConstraintLayout constraintLayoutAdmin = dialogView.findViewById(R.id.constraintLayoutAdmin);
        ConstraintLayout constraintLayoutUser = dialogView.findViewById(R.id.constraintLayoutUser);
        RadioButton radioButtonAdmin = dialogView.findViewById(R.id.radioButtonAdmin);
        RadioButton radioButtonUser = dialogView.findViewById(R.id.radioButtonUser);

        close.setOnClickListener(v -> dismiss());

        if (role.contains("admin")) {
            radioButtonAdmin.setChecked(true);
            constraintLayoutAdmin.setBackgroundResource(R.drawable.corner_green);
        } else {
            radioButtonUser.setChecked(true);
            constraintLayoutUser.setBackgroundResource(R.drawable.corner_green);
        }

        constraintLayoutAdmin.setOnClickListener(v -> {
            radioButtonAdmin.setChecked(true);
            radioButtonUser.setChecked(false);
            constraintLayoutAdmin.setBackgroundResource(R.drawable.corner_green);
            constraintLayoutUser.setBackgroundResource(R.drawable.corner_gray);
            changeRole("admin");
        });

        constraintLayoutUser.setOnClickListener(v -> {
            radioButtonAdmin.setChecked(false);
            radioButtonUser.setChecked(true);
            constraintLayoutAdmin.setBackgroundResource(R.drawable.corner_gray);
            constraintLayoutUser.setBackgroundResource(R.drawable.corner_green);
            changeRole("user");
        });

        radioButtonAdmin.setOnClickListener(v -> {
            radioButtonAdmin.setChecked(true);
            radioButtonUser.setChecked(false);
            constraintLayoutAdmin.setBackgroundResource(R.drawable.corner_green);
            constraintLayoutUser.setBackgroundResource(R.drawable.corner_gray);
            changeRole("admin");
        });

        radioButtonUser.setOnClickListener(v -> {
            radioButtonAdmin.setChecked(false);
            radioButtonUser.setChecked(true);
            constraintLayoutAdmin.setBackgroundResource(R.drawable.corner_gray);
            constraintLayoutUser.setBackgroundResource(R.drawable.corner_green);
            changeRole("user");
        });

        return builder.create();
    }

    void changeRole(String role) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("boards").child(boardId).child("users").child(userId).setValue(role);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int pixelsWidth = getResources().getDimensionPixelSize(R.dimen.dialog_role_width);
            getDialog().getWindow().setLayout(pixelsWidth, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }
}
