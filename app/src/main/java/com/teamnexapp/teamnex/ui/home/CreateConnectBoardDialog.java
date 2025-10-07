package com.teamnexapp.teamnex.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        if (type == 1) {
            dialogView = inflater.inflate(R.layout.dialog_connect, null);
        } else {
            dialogView = inflater.inflate(R.layout.dialog_edit, null);
        }
        builder.setView(dialogView);

        ImageButton close = dialogView.findViewById(R.id.EditDialogClose);
        close.setOnClickListener(v -> dismiss());

        TextView titleText = dialogView.findViewById(R.id.EditDialogTitle);
        TextInputLayout editTextLayout = dialogView.findViewById(R.id.EditDialogEditTextLayout);
        TextInputEditText editText = dialogView.findViewById(R.id.EditDialogEditText);
        Button button = dialogView.findViewById(R.id.EditDialogButton);

        if (type == 1) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
            button.setOnClickListener(view -> {
                String code = String.valueOf(editText.getText()).trim();
                if (code.length() != 8) {
                    editTextLayout.setError(getString(R.string.board_dialog_connect_error));
                } else {
                    connectToBoard(code);
                    dismiss();
                }
            });
            ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                connectToBoard(data.getStringExtra("code"));
                                dismiss();
                            }
                        }
                    });
            Button scan = dialogView.findViewById(R.id.buttonQR);
            scan.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), QRCodeScannerActivity.class);
                activityResultLauncher.launch(intent);
            });
        } else {
            titleText.setText(R.string.board_dialog_create_title);
            editTextLayout.setHint(R.string.board_dialog_create_hint);
            button.setText(R.string.board_dialog_create_text);
            button.setOnClickListener(view -> {
                String name = String.valueOf(editText.getText()).trim();
                if (name.isEmpty()) {
                    editTextLayout.setError(getString(R.string.board_dialog_create_error));
                } else {
                    //Получаем текущие дату и время
                    @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    //Создаем доску
                    createBoard(name, date, date);

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
                                mDatabase.child("boards").child(boardSnapshot.getKey()).child("users").child(currentUser.getUid()).setValue("user").addOnCompleteListener(task -> {
                                    onCreateConnectBoard.onChange();
                                    Toast.makeText(dialogView.getContext(), R.string.board_dialog_connect_complete, Toast.LENGTH_SHORT).show();
                                });
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
                        mDatabase.child("boards").child(ID).setValue(board).addOnCompleteListener(task -> {
                            onCreateConnectBoard.onChange();
                            Toast.makeText(dialogView.getContext(), "Доска успешно создана", Toast.LENGTH_SHORT).show();
                        });

                        // Добавление создателя доски к списку пользователей
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        mDatabase.child("boards").child(ID).child("users").child(userId).setValue("owner");

                        loadingDialog.dismissDialog();
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
        }
    }
}
