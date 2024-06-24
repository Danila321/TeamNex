package com.teamnexapp.teamnex.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.teamnexapp.teamnex.LoadingDialog;
import com.teamnexapp.teamnex.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class CreateConnectBoardDialog extends DialogFragment {
    View dialogView;
    private int type;
    OnCreateConnectBoard onCreateConnectBoard;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onCreateConnectBoard = (OnCreateConnectBoard) getParentFragment();
        System.out.println(onCreateConnectBoard);
    }

    public static CreateConnectBoardDialog newInstance(int type) {
        CreateConnectBoardDialog dialog = new CreateConnectBoardDialog();
        Bundle args = new Bundle();
        args.putInt("type", type);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("type");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_edit, null);
        builder.setView(dialogView);

        ImageButton close = dialogView.findViewById(R.id.EditDialogClose);
        close.setOnClickListener(v -> dismiss());

        TextView titleText = dialogView.findViewById(R.id.EditDialogTitle);
        EditText editText = dialogView.findViewById(R.id.EditDialogEditText);
        Button button = dialogView.findViewById(R.id.EditDialogButton);

        if (type == 1) {
            titleText.setText(R.string.board_dialog_connect_title);
            editText.setHint(R.string.board_dialog_connect_hint);
            button.setText(R.string.board_dialog_connect_text);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
            button.setOnClickListener(view -> {
                if (editText.getText().length() != 8) {
                    editText.setError(getString(R.string.board_dialog_connect_error));
                } else {
                    connectToBoard(editText.getText().toString());
                    dismiss();
                }
            });
        } else {
            titleText.setText(R.string.board_dialog_create_title);
            editText.setHint(R.string.board_dialog_create_hint);
            button.setText(R.string.board_dialog_create_text);
            button.setOnClickListener(view -> {
                if (editText.getText().length() == 0) {
                    editText.setError(getString(R.string.board_dialog_create_error));
                } else {
                    //Получаем текущие дату и время
                    @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    //Создаем доску
                    createBoard(editText.getText().toString(), date, date);

                    dismiss();
                }
            });
        }

        return builder.create();
    }

    private void connectToBoard(String boardCode) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            mDatabase.child("boards").orderByChild("code").equalTo(boardCode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Доска существует
                        for (DataSnapshot boardSnapshot : dataSnapshot.getChildren()) {
                            // Проверяем, не подключен ли уже текущий пользователь
                            if (!boardSnapshot.child("users").hasChild(currentUser.getUid())) {
                                // Добавляем пользователя к списку пользователей доски
                                mDatabase.child("boards").child(boardSnapshot.getKey()).child("users").child(currentUser.getUid()).setValue("user");

                                onCreateConnectBoard.onChange();
                                Toast.makeText(dialogView.getContext(), R.string.board_dialog_connect_complete, Toast.LENGTH_SHORT).show();
                            } else {
                                //Пользователь уже подключен к доске
                                Toast.makeText(dialogView.getContext(), R.string.board_dialog_connect_error_already, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        //Доска не найдена
                        Toast.makeText(dialogView.getContext(), R.string.board_dialog_connect_error_not_found, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(dialogView.getContext(), R.string.board_dialog_connect_error_unknown, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void createBoard(String boardName, String boardDate, String boardEditDate) {
        //Показываем загрузочный диалог
        LoadingDialog loadingDialog = new LoadingDialog(getActivity(), getString(R.string.board_dialog_create_loading));
        loadingDialog.startDialog();
        FirebaseDatabase.getInstance().getReference().child("boards").orderByChild("code").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Получаем коды всех досок
                ArrayList<String> codes = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    codes.add(dataSnapshot.getValue(Board.class).getCode());
                }

                //Создаем ID доски
                String ID = UUID.randomUUID().toString();

                //Добавляем дефолтное изображение доски
                Uri uri = Uri.parse("android.resource://com.teamnexapp.teamnex/" + R.drawable.background);
                StorageReference storageReference = FirebaseStorage.getInstance().getReference("boards").child(ID).child(ID + "_board_image");
                storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                    // Получаем URL загруженного изображения
                    storageReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                        //Создаем уникальный код доски
                        int boardCode;
                        do {
                            boardCode = 10000000 + new Random().nextInt(90000000);
                        } while (codes.contains(String.valueOf(boardCode)));

                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                        // Создание узла доски с информацией о доске
                        Board board = new Board(ID, boardName, uri1.toString(), boardDate, boardEditDate, String.valueOf(boardCode));
                        mDatabase.child("boards").child(ID).setValue(board);

                        // Добавление создателя доски к списку пользователей
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        mDatabase.child("boards").child(ID).child("users").child(userId).setValue("owner");

                        loadingDialog.dismissDialog();

                        onCreateConnectBoard.onChange();
                    });
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
