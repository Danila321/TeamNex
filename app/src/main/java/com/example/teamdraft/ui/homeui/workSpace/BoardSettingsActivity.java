package com.example.teamdraft.ui.homeui.workSpace;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class BoardSettingsActivity extends AppCompatActivity {
    boolean starState = false;
    ImageView boardImage;
    String boardName;
    TextView boardNameText, boardOwner, boardCreateDate, boardEditDate;
    private Uri selectedImageUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_settings);

        boardImage = findViewById(R.id.boardImage);
        boardNameText = findViewById(R.id.SettingsBoardName);
        ImageView star = findViewById(R.id.boardStar);
        boardOwner = findViewById(R.id.SettingsOwner);
        boardCreateDate = findViewById(R.id.SettingsCreateDate);
        boardEditDate = findViewById(R.id.SettingsEditDate);

        star.setOnClickListener(view -> {
            star.setImageDrawable(null);
            if (starState) {
                star.setBackgroundResource(R.drawable.star);
            } else {
                star.setBackgroundResource(R.drawable.star_blue);
            }
            starState = !starState;
        });

        Bundle data = getIntent().getExtras();
        if (data != null) {
            boardName = data.getString("boardName");
            GetUserRole.getUserRole(boardName, this::roleManager);
            getData(boardName);
        }
    }

    void getData(String boardName) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("boards")
                .child(boardName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Board board = dataSnapshot.getValue(Board.class);
                            if (board != null) {
                                Picasso.get().load(board.getImageUri()).into(boardImage);
                                boardNameText.setText(board.getName());
                                boardCreateDate.setText(board.getDate());
                                boardEditDate.setText(board.getEditDate());

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
                                                    boardOwner.setText(userName);
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
        //Выключаем все дополнительные элементы
        boardImageText.setVisibility(View.GONE);
        editBoardName.setVisibility(View.INVISIBLE);
        deleteBoard.setVisibility(View.INVISIBLE);

        storageReference = FirebaseStorage.getInstance().getReference("board_images");
        databaseReference = FirebaseDatabase.getInstance().getReference("boards");

        switch (role) {
            case "owner":
                boardImageText.setVisibility(View.VISIBLE);
                editBoardName.setVisibility(View.VISIBLE);
                deleteBoard.setVisibility(View.VISIBLE);

                boardImageLayout.setOnClickListener(view -> openImagePicker());
                break;
            case "admin":
                boardImageText.setVisibility(View.VISIBLE);
                editBoardName.setVisibility(View.VISIBLE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            boardImage.setImageURI(selectedImageUri);

            // Загружаем изображение в Firebase Storage
            uploadImage();
        }
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
                        boardImage.setImageURI(selectedImageUri);

                        // Загружаем изображение в Firebase Storage
                        uploadImage();
                    }
                }
            });

    private void uploadImage() {
        if (selectedImageUri != null) {
            StorageReference imageRef = storageReference.child(boardName)
                    .child(System.currentTimeMillis() + "_board_image.jpg");

            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Получаем URL загруженного изображения
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Сохраняем URL в базе данных
                            databaseReference.child(boardName).child("imageUri").setValue(uri.toString());
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Обработка ошибок при загрузке изображения
                    });
        }
    }
}