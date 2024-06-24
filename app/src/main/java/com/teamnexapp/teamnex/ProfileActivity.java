package com.teamnexapp.teamnex;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    ImageView imageView;
    boolean imageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageButton backButton = findViewById(R.id.backButton);
        imageView = findViewById(R.id.ProfileImageView);
        Button imageChoose = findViewById(R.id.ImageAccountAction);
        Button imageDelete = findViewById(R.id.ImageAccountDelete);
        ImageButton editProfile = findViewById(R.id.ProfileEditButton);
        TextView textViewEmail = findViewById(R.id.ProfileTextViewEmail);
        TextView textViewName = findViewById(R.id.ProfileTextViewName);
        TextView textViewLogout = findViewById(R.id.ProfileLogout);
        TextView textViewDeleteAccount = findViewById(R.id.ProfileDelete);
        ProgressBar progressBar = findViewById(R.id.ProfileProgressBar);

        //Настраиваем кнопки выхода
        backButton.setOnClickListener(v -> {
            Intent result = new Intent();
            result.putExtra("imageChanged", imageChanged);
            setResult(Activity.RESULT_OK, result);
            finish();
        });
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent result = new Intent();
                result.putExtra("imageChanged", imageChanged);
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        });

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(ProfileActivity.this, "Произошла ошибка!", Toast.LENGTH_SHORT).show();
        } else {
            //Забираем данные пользователя из БД и показываем их
            progressBar.setVisibility(View.VISIBLE);
            textViewName.setText(firebaseUser.getDisplayName());
            textViewEmail.setText(firebaseUser.getEmail());
            Picasso.get().load(firebaseUser.getPhotoUrl()).into(imageView);
            progressBar.setVisibility(View.INVISIBLE);

            ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                if (data.getBooleanExtra("imageChanged", false)) {
                                    Picasso.get().load(firebaseUser.getPhotoUrl()).into(imageView);
                                    imageChanged = true;
                                }
                                if (data.getBooleanExtra("dataChanged", false)){
                                    textViewName.setText(firebaseUser.getDisplayName());
                                }
                            }
                        }
                    });

            //Настраиваем кнопки для работы с изображением
            imageChoose.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, UploadProfileImageActivity.class);
                activityResultLauncher.launch(intent);
            });
            imageDelete.setOnClickListener(v -> showDeleteImageDialog());

            //Настраиваем кнопку редактирования данных профиля
            editProfile.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, UpdateProfile.class);
                activityResultLauncher.launch(intent);
            });

            //Настраиваем кнопку выхода из аккаунта
            textViewLogout.setOnClickListener(v -> showSignOutDialog());

            //Настраиваем кнопку удаления аккаунта
            textViewDeleteAccount.setOnClickListener(v -> {
                //TODO: Реализовать удаление аккаунта
            });
        }
    }

    void showDeleteImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle(R.string.profile_delete_dialog_title);
        builder.setMessage(R.string.profile_delete_dialog_text);
        builder.setNegativeButton("Нет", (dialog, which) -> dialog.cancel());
        builder.setPositiveButton("Да", (dialog, which) -> {
            dialog.dismiss();
            //Показываем загрузочный диалог
            LoadingDialog loadingDialog = new LoadingDialog(this, getString(R.string.profile_delete_dialog_loading));
            loadingDialog.startDialog();
            //Ставим дефолтное изображение пользователя
            Uri uri = Uri.parse("android.resource://com.teamnexapp.teamnex/" + R.drawable.user);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");
            StorageReference fileReference = storageReference.child(authProfile.getCurrentUser().getUid());
            fileReference.putFile(uri).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(uri1).build();
                firebaseUser.updateProfile(profileChangeRequest).addOnSuccessListener(unused -> {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("users").child(authProfile.getCurrentUser().getUid()).child("photo").setValue(uri1.toString());

                    Picasso.get().load(uri1).into(imageView);

                    imageChanged = true;

                    loadingDialog.dismissDialog();
                    Toast.makeText(ProfileActivity.this, R.string.profile_delete_dialog_success, Toast.LENGTH_SHORT).show();
                });
            })).addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void showSignOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle(R.string.profile_exit_title);
        builder.setMessage(R.string.profile_exit_text);
        builder.setNegativeButton(R.string.profile_exit_no, (dialog, which) -> dialog.cancel());
        builder.setPositiveButton(R.string.profile_exit_yes, (dialog, which) -> {
            dialog.dismiss();
            authProfile.signOut();
            Toast.makeText(ProfileActivity.this, R.string.profile_exit_complete, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}