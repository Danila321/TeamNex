package com.example.teamdraft;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class UploadProfileImageActivity extends AppCompatActivity {
    private ImageView imageView;
    StorageReference storageReference;
    FirebaseAuth authAccount;
    FirebaseUser firebaseUser;
    private Uri uriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_image);

        ImageButton backButton = findViewById(R.id.backButton);
        Button choose = findViewById(R.id.choose_image_button);
        Button upload = findViewById(R.id.uploadImage);
        imageView = findViewById(R.id.ChooseImageView);

        backButton.setOnClickListener(view -> finish());

        authAccount = FirebaseAuth.getInstance();
        firebaseUser = authAccount.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");

        Uri uri = firebaseUser.getPhotoUrl();
        Picasso.get().load(uri).into(imageView);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            uriImage = data.getData();
                            imageView.setImageURI(uriImage);
                        }
                    }
                });
        choose.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activityResultLauncher.launch(intent);
        });

        upload.setOnClickListener(v -> {
            if (uriImage != null) {
                //Показываем загрузочный диалог
                LoadingDialog loadingDialog = new LoadingDialog(this, "Загружаем фото...");
                loadingDialog.startDialog();
                //Обновляем данные пользователя
                StorageReference fileReference = storageReference.child(authAccount.getCurrentUser().getUid());
                fileReference.putFile(uriImage).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                    //Загружаем данные в БД
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(uri1).build();
                    firebaseUser.updateProfile(profileChangeRequest).addOnSuccessListener(unused -> {
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("users").child(authAccount.getCurrentUser().getUid()).child("photo").setValue(uri1.toString());

                        loadingDialog.dismissDialog();
                        Toast.makeText(UploadProfileImageActivity.this, "Изображение успешно загружено!", Toast.LENGTH_SHORT).show();

                        Intent result = new Intent();
                        result.putExtra("imageChanged", true);
                        setResult(Activity.RESULT_OK, result);
                        finish();
                    });
                })).addOnFailureListener(e -> Toast.makeText(UploadProfileImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
}