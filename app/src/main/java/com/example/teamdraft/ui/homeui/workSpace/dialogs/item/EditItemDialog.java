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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.teamdraft.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditItemDialog extends DialogFragment {
    private String boardId, itemId, name;
    private OnChange onChange;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onChange = (OnChange) context;
    }

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_twobutton, null);
        builder.setView(dialogView);

        EditText editName = dialogView.findViewById(R.id.EditDialog2EditText);
        Button buttonCancel = dialogView.findViewById(R.id.EditDialog2ButtonCancel);
        Button buttonEdit = dialogView.findViewById(R.id.EditDialog2ButtonEdit);

        editName.setText(name);

        buttonCancel.setOnClickListener(view -> dismiss());
        buttonEdit.setOnClickListener(view -> {
            if (editName.getText().length() == 0) {
                editName.setError("Введите название");
            } else {
                //Обновляем данные в БД
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("boards").child(boardId).child("items").child(itemId).child("name").setValue(editName.getText().toString());
                onChange.onChange();
                dismiss();
            }
        });
        return builder.create();
    }

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
