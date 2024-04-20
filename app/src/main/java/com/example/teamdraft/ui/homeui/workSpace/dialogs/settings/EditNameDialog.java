package com.example.teamdraft.ui.homeui.workSpace.dialogs.settings;

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

public class EditNameDialog extends DialogFragment {
    private String name;
    private OnEditName onEditName;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onEditName = (OnEditName) context;
    }

    public static EditNameDialog newInstance(String name) {
        EditNameDialog dialog = new EditNameDialog();
        Bundle args = new Bundle();
        args.putString("name", name);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString("name");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.settings_board_editname_dialog, null);
        builder.setView(dialogView);

        EditText editName = dialogView.findViewById(R.id.BoardDialogEditName);
        Button buttonCancel = dialogView.findViewById(R.id.BoardDialogButtonCancel);
        Button buttonEdit = dialogView.findViewById(R.id.BoardDialogButtonEdit);

        editName.setText(name);

        buttonCancel.setOnClickListener(view -> dismiss());
        buttonEdit.setOnClickListener(view -> {
            if (editName.getText().length() == 0) {
                editName.setError("Введите название");
            } else {
                onEditName.onEdit(editName.getText().toString());
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
