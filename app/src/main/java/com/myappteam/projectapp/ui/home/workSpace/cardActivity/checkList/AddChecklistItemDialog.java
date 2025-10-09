package com.myappteam.projectapp.ui.home.workSpace.cardActivity.checkList;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.myappteam.projectapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class AddChecklistItemDialog extends DialogFragment {
    private String boardId, itemId, cardId;

    public static AddChecklistItemDialog newInstance(String boardId, String itemId, String cardId) {
        AddChecklistItemDialog dialog = new AddChecklistItemDialog();
        Bundle args = new Bundle();
        args.putString("boardId", boardId);
        args.putString("itemId", itemId);
        args.putString("cardId", cardId);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            boardId = getArguments().getString("boardId");
            itemId = getArguments().getString("itemId");
            cardId = getArguments().getString("cardId");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit, null);
        builder.setView(dialogView);

        TextView title = dialogView.findViewById(R.id.EditDialogTitle);
        TextInputLayout editTextLayout = dialogView.findViewById(R.id.EditDialogEditTextLayout);
        TextInputEditText editText = dialogView.findViewById(R.id.EditDialogEditText);
        Button button = dialogView.findViewById(R.id.EditDialogButton);
        ImageButton close = dialogView.findViewById(R.id.EditDialogClose);

        title.setText("Новая задача");

        close.setOnClickListener(v -> dismiss());

        button.setText("Добавить");
        button.setOnClickListener(view -> {
            String text = String.valueOf(editText.getText()).trim();
            if (text.isEmpty()) {
                editTextLayout.setError("Введите название");
            } else {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                String ID = UUID.randomUUID().toString();
                ItemChecklist item = new ItemChecklist(ID, false, text, "");
                mDatabase.child("boards").child(boardId).child("items").child(itemId).child("cards").child(cardId).child("checklist").child(ID).setValue(item);
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
        }
    }
}
