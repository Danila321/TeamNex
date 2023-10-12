package com.example.teamdraft;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    ImageButton backToLogin;
    private EditText editTextName, editTextEmail, editTextPassword, editTextPasswordAgain;
    Button buttonRegister;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        backToLogin = findViewById(R.id.backButton);
        editTextName = findViewById(R.id.name);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextPasswordAgain = findViewById(R.id.password_again);
        buttonRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        backToLogin.setOnClickListener(view -> finish());

        buttonRegister.setOnClickListener(v -> {
            String name, email, password, passwordAgain;
            name = String.valueOf(editTextName.getText());
            email = String.valueOf(editTextEmail.getText());
            password = String.valueOf(editTextPassword.getText());
            passwordAgain = String.valueOf(editTextPasswordAgain.getText());

            if (name.length() == 0) {
                editTextName.setError("Введите свое имя");
            } else if (email.length() == 0) {
                editTextEmail.setError("Введите email");
            } else if (password.length() == 0) {
                editTextPassword.setError("Введите пароль");
            } else if (passwordAgain.length() == 0) {
                editTextPasswordAgain.setError("Повторите введенный пароль");
            } else if (!password.equals(passwordAgain)) {
                editTextPasswordAgain.setError("Пароли не совпадают");
            } else {
                progressBar.setVisibility(View.VISIBLE);
                registerUser(email, password, name);
            }
        });
    }

    private void registerUser(String email, String password, String name) {
        //Добавляем юзера в БД
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                        firebaseUser.updateProfile(profileChangeRequest);
                        //Отправляем юзеру письмо
                        firebaseUser.sendEmailVerification();
                        //Показываем диалоговое окно и выходим при нажатии на кнопку
                        showDialog();
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            editTextPassword.setError("Пароль слишком слабый");
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            editTextEmail.setError("Email введен некорректно");
                        } catch (FirebaseAuthUserCollisionException e) {
                            editTextEmail.setError("Пользователь с таким email уже зарегестрирован");
                        } catch (Exception e) {
                            Log.e("RegisterActivity", e.getMessage());
                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Вы успешно зарегистрировались!");
        builder.setMessage("На указанную почту было отправлено письмо с ссылкой для подтверждения, для авторизации в приложении необходимо перейти по этой ссылке");
        builder.setPositiveButton("Хорошо", (dialog, which) -> finish());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}