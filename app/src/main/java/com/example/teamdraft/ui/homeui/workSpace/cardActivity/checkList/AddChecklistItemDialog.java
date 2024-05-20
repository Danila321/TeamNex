package com.example.teamdraft.ui.homeui.workSpace.cardActivity.checkList;

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
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.teamdraft.R;
import com.example.teamdraft.ui.homeui.workSpace.Card;
import com.example.teamdraft.ui.homeui.workSpace.cardActivity.OnChange;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class AddChecklistItemDialog extends DialogFragment {
    private String boardId, itemId, cardId;
    OnChange onChange;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onChange = (OnChange) context;
    }

    public static AddChecklistItemDialog newInstance(String boardId, String itemId, String cardId) {
        AddChecklistItemDialog dialog = new AddChecklistItemDialog();
        Bundle args = new Bundle();
        args.putString("boardId", boardId);
        args.putString("itemId", itemId);
        args.putString("cardId", cardId);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            boardId = getArguments().getString("boardId");
            itemId = getArguments().getString("itemId");
            cardId = getArguments().getString("cardId");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.workspace_card_checklist_dialog, null);
        builder.setView(dialogView);

        EditText editText = dialogView.findViewById(R.id.CheckListDialogEditText);
        Button button = dialogView.findViewById(R.id.ChecklistDialogButton);
        ImageButton close = dialogView.findViewById(R.id.CheckListDialogClose);

        button.setOnClickListener(view -> {
            if (editText.getText().length() == 0) {
                editText.setError("Введите название");
            } else {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                String ID = UUID.randomUUID().toString();
                ItemChecklist item = new ItemChecklist(ID, false, editText.getText().toString(), "");
                mDatabase.child("boards").child(boardId).child("items").child(itemId).child("cards").child(cardId).child("checklist").child(ID).setValue(item);
                onChange.onChangeCheckList();
                dismiss();
            }
        });

        close.setOnClickListener(v -> dismiss());

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
