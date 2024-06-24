package com.teamnexapp.teamnex;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText editTextEmail;
    Button buttonReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ImageButton backToLogin = findViewById(R.id.backButton);
        editTextEmail = findViewById(R.id.email);
        buttonReset = findViewById(R.id.btn_reset);

        backToLogin.setOnClickListener(view -> finish());

        buttonReset.setOnClickListener(v -> {
            String email = String.valueOf(editTextEmail.getText());

            if (email.isEmpty()) {
                editTextEmail.setError(getString(R.string.reset_error));
            } else {
                resetPassword(email);
            }
        });
    }

    void resetPassword(String email) {
        //Закрываем клавиатуру
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        //Показываем загрузочный диалог
        LoadingDialog loadingDialog = new LoadingDialog(this, getString(R.string.reset_loading));
        loadingDialog.startDialog();

        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            loadingDialog.dismissDialog();
            if (task.isSuccessful()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                builder.setTitle(R.string.reset_dialog_title);
                builder.setMessage(R.string.reset_dialog_text);
                builder.setPositiveButton(R.string.reset_dialog_button, (dialog, which) -> finish());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                try {
                    throw task.getException();
                } catch (FirebaseAuthInvalidUserException e) {
                    editTextEmail.setError(getString(R.string.reset_error_user));
                } catch (Exception e) {
                    Log.e("ForgotPasswordActivity", e.getMessage());
                    Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}