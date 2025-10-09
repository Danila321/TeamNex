package com.myappteam.projectapp.ui.home.workSpace.dialogs.item;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.myappteam.projectapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeleteItemDialog extends DialogFragment {
    private String boardId, itemId;

    public static DeleteItemDialog newInstance(String boardId, String itemId) {
        DeleteItemDialog dialog = new DeleteItemDialog();
        Bundle args = new Bundle();
        args.putString("boardId", boardId);
        args.putString("itemId", itemId);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            boardId = getArguments().getString("boardId");
            itemId = getArguments().getString("itemId");
        }
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_text, null);
        builder.setView(dialogView);

        TextView title = dialogView.findViewById(R.id.TextDialogTitle);
        TextView description = dialogView.findViewById(R.id.TextDialogDescription);

        title.setText("Удаление пункта");
        description.setText("Вы уверены что хотите удалить\nпункт? Это действие является\nбезвозвратным!");

        Button buttonCancel = dialogView.findViewById(R.id.TextDialogButtonCancel);
        Button buttonDelete = dialogView.findViewById(R.id.TextDialogButtonDelete);

        buttonCancel.setOnClickListener(view -> dismiss());
        buttonDelete.setOnClickListener(view -> {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child("boards").child(boardId).child("items").child(itemId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    snapshot.getRef().removeValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            dismiss();
        });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int pixelsWidth = getResources().getDimensionPixelSize(R.dimen.dialog_text_width);
            getDialog().getWindow().setLayout(pixelsWidth, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }
}
