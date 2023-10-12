package com.example.teamdraft;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        backButton = findViewById(R.id.backButton);
        ImageView imageView = findViewById(R.id.ProfileImageView);
        Button imageChoose = findViewById(R.id.ImageAccountAction);
        Button imageDelete = findViewById(R.id.ImageAccountDelete);
        ImageButton editProfile = findViewById(R.id.ProfileEditButton);
        TextView textViewEmail = findViewById(R.id.ProfileTextViewEmail);
        TextView textViewName = findViewById(R.id.ProfileTextViewName);
        TextView textViewLogout = findViewById(R.id.ProfileLogout);
        TextView textViewDeleteAccount = findViewById(R.id.ProfileDelete);
        ProgressBar progressBar = findViewById(R.id.ProfileProgressBar);

        backButton.setOnClickListener(view -> finish());

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(ProfileActivity.this, "Произошла ошибка!", Toast.LENGTH_SHORT).show();
        } else {
            //Забираем данные пользователя из БД и показываем их
            progressBar.setVisibility(View.VISIBLE);
            textViewName.setText(firebaseUser.getDisplayName());
            textViewEmail.setText(firebaseUser.getEmail());
            Picasso.get().load(firebaseUser.getPhotoUrl()).into(imageView);
            progressBar.setVisibility(View.INVISIBLE);
            //Настраиваем кнопки для работы с изображением
            imageChoose.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, UploadProfileImageActivity.class);
                startActivity(intent);
            });
            imageDelete.setOnClickListener(v -> {
                showDeleteImageDialog();
            });
            //Настраиваем кнопку редактирования данных профиля
            editProfile.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, UpdateProfile.class);
                startActivity(intent);
            });
            //Настраиваем кнопку выхода из аккаунта
            textViewLogout.setOnClickListener(v -> {
                showSignOutDialog();
            });
            //Настраиваем кнопку удаления аккаунта
            textViewDeleteAccount.setOnClickListener(v -> {
                //TODO: Реализовать удаление аккаунта
            });
        }
    }

    void showDeleteImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Удаление фото профиля");
        builder.setMessage("Вы действительно хотите удалить фото профиля?");
        builder.setNegativeButton("Нет", (dialog, which) -> dialog.cancel());
        builder.setPositiveButton("Да", (dialog, which) -> {
            Toast.makeText(ProfileActivity.this, "Изображение успешно удалено!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            //TODO: Реализовать удаление пользовательского изображения
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void showSignOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Выход из аккаунта");
        builder.setMessage("Вы действительно хотите выйти из аккаунта?");
        builder.setNegativeButton("Нет", (dialog, which) -> dialog.cancel());
        builder.setPositiveButton("Да", (dialog, which) -> {
            dialog.dismiss();
            authProfile.signOut();
            Toast.makeText(ProfileActivity.this, "Вы успешно вышли!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}