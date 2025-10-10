package com.myappteam.projectapp.profile.passwordSettings;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myappteam.projectapp.LoadingDialog;
import com.myappteam.projectapp.R;

public class ProfilePasswordActivity extends AppCompatActivity {
    TextInputLayout oldPasswordLayout;
    TextInputEditText oldPasswordEditText;
    /*private TextInputLayout passwordLayout, passwordAgainLayout;
    private TextInputEditText passwordEditText, passwordAgainEditText;*/
    Button buttonUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_password_activity);

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.md_theme_primary));

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.profilePasswordConstraintLayout, new ProfilePasswordSettingsFirst())
                .commit();

        /*
        //Подключаем необходимые view


        oldPasswordLayout = findViewById(R.id.ProfileOldPasswordLayout);
        oldPasswordEditText = findViewById(R.id.ProfileOldPassword);
        passwordLayout = findViewById(R.id.passwordLayout);
        passwordEditText = findViewById(R.id.password);
        passwordAgainLayout = findViewById(R.id.passwordAgainLayout);
        passwordAgainEditText = findViewById(R.id.passwordAgain);

        buttonUpdate = findViewById(R.id.btn);

        backButton.setOnClickListener(view -> finish());

        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        CardView cardView = findViewById(R.id.cardView5);
        cardView.setVisibility(View.INVISIBLE);

        title.setText("Новый пароль");
        nameLayout.setVisibility(View.GONE);
        emailLayout.setVisibility(View.GONE);
        buttonUpdate.setText("Сохранить");

        buttonUpdate.setOnClickListener(v -> {
            String password = String.valueOf(passwordEditText.getText()).trim();
            String passwordAgain = String.valueOf(passwordAgainEditText.getText()).trim();
            if (validatePassword(password, passwordAgain)) {
                updatePassword(firebaseUser, password);
            }
        });*/
    }

    /*boolean validatePassword(String password, String passwordAgain) {
        boolean validate = true;
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

    void updatePassword(FirebaseUser firebaseUser, String password) {
        //Закрываем клавиатуру
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        //Показываем загрузочный диалог
        LoadingDialog loadingDialog = new LoadingDialog(this, "Меняем пароль...");
        loadingDialog.startDialog();

        firebaseUser.reauthenticate(EmailAuthProvider.getCredential(firebaseUser.getEmail(), password)).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseUser.updatePassword(password).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(ProfilePasswordActivity.this, "Новый пароль успешно установлен", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("ОШибка2222", "sdfsadfgdsfgdsfgdsg");
                    }
                });
            } else {
                Log.i("ОШибка111", "sdfsadfgdsfgdsfgdsg");
            }
        });
    }*/
}