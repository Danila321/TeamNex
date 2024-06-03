package com.example.teamdraft.ui.home.workSpace.dialogs.card;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.teamdraft.R;
import com.example.teamdraft.ui.home.workSpace.Card;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class AddCardDialog extends DialogFragment {
    private String boardId, itemId;

    public static AddCardDialog newInstance(String boardId, String itemId) {
        AddCardDialog dialog = new AddCardDialog();
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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit, null);
        builder.setView(dialogView);

        TextView titleText = dialogView.findViewById(R.id.EditDialogTitle);
        EditText editText = dialogView.findViewById(R.id.EditDialogEditText);
        Button button = dialogView.findViewById(R.id.EditDialogButton);

        titleText.setText("Новая карточка");

        button.setOnClickListener(view -> {
            if (editText.getText().length() == 0) {
                editText.setError("Введите название");
            } else {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                String ID = UUID.randomUUID().toString();
                Card card = new Card(ID, editText.getText().toString(), "");
                mDatabase.child("boards").child(boardId).child("items").child(itemId).child("cards").child(ID).setValue(card);
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int pixelsWidth = getResources().getDimensionPixelSize(R.dimen.dialog_edit_width);
            getDialog().getWindow().setLayout(pixelsWidth, WindowManager.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
