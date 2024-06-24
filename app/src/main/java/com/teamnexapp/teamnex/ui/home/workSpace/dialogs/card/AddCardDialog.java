package com.teamnexapp.teamnex.ui.home.workSpace.dialogs.card;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.teamnexapp.teamnex.R;
import com.teamnexapp.teamnex.ui.home.workSpace.Card;
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
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit, null);
        builder.setView(dialogView);

        ImageButton close = dialogView.findViewById(R.id.EditDialogClose);
        TextView titleText = dialogView.findViewById(R.id.EditDialogTitle);
        TextInputLayout editText = dialogView.findViewById(R.id.EditDialogEditText);
        Button button = dialogView.findViewById(R.id.EditDialogButton);

        close.setOnClickListener(v -> dismiss());

        titleText.setText("Новая карточка");

        button.setOnClickListener(view -> {
            if (editText.getEditText().length() == 0) {
                editText.setError("Введите название");
            } else {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                String ID = UUID.randomUUID().toString();
                Card card = new Card(ID, editText.getEditText().toString(), "");
                mDatabase.child("boards").child(boardId).child("items").child(itemId).child("cards").child(ID).setValue(card);
                dismiss();
            }
        });

        return builder.create();
    }
}
