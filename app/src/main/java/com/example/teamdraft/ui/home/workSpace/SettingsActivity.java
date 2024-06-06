package com.example.teamdraft.ui.home.workSpace;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teamdraft.GetUserRole;
import com.example.teamdraft.R;
import com.example.teamdraft.ui.home.Board;
import com.example.teamdraft.ui.home.workSpace.dialogs.settings.DeleteDialog;
import com.example.teamdraft.ui.home.workSpace.dialogs.settings.DisconnectDialog;
import com.example.teamdraft.ui.home.workSpace.dialogs.settings.EditNameDialog;
import com.example.teamdraft.ui.home.workSpace.dialogs.settings.OnChangeBoard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class SettingsActivity extends AppCompatActivity implements OnChangeBoard {
    ImageView boardImageView;
    String boardIdData, boardNameData;
    TextView boardNameText, boardOwnerText, boardCreateDateText, boardCodeText;
    private Uri selectedImageUri;
    boolean dataChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_settings);

        //Настраиваем кнопки выхода
        ImageButton backButton = findViewById(R.id.backButtonSettings);
        backButton.setOnClickListener(v -> {
            Intent result = new Intent();
            result.putExtra("dataChanged", dataChanged);
            setResult(Activity.RESULT_OK, result);
            finish();
        });
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent result = new Intent();
                result.putExtra("dataChanged", dataChanged);
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        });

        Bundle data = getIntent().getExtras();
        if (data != null) {
            boardImageView = findViewById(R.id.boardImage);
            boardNameText = findViewById(R.id.SettingsBoardName);
            boardOwnerText = findViewById(R.id.SettingsOwner);
            boardCreateDateText = findViewById(R.id.SettingsCreateDate);
            boardCodeText = findViewById(R.id.SettingsBoardCode);

            boardIdData = data.getString("boardId");
            getData(boardIdData);
            GetUserRole.getUserRole(boardIdData, this::roleManager);
        }
    }

    void getData(String boardId) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("boards")
                .child(boardId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Board board = dataSnapshot.getValue(Board.class);
                            if (board != null) {
                                Picasso.get().load(board.getImageUri()).into(boardImageView);
                                boardNameData = board.getName();
                                boardNameText.setText(board.getName());
                                boardCreateDateText.setText(board.getDate());
                                boardCodeText.setText(board.getCode());

                                //Кнопка копирования кода в буфер обмена
                                ImageButton copyCode = findViewById(R.id.SettingsImageButtonCopyCode);
                                copyCode.setOnClickListener(v -> {
                                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("code", board.getCode());
                                    clipboard.setPrimaryClip(clip);
                                    Toast.makeText(SettingsActivity.this, R.string.board_settings_code_text, Toast.LENGTH_SHORT).show();
                                });

                                // Получаем UID создателя доски
                                String creatorUserId = null;
                                for (DataSnapshot userSnapshot : dataSnapshot.child("users").getChildren()) {
                                    String userRole = userSnapshot.getValue(String.class);
                                    if ("owner".equals(userRole)) {
                                        creatorUserId = userSnapshot.getKey();
                                        break;
                                    }
                                }

                                // Теперь делаем запрос для получения имени создателя
                                if (creatorUserId != null) {
                                    mDatabase.child("users").child(creatorUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                            if (userSnapshot.exists()) {
                                                String userName = userSnapshot.child("name").getValue(String.class);

                                                if (userName != null) {
                                                    boardOwnerText.setText(userName);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Обработка ошибок
                                        }
                                    });
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Обработка ошибок
                    }
                });
    }

    void roleManager(String role) {
        ConstraintLayout boardImageLayout = findViewById(R.id.boardImageLayout);
        TextView boardImageText = findViewById(R.id.boardImageText);
        ImageButton editBoardName = findViewById(R.id.editBoardName);
        ConstraintLayout actionLayout = findViewById(R.id.SettingsActionLayout);
        ImageView actionImage = findViewById(R.id.SettingsActionImage);
        TextView actionText = findViewById(R.id.SettingsActionText);

        switch (role) {
            case "owner":
                //Включаем возможность изменения изображения
                boardImageText.setVisibility(View.VISIBLE);
                boardImageLayout.setOnClickListener(view -> openImagePicker());

                //Включаем возможность изменения названия
                editBoardName.setVisibility(View.VISIBLE);
                editBoardName.setOnClickListener(view -> {
                    EditNameDialog dialog = EditNameDialog.newInstance(boardNameData);
                    dialog.show(getSupportFragmentManager(), "editBoardName");
                });

                //Включаем возможность удаления
                actionLayout.setVisibility(View.VISIBLE);
                actionLayout.setOnClickListener(view -> {
                    DeleteDialog dialog = DeleteDialog.newInstance("доски", "доску");
                    dialog.show(getSupportFragmentManager(), "deleteBoard");
                });
                break;
            case "admin":
            case "user":
                //Включаем возможность отключения
                actionLayout.setVisibility(View.VISIBLE);
                Picasso.get().load(R.drawable.disconnect).into(actionImage);
                actionText.setText("Отключиться от доски");
                actionLayout.setOnClickListener(view -> {
                    DisconnectDialog dialog = new DisconnectDialog();
                    dialog.show(getSupportFragmentManager(), "disconnect");
                });
                break;
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, getString(R.string.board_settings_choose_image)));
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        selectedImageUri = data.getData();
                        boardImageView.setImageURI(selectedImageUri);

                        // Загружаем изображение в Firebase Storage
                        uploadImage();
                    }
                }
            });

    private void uploadImage() {
        if (selectedImageUri != null) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReference("boards").child(boardIdData)
                    .child(boardIdData + "_board_image");

            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Получаем URL загруженного изображения
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Сохраняем URL в базе данных
                            FirebaseDatabase.getInstance().getReference("boards").child(boardIdData).child("imageUri").setValue(uri.toString());
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Обработка ошибок при загрузке изображения
                    });
        }
    }

    @Override
    public void onDelete() {
        //Обновляем данные в БД
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("boards").child(boardIdData).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();

                Intent result = new Intent();
                result.putExtra("dataChanged", true);
                setResult(Activity.RESULT_OK, result);
                //Закрываем Activity
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDisconnect() {
        //Обновляем данные в БД
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("boards").child(boardIdData).child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();

                Intent result = new Intent();
                result.putExtra("dataChanged", true);
                setResult(Activity.RESULT_OK, result);
                //Закрываем Activity
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onEdit(String name) {
        //Обновляем данные в БД
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("boards").child(boardIdData).child("name").setValue(name);
        //Устанавливаем флаг для обновления списка досок
        dataChanged = true;
    }
}