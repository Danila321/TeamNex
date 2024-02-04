package com.example.teamdraft.ui.homeui.workSpace;

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

public class EditBoardNameDialog extends DialogFragment {
    View dialogView;
    private String id, name;
    private OnEditNameBoard onEditNameBoard;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onEditNameBoard = (OnEditNameBoard) getActivity();
    }

    public static EditBoardNameDialog newInstance(String id, String name) {
        EditBoardNameDialog dialog = new EditBoardNameDialog();
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("name", name);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString("id");
            name = getArguments().getString("name");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.settings_board_editname_dialog, null);
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
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("boards").child(id).child("name").setValue(editName.getText().toString(), ((error, ref) -> {
                    if (error == null) {
                        dismiss();
                        onEditNameBoard.onEdit();
                    } else {

                    }
                }));
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
