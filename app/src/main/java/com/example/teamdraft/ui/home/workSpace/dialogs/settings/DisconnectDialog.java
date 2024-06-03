package com.example.teamdraft.ui.home.workSpace.dialogs.settings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.teamdraft.R;

public class DisconnectDialog extends DialogFragment {
    private OnChangeBoard onChangeBoard;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onChangeBoard = (OnChangeBoard) context;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_disconnect, null);
        builder.setView(dialogView);

        Button buttonCancel = dialogView.findViewById(R.id.DisconnectDialogButtonCancel);
        Button buttonDisconnect = dialogView.findViewById(R.id.DisconnectDialogButtonAccept);

        buttonCancel.setOnClickListener(view -> dismiss());
        buttonDisconnect.setOnClickListener(view -> {
            onChangeBoard.onDisconnect();
            dismiss();
        });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int pixelsWidth = getResources().getDimensionPixelSize(R.dimen.dialog_disconnect_width);
            getDialog().getWindow().setLayout(pixelsWidth, WindowManager.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
