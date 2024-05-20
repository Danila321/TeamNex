package com.example.teamdraft.ui.homeui.workSpace.dialogs.settings;

import android.annotation.SuppressLint;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.teamdraft.R;

public class DeleteDialog extends DialogFragment {
    private String titleText, descriptionText;
    private OnDelete onDelete;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onDelete = (OnDelete) context;
    }

    public static DeleteDialog newInstance(String titleText, String descriptionText) {
        DeleteDialog dialog = new DeleteDialog();
        Bundle args = new Bundle();
        args.putString("titleText", titleText);
        args.putString("descriptionText", descriptionText);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            titleText = getArguments().getString("titleText");
            descriptionText = getArguments().getString("descriptionText");
        }
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_text, null);
        builder.setView(dialogView);

        TextView title = dialogView.findViewById(R.id.TextDialogTitle);
        TextView description = dialogView.findViewById(R.id.TextDialogDescription);

        title.setText("Удаление " + titleText);
        description.setText("Вы уверены что хотите удалить\n" + descriptionText + "? Это действие является\nбезвозвратным!");

        Button buttonCancel = dialogView.findViewById(R.id.TextDialogButtonCancel);
        Button buttonDelete = dialogView.findViewById(R.id.TextDialogButtonDelete);

        buttonCancel.setOnClickListener(view -> dismiss());
        buttonDelete.setOnClickListener(view -> {
            onDelete.onDelete();
            dismiss();
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
