package com.example.teamdraft.ui.homeui.boards;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.teamdraft.R;
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
import java.util.Date;
import java.util.Random;

public class CreateConnectBoardDialog extends DialogFragment {
    View dialogView;
    private int type;

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
        dialogView = inflater.inflate(R.layout.board_create_dialog, null);
        builder.setView(dialogView);

        TextView titleText = dialogView.findViewById(R.id.BoardDialogTitle);
        EditText editText = dialogView.findViewById(R.id.BoardDialogEditText);
        Button button = dialogView.findViewById(R.id.BoardDIalogButton);

        if (type == 1) {
            titleText.setText("Подключение");
            editText.setHint("Введите код");
            button.setText("Подключиться");
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
            button.setOnClickListener(view -> {
                if (editText.getText().length() != 8) {
                    editText.setError("Введите 8-ми значный код");
                } else {
                    connectToBoard(editText.getText().toString());
                    dismiss();
                }
            });
        } else {
            titleText.setText("Новая доска");
            editText.setHint("Введите название");
            button.setText("Создать");
            button.setOnClickListener(view -> {
                if (editText.getText().length() == 0) {
                    editText.setError("Введите название");
                } else {
                    //Получаем текущие дату и время
                    @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    //Выгружаем из хранилища дефолтный фон для доски
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("default_board_images/background_1.jpg");
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        //Создаем доску
                        createBoard(editText.getText().toString(), uri.toString(), date, date, generateBoardCode());
                    }).addOnFailureListener(exception -> {
                        //Обработка ошибки
                        Toast.makeText(getContext(), "Произошла ошибка", Toast.LENGTH_SHORT).show();
                    });

                    dismiss();
                }
            });
        }

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

                                Toast.makeText(dialogView.getContext(), "Вы успешно подключились к доске", Toast.LENGTH_SHORT).show();
                            } else {
                                //Пользователь уже подключен к доске
                                Toast.makeText(dialogView.getContext(), "Вы уже подключены к данной доске", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        //Доска не найдена
                        Toast.makeText(dialogView.getContext(), "Доска с таким кодом не найдена", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(dialogView.getContext(), "Ошибка при подключении пользователя к доске", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void createBoard(String boardName, String imageUrl, String boardDate, String boardEditDate, String boardCode) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        // Создание узла доски с информацией о доске
        Board board = new Board(boardName, imageUrl, boardDate, boardEditDate, boardCode);
        mDatabase.child("boards").child(boardName).setValue(board);

        // Добавление создателя доски к списку пользователей
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child("boards").child(boardName).child("users").child(userId).setValue("owner");
    }

    private String generateBoardCode() {
        Random random = new Random();
        int randomNumber = 10000000 + random.nextInt(90000000);

        return String.valueOf(randomNumber);
    }
}
