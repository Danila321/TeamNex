package com.example.teamdraft.ui.homeui.workSpace;

import android.app.AlertDialog;
import android.app.Dialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddItemCardDialog extends DialogFragment {
    private String id, itemName;

    public static AddItemCardDialog newInstance(String id, String itemName) {
        AddItemCardDialog dialog = new AddItemCardDialog();
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("itemName", itemName);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString("id");
            itemName = getArguments().getString("itemName");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.board_create_dialog, null);
        builder.setView(dialogView);

        TextView titleText = dialogView.findViewById(R.id.BoardDialogTitle);
        EditText editText = dialogView.findViewById(R.id.BoardDialogEditText);
        Button button = dialogView.findViewById(R.id.BoardDIalogButton);

        if (itemName.equals("")){
            titleText.setText("Новый пункт");
        } else {
            titleText.setText("Новая карточка");
        }

        button.setOnClickListener(view -> {
            if (editText.getText().length() == 0) {
                editText.setError("Введите название");
            } else {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                if (itemName.equals("")){
                    mDatabase.child("boards").child(id).child("items").child(editText.getText().toString()).setValue("");
                } else {
                    Card card = new Card(editText.getText().toString());
                    mDatabase.child("boards").child(id).child("items").child(itemName).child(editText.getText().toString()).setValue(card);
                }
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
