package com.example.teamdraft.ui.homeui.workSpace;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.teamdraft.GetUserRole;
import com.example.teamdraft.R;
import com.example.teamdraft.ui.homeui.boards.Board;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class BoardSettingsActivity extends AppCompatActivity implements OnDeleteBoard, OnEditNameBoard {
    boolean starState = false;
    ImageView boardImageView;
    String boardIdData, boardNameData;
    TextView boardNameText, boardOwnerText, boardCreateDateText, boardEditDateText;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_settings);

        ImageButton backButton = findViewById(R.id.backButtonSettings);
        backButton.setOnClickListener(view -> finish());

        Bundle data = getIntent().getExtras();
        if (data != null) {
            boardImageView = findViewById(R.id.boardImage);
            boardNameText = findViewById(R.id.SettingsBoardName);
            boardOwnerText = findViewById(R.id.SettingsOwner);
            boardCreateDateText = findViewById(R.id.SettingsCreateDate);
            boardEditDateText = findViewById(R.id.SettingsEditDate);

            starBoard();

            boardIdData = data.getString("boardId");
            getData(boardIdData);
            GetUserRole.getUserRole(boardIdData, this::roleManager);
        }
    }

    void getData(String boardId) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("boards")
                .child(boardId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Board board = dataSnapshot.getValue(Board.class);
                            if (board != null) {
                                Picasso.get().load(board.getImageUri()).into(boardImageView);
                                boardNameData = board.getName();
                                boardNameText.setText(board.getName());
                                boardCreateDateText.setText(board.getDate());
                                boardEditDateText.setText(board.getEditDate());

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
        ConstraintLayout deleteBoard = findViewById(R.id.deleteBoard);

        switch (role) {
            case "owner":
                //Включаем возможность изменения изображения
                boardImageText.setVisibility(View.VISIBLE);
                boardImageLayout.setOnClickListener(view -> openImagePicker());

                //Включаем возможность изменения названия
                editBoardName.setVisibility(View.VISIBLE);
                editBoardName.setOnClickListener(view -> {
                    EditBoardNameDialog dialog = EditBoardNameDialog.newInstance(boardIdData, boardNameData);
                    dialog.show(getSupportFragmentManager(), "editName");
                });

                //Включаем возможность удаления
                deleteBoard.setVisibility(View.VISIBLE);
                deleteBoard.setOnClickListener(view -> {
                    DeleteBoardDialog dialog = DeleteBoardDialog.newInstance(boardIdData);
                    dialog.show(getSupportFragmentManager(), "deleteBoard");
                });
                break;
            case "admin":
                //Включаем возможность изменения изображения
                boardImageText.setVisibility(View.VISIBLE);
                boardImageLayout.setOnClickListener(view -> openImagePicker());

                //Включаем возможность изменения названия
                editBoardName.setVisibility(View.VISIBLE);
                editBoardName.setOnClickListener(view -> {
                    EditBoardNameDialog dialog = EditBoardNameDialog.newInstance(boardIdData, boardNameData);
                    dialog.show(getSupportFragmentManager(), "editName");
                });
                break;
            case "user":
                break;
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "Выберите изображение"));
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        // There are no request codes
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
            StorageReference imageRef = FirebaseStorage.getInstance().getReference("board_images").child(boardIdData)
                    .child(System.currentTimeMillis() + "_board_image.jpg");

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

    void starBoard() {
        ImageView star = findViewById(R.id.boardStar);
        star.setOnClickListener(view -> {
            star.setImageDrawable(null);
            if (starState) {
                star.setBackgroundResource(R.drawable.star);
            } else {
                star.setBackgroundResource(R.drawable.star_blue);
            }
            starState = !starState;
        });
    }

    @Override
    public void onDelete() {
        finish();
    }

    @Override
    public void onEdit() {
        startActivity(getIntent());
        finish();
        overridePendingTransition(0, 0);
    }
}