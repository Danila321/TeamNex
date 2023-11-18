package com.example.teamdraft.ui.homeui.boards;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.teamdraft.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class CreateBoardDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.board_create_dialog, null);
        builder.setView(dialogView);

        Button createButton = dialogView.findViewById(R.id.dialogCreateBoard);
        EditText boardName = dialogView.findViewById(R.id.boardNameEdit);

        createButton.setOnClickListener(view -> {
            if (boardName.getText().length() == 0) {
                boardName.setError("Введите название");
            } else {
                createBoard(boardName.getText().toString(), generateBoardCode());
            }
        });

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

    private void createBoard(String boardName, String boardCode) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersReference = database.getReference("users");

        String currentUserUid = firebaseAuth.getCurrentUser().getUid();

        // Генерируем уникальный ключ для новой доски
        String boardId = usersReference.child(currentUserUid).child("boards").push().getKey();
        if (boardId == null) {
            Toast.makeText(getContext(), "Ошибка при создании доски", Toast.LENGTH_SHORT).show();
            return;
        }

        // Создаем новую доску
        Board newBoard = new Board(boardName, boardCode);
        usersReference.child(currentUserUid).child("boards").child(boardId).setValue(newBoard);

        Toast.makeText(getContext(), "Доска успешно создана", Toast.LENGTH_SHORT).show();
    }

    private String generateBoardCode() {
        Random random = new Random();
        int randomNumber = 10000000 + random.nextInt(90000000);

        return String.valueOf(randomNumber);
    }
}
