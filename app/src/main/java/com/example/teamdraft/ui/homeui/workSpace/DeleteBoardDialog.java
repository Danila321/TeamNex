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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.teamdraft.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeleteBoardDialog extends DialogFragment {
    View dialogView;
    private String id;
    private OnDeleteBoard onDeleteBoard;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onDeleteBoard = (OnDeleteBoard) getActivity();
    }

    public static DeleteBoardDialog newInstance(String id) {
        DeleteBoardDialog dialog = new DeleteBoardDialog();
        Bundle args = new Bundle();
        args.putString("id", id);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString("id");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.settings_board_delete_dialog, null);
        builder.setView(dialogView);

        Button buttonCancel = dialogView.findViewById(R.id.BoardDialogButtonCancel);
        Button buttonDelete = dialogView.findViewById(R.id.BoardDialogButtonDelete);

        buttonCancel.setOnClickListener(view -> dismiss());
        buttonDelete.setOnClickListener(view -> {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child("boards").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    snapshot.getRef().removeValue();
                    onDeleteBoard.onDelete();
                    dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
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
