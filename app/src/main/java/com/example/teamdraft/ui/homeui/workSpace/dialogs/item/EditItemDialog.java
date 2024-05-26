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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.teamdraft.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditItemDialog extends DialogFragment {
    private String boardId, itemId, name;
    private OnChangeItem onChangeItem;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onChangeItem = (OnChangeItem) context;
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
        View dialogView = inflater.inflate(R.layout.dialog_edit, null);
        builder.setView(dialogView);

        TextView title = dialogView.findViewById(R.id.EditDialogTitle);
        EditText editName = dialogView.findViewById(R.id.EditDialogEditText);
        ImageButton buttonCancel = dialogView.findViewById(R.id.EditDialogClose);
        Button buttonEdit = dialogView.findViewById(R.id.EditDialogButton);

        title.setText("Изменить название");

        editName.setText(name);

        buttonEdit.setText("Изменить");

        buttonCancel.setOnClickListener(view -> dismiss());
        buttonEdit.setOnClickListener(view -> {
            if (editName.getText().length() == 0) {
                editName.setError("Введите название");
            } else {
                //Обновляем данные в БД
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("boards").child(boardId).child("items").child(itemId).child("name").setValue(editName.getText().toString());
                onChangeItem.onChange();
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
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
