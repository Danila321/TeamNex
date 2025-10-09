package com.myappteam.projectapp.profile.imageSettings;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.myappteam.projectapp.LoadingDialog;
import com.myappteam.projectapp.R;

public class ProfileImageSettingsActivity extends AppCompatActivity implements ProfileImageSettingsBottomDialog.OnChangeImage {
    private ImageView imageView;
    FirebaseAuth authAccount;
    FirebaseUser firebaseUser;
    private Uri uriImage;

    private boolean dataChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_image);

        ImageButton backButton = findViewById(R.id.backButton);
        Button changeImageButton = findViewById(R.id.changeImageButton);
        Button deleteImageButton = findViewById(R.id.deleteImageButton);
        imageView = findViewById(R.id.ChooseImageView);

        //Настраиваем кнопки выхода
        backButton.setOnClickListener(v -> {
            Intent result = new Intent();
            result.putExtra("imageChanged", dataChanged);
            setResult(Activity.RESULT_OK, result);
            finish();
        });
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent result = new Intent();
                result.putExtra("imageChanged", dataChanged);
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        });

        authAccount = FirebaseAuth.getInstance();
        firebaseUser = authAccount.getCurrentUser();

        Uri uri = firebaseUser.getPhotoUrl();
        Glide.with(this).load(uri).into(imageView);

        //Кнопка изменения изображения
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            uriImage = data.getData();
                            ProfileImageSettingsBottomDialog bottomDialog = ProfileImageSettingsBottomDialog.newInstance(uriImage);
                            bottomDialog.show(getSupportFragmentManager(), "applyNewImage");
                        }
                    }
                });
        changeImageButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activityResultLauncher.launch(intent);
        });

        //Кнопка удаления изображения
        deleteImageButton.setOnClickListener(view -> showDeleteImageDialog());
    }

    void showDeleteImageDialog() {
        new MaterialAlertDialogBuilder(ProfileImageSettingsActivity.this)
                .setTitle(R.string.profile_delete_dialog_title)
                .setMessage(R.string.profile_delete_dialog_text)
                .setNegativeButton("Нет", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Да", (dialog, which) -> {
                    dialog.dismiss();
                    //Показываем загрузочный диалог
                    LoadingDialog loadingDialog = new LoadingDialog(this, getString(R.string.profile_delete_dialog_loading));
                    loadingDialog.startDialog();
                    //Ставим дефолтное изображение пользователя
                    Uri uri = Uri.parse("android.resource://com.teamnexapp.teamnex/" + R.drawable.user);
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");
                    StorageReference fileReference = storageReference.child(authAccount.getCurrentUser().getUid());
                    fileReference.putFile(uri).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(uri1).build();
                        firebaseUser.updateProfile(profileChangeRequest).addOnSuccessListener(unused -> {
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("users").child(authAccount.getCurrentUser().getUid()).child("photo").setValue(uri1.toString());

                            //Показываем новое дефолтное изображение
                            Glide.with(this).load(uri).into(imageView);

                            //Устанавливаем флаг
                            dataChanged = true;

                            loadingDialog.dismissDialog();
                            Toast.makeText(ProfileImageSettingsActivity.this, R.string.profile_delete_dialog_success, Toast.LENGTH_SHORT).show();
                        });
                    })).addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .show();
    }

    @Override
    public void onChangeImage() {
        if (uriImage != null) {
            //Показываем загрузочный диалог
            LoadingDialog loadingDialog = new LoadingDialog(this, "Загружаем фото...");
            loadingDialog.startDialog();
            //Обновляем данные пользователя
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");
            StorageReference fileReference = storageReference.child(authAccount.getCurrentUser().getUid());
            fileReference.putFile(uriImage).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                //Загружаем данные в БД
                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(uri1).build();
                firebaseUser.updateProfile(profileChangeRequest).addOnSuccessListener(unused -> {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("users").child(authAccount.getCurrentUser().getUid()).child("photo").setValue(uri1.toString());

                    //Показываем новое изображение
                    Glide.with(this).load(uri1).into(imageView);

                    //Устанавливаем флаг
                    dataChanged = true;

                    loadingDialog.dismissDialog();
                    Toast.makeText(ProfileImageSettingsActivity.this, "Изображение успешно загружено!", Toast.LENGTH_SHORT).show();
                });
            })).addOnFailureListener(e -> Toast.makeText(ProfileImageSettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}