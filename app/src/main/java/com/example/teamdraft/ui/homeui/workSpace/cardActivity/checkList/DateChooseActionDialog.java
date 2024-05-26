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
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.example.teamdraft.R;
import com.example.teamdraft.ui.homeui.workSpace.cardActivity.OnChange;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DateChooseActionDialog extends DialogFragment {
    private String boardId, itemId, cardId, taskId, dateString;
    OnChange onChange;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onChange = (OnChange) context;
    }

    public static DateChooseActionDialog newInstance(String boardId, String itemId, String cardId, String taskId, String dateString) {
        DateChooseActionDialog dialog = new DateChooseActionDialog();
        Bundle args = new Bundle();
        args.putString("boardId", boardId);
        args.putString("itemId", itemId);
        args.putString("cardId", cardId);
        args.putString("taskId", taskId);
        args.putString("dateString", dateString);
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
            taskId = getArguments().getString("taskId");
            dateString = getArguments().getString("dateString");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.workspace_card_checklist_choose_dialog, null);
        builder.setView(dialogView);

        //Кнопка изменения даты
        ConstraintLayout change = dialogView.findViewById(R.id.checklistChooseChange);
        change.setOnClickListener(v -> {
            ChooseDateDialog dialog = ChooseDateDialog.newInstance(boardId, itemId, cardId, taskId, dateString);
            dialog.show(getParentFragmentManager(), "changeDialog");
            dismiss();
        });

        ConstraintLayout delete = dialogView.findViewById(R.id.checklistChooseDelete);
        delete.setOnClickListener(v -> {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child("boards").child(boardId).child("items").child(itemId).child("cards").child(cardId).child("checklist").child(taskId).child("date").setValue("");
            onChange.onChangeCheckList();
            dismiss();
        });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int pixelsWidth = getResources().getDimensionPixelSize(R.dimen.dialog_edit_width);
            getDialog().getWindow().setLayout(pixelsWidth, WindowManager.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
