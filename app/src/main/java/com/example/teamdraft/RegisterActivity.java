package com.example.teamdraft;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText editTextName, editTextEmail, editTextPassword, editTextPasswordAgain;
    Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ImageButton backToLogin = findViewById(R.id.backButton);
        editTextName = findViewById(R.id.name);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextPasswordAgain = findViewById(R.id.password_again);
        buttonRegister = findViewById(R.id.btn_register);

        mAuth = FirebaseAuth.getInstance();

        backToLogin.setOnClickListener(view -> finish());

        buttonRegister.setOnClickListener(v -> {
            String name, email, password, passwordAgain;
            name = String.valueOf(editTextName.getText());
            email = String.valueOf(editTextEmail.getText());
            password = String.valueOf(editTextPassword.getText());
            passwordAgain = String.valueOf(editTextPasswordAgain.getText());

            if (name.isEmpty()) {
                editTextName.setError(getString(R.string.register_error_name));
            } else if (email.isEmpty()) {
                editTextEmail.setError(getString(R.string.register_error_email));
            } else if (password.isEmpty()) {
                editTextPassword.setError(getString(R.string.register_error_password));
            } else if (passwordAgain.isEmpty()) {
                editTextPasswordAgain.setError(getString(R.string.register_error_password_again));
            } else if (!password.equals(passwordAgain)) {
                editTextPasswordAgain.setError(getString(R.string.register_error_password_not_match));
            } else {
                registerUser(email, password, name);
            }
        });
    }

    private void registerUser(String email, String password, String name) {
        //Закрываем клавиатуру
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        //Показываем загрузочный диалог
        LoadingDialog loadingDialog = new LoadingDialog(this, getString(R.string.register_loading));
        loadingDialog.startDialog();
        //Добавляем юзера в БД
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                        firebaseUser.updateProfile(profileChangeRequest);

                        // Загружаем дефолтное изображения пользователя в Storage
                        Uri uri = Uri.parse("android.resource://com.example.teamdraft/" + R.drawable.user);
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");
                        StorageReference fileReference = storageReference.child(mAuth.getCurrentUser().getUid());
                        fileReference.putFile(uri).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                            UserProfileChangeRequest profileChangeRequestPhoto = new UserProfileChangeRequest.Builder().setPhotoUri(uri1).build();
                            firebaseUser.updateProfile(profileChangeRequestPhoto);

                            // Сохраняем имя и изображение пользователя в базу данных
                            String userId = firebaseUser.getUid();
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("id", userId);
                            userData.put("name", name);
                            userData.put("photo", String.valueOf(uri1));
                            usersRef.setValue(userData)
                                    .addOnCompleteListener(databaseTask -> {
                                        if (databaseTask.isSuccessful()) {
                                            //Отправляем юзеру письмо
                                            firebaseUser.sendEmailVerification();
                                            //Выходим из зарегистрированного аккаунта
                                            mAuth.signOut();
                                            //Закрываем загрузочный диалог
                                            loadingDialog.dismissDialog();
                                            //Показываем диалоговое окно и выходим при нажатии на кнопку
                                            showDialog();
                                        } else {
                                            // Ошибка сохранения данных в БД
                                            Exception databaseException = databaseTask.getException();
                                            if (databaseException != null) {
                                                databaseException.printStackTrace();
                                            }
                                        }
                                    });
                        })).addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            editTextPassword.setError(getString(R.string.register_action_error_password));
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            editTextEmail.setError(getString(R.string.register_action_email));
                        } catch (FirebaseAuthUserCollisionException e) {
                            editTextEmail.setError(getString(R.string.register_action_email_already));
                        } catch (Exception e) {
                            Log.e("RegisterActivity", e.getMessage());
                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle(R.string.register_dialog_title);
        builder.setMessage(R.string.register_dialog_text);
        builder.setPositiveButton(R.string.register_dialog_button, (dialog, which) -> finish());
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}