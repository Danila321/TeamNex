package com.teamnexapp.teamnex.ui.home.workSpace.dialogs.item;

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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.teamnexapp.teamnex.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditItemDialog extends DialogFragment {
    private String boardId, itemId, name;

    public static EditItemDialog newInstance(String boardId, String itemId, String name) {
        EditItemDialog dialog = new EditItemDialog();
        Bundle args = new Bundle();
        args.putString("boardId", boardId);
        args.putString("itemId", itemId);
        args.putString("name", name);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            boardId = getArguments().getString("boardId");
            itemId = getArguments().getString("itemId");
            name = getArguments().getString("name");
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
        ImageButton buttonCancel = dialogView.findViewById(R.id.EditDialogClose);
        Button buttonEdit = dialogView.findViewById(R.id.EditDialogButton);

        title.setText("Изменить название");

        editText.setText(name);

        buttonEdit.setText("Изменить");

        buttonCancel.setOnClickListener(view -> dismiss());
        buttonEdit.setOnClickListener(view -> {
            String text = String.valueOf(editText.getText()).trim();
            if (text.isEmpty()) {
                editTextLayout.setError("Введите название");
            } else {
                //Обновляем данные в БД
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("boards").child(boardId).child("items").child(itemId).child("name").setValue(text);
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
