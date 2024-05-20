package com.example.teamdraft.ui.homeui.workSpace.dialogs.item;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.teamdraft.R;
import com.example.teamdraft.ui.homeui.workSpace.Item;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class AddItemDialog extends DialogFragment {
    private String boardId;
    private OnChange onChange;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onChange = (OnChange) context;
    }

    public static AddItemDialog newInstance(String boardId) {
        AddItemDialog dialog = new AddItemDialog();
        Bundle args = new Bundle();
        args.putString("boardId", boardId);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            boardId = getArguments().getString("boardId");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_onebutton, null);
        builder.setView(dialogView);

        TextView titleText = dialogView.findViewById(R.id.EditDialogTitle);
        EditText editText = dialogView.findViewById(R.id.EditDialogEditText);
        Button button = dialogView.findViewById(R.id.EditDialogButton);

        titleText.setText("Новый пункт");

        button.setOnClickListener(view -> {
            if (editText.getText().length() == 0) {
                editText.setError("Введите название");
            } else {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                String ID = UUID.randomUUID().toString();
                Item item = new Item(ID, editText.getText().toString());
                mDatabase.child("boards").child(boardId).child("items").child(ID).setValue(item);
                onChange.onChange();
                dismiss();
            }
        });

        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        }
        return null;
    }
}
