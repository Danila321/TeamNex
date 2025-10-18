package com.myappteam.projectapp.ui.home.workSpace.settingsActivity;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.myappteam.projectapp.BaseActivity;
import com.myappteam.projectapp.GetUserRole;
import com.myappteam.projectapp.LoadingDialog;
import com.myappteam.projectapp.R;
import com.myappteam.projectapp.ui.home.Board;
import com.myappteam.projectapp.ui.home.workSpace.dialogs.settings.DeleteBoardDialog;
import com.myappteam.projectapp.ui.home.workSpace.dialogs.settings.DisconnectBoardDialog;
import com.myappteam.projectapp.ui.home.workSpace.dialogs.settings.EditBoardDialog;
import com.myappteam.projectapp.ui.home.workSpace.dialogs.settings.OnChangeBoard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SettingsActivity extends BaseActivity implements OnChangeBoard {
    ImageView boardImageView;
    String boardIdData, boardNameData;
    TextView boardNameText, boardOwnerText, boardCreateDateText, boardCodeText;
    private Uri selectedImageUri;
    private DatabaseReference mDatabase;
    private ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_settings);

        //Настраиваем кнопки выхода
        ImageButton backButton = findViewById(R.id.backButtonSettings);
        backButton.setOnClickListener(v -> {
            finish();
        });
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
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
            createDataListener(boardIdData);
            GetUserRole.getUserRole(boardIdData, this::roleManager);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mDatabase != null && listener != null) {
            mDatabase.addValueEventListener(listener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDatabase != null && listener != null) {
            mDatabase.removeEventListener(listener);
        }
    }

    void createDataListener(String boardId) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("boards").child(boardId);

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Board board = dataSnapshot.getValue(Board.class);
                    if (board != null) {
                        Glide.with(SettingsActivity.this).load(board.getImageUri()).into(boardImageView);
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

                        Button showQR = findViewById(R.id.SettingsShowQR);
                        showQR.setOnClickListener(v -> {
                            SettingsQRDialog settingsQRDialog = SettingsQRDialog.newInstance(board.getCode());
                            settingsQRDialog.show(getSupportFragmentManager(), "qr");
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
                            DatabaseReference ownerNameDatabaseReference = FirebaseDatabase.getInstance().getReference();
                            ownerNameDatabaseReference.child("users").child(creatorUserId).addListenerForSingleValueEvent(new ValueEventListener() {
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
        };
    }

    void roleManager(String role) {
        ConstraintLayout boardImageLayout = findViewById(R.id.boardImageLayout);
        TextView boardImageText = findViewById(R.id.boardImageText);
        ImageButton editBoardName = findViewById(R.id.editBoardName);
        Button deleteBoard = findViewById(R.id.settingsDeleteBoardButton);

        switch (role) {
            case "owner":
                //Включаем возможность изменения изображения
                boardImageText.setVisibility(View.VISIBLE);
                boardImageLayout.setOnClickListener(view -> openImagePicker());

                //Включаем возможность изменения названия
                editBoardName.setVisibility(View.VISIBLE);
                editBoardName.setOnClickListener(view -> {
                    EditBoardDialog dialog = EditBoardDialog.newInstance(boardNameData);
                    dialog.show(getSupportFragmentManager(), "editBoardName");
                });

                //Включаем возможность удаления
                deleteBoard.setVisibility(View.VISIBLE);
                deleteBoard.setOnClickListener(view -> {
                    DeleteBoardDialog dialog = DeleteBoardDialog.newInstance("доски", "доску");
                    dialog.show(getSupportFragmentManager(), "deleteBoard");
                });
                break;
            case "admin":
            case "user":
                //Включаем возможность отключения
                deleteBoard.setVisibility(View.VISIBLE);
                deleteBoard.setText("Отключиться от доски");
                deleteBoard.setOnClickListener(view -> {
                    DisconnectBoardDialog dialog = new DisconnectBoardDialog();
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
            //Показываем загрузочный диалог
            LoadingDialog dialog = new LoadingDialog(SettingsActivity.this, "Обновляем изображение...");
            dialog.startDialog();

            StorageReference imageRef = FirebaseStorage.getInstance().getReference("boards").child(boardIdData)
                    .child(boardIdData + "_board_image");

            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Получаем URL загруженного изображения
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Сохраняем URL в базе данных
                            FirebaseDatabase.getInstance().getReference("boards").child(boardIdData).child("imageUri").setValue(uri.toString());
                            //Закрываем загрузочный диалог
                            dialog.dismissDialog();
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
    }
}