package com.myappteam.projectapp.ui.home.workSpace.dialogs.settings;

import android.app.Dialog;
import android.content.Context;
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

public class EditBoardDialog extends DialogFragment {
    private String name;
    private OnChangeBoard onChangeBoard;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onChangeBoard = (OnChangeBoard) context;
    }

    public static EditBoardDialog newInstance(String name) {
        EditBoardDialog dialog = new EditBoardDialog();
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
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit, null);
        builder.setView(dialogView);

        TextView title = dialogView.findViewById(R.id.EditDialogTitle);
        TextInputLayout editTextLayout = dialogView.findViewById(R.id.EditDialogEditTextLayout);
        TextInputEditText editText = dialogView.findViewById(R.id.EditDialogEditText);
        ImageButton buttonCancel = dialogView.findViewById(R.id.EditDialogClose);
        Button buttonEdit = dialogView.findViewById(R.id.EditDialogButton);

        title.setText(getString(R.string.board_settings_edit_name_title));

        editText.setText(name);

        buttonEdit.setText(getString(R.string.board_settings_edit_name_button));

        buttonCancel.setOnClickListener(view -> dismiss());
        buttonEdit.setOnClickListener(view -> {
            String text = String.valueOf(editText.getText()).trim();
            if (text.isEmpty()) {
                editTextLayout.setError(getString(R.string.board_settings_edit_name_error));
            } else {
                onChangeBoard.onEdit(text);
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
