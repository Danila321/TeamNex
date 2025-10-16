package com.myappteam.projectapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
    private TextInputLayout nameLayout, emailLayout, passwordLayout, passwordAgainLayout;
    private TextInputEditText editTextName, editTextEmail, editTextPassword, editTextPasswordAgain;
    Button buttonRegister;
    TextView privacyPolicyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.md_theme_primary));

        ImageButton backToLogin = findViewById(R.id.backButton);
        editTextName = findViewById(R.id.name);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextPasswordAgain = findViewById(R.id.passwordAgain);
        buttonRegister = findViewById(R.id.btn);
        nameLayout = findViewById(R.id.nameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        passwordAgainLayout = findViewById(R.id.passwordAgainLayout);
        privacyPolicyText = findViewById(R.id.agreePrivacyPolicyText);

        mAuth = FirebaseAuth.getInstance();

        backToLogin.setOnClickListener(view -> finish());

        buttonRegister.setOnClickListener(v -> {
            String name, email, password, passwordAgain;
            name = String.valueOf(editTextName.getText()).trim();
            email = String.valueOf(editTextEmail.getText()).trim();
            password = String.valueOf(editTextPassword.getText());
            passwordAgain = String.valueOf(editTextPasswordAgain.getText());

            if (validateInputs(name, email, password, passwordAgain)) {
                registerUser(email, password, name);
            }
        });

        //Настраиваем ссылку на Политику конфиденциальности
        String fullText = "Регистрируясь, вы соглашаетесь с Политикой конфиденциальности TeamNex";
        String linkText = "Политикой конфиденциальности";

        int start = fullText.indexOf(linkText);
        int end = start + linkText.length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // открываем браузер с твоей ссылкой
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://teamnex.tilda.ws/privacy_policy"));
                widget.getContext().startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
            }
        };

        SpannableString spannable = new SpannableString(fullText);
        spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        privacyPolicyText.setText(spannable);
        privacyPolicyText.setMovementMethod(LinkMovementMethod.getInstance());
        privacyPolicyText.setHighlightColor(Color.TRANSPARENT);
    }

    private boolean validateInputs(String name, String email, String password, String passwordAgain) {
        boolean validate = true;
        if (name.isEmpty()) {
            nameLayout.setError(getString(R.string.register_error_name));
            validate = false;
        } else {
            nameLayout.setErrorEnabled(false);
        }
        if (email.isEmpty()) {
            emailLayout.setError(getString(R.string.register_error_email));
            validate = false;
        } else {
            emailLayout.setErrorEnabled(false);
        }
        if (password.isEmpty()) {
            passwordLayout.setError(getString(R.string.register_error_password));
            validate = false;
        } else {
            passwordLayout.setErrorEnabled(false);
            if (password.length() < 7) {
                passwordLayout.setError("Минимальная длина пароля: 7");
                validate = false;
            } else {
                passwordLayout.setErrorEnabled(false);
            }
        }
        if (passwordAgain.isEmpty()) {
            passwordAgainLayout.setError(getString(R.string.register_error_password_again));
            validate = false;
        } else {
            passwordAgainLayout.setErrorEnabled(false);
            if (!password.equals(passwordAgain)) {
                validate = false;
                passwordAgainLayout.setError(getString(R.string.register_error_password_not_match));
            } else {
                passwordAgainLayout.setErrorEnabled(false);
            }
        }
        return validate;
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
                        Uri uri = Uri.parse("android.resource://com.myappteam.projectapp/" + R.drawable.user);
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
                            passwordLayout.setError(getString(R.string.register_action_error_password));
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            emailLayout.setError(getString(R.string.register_action_email));
                        } catch (FirebaseAuthUserCollisionException e) {
                            emailLayout.setError(getString(R.string.register_action_email_already));
                        } catch (Exception e) {
                            Log.e("RegisterActivity", e.getMessage());
                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        //Закрываем загрузочный диалог
                        loadingDialog.dismissDialog();
                    }
                });
    }

    private void showDialog() {
        new MaterialAlertDialogBuilder(RegisterActivity.this)
                .setTitle(R.string.register_dialog_title)
                .setMessage(R.string.register_dialog_text)
                .setPositiveButton(R.string.register_dialog_button, (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }
}