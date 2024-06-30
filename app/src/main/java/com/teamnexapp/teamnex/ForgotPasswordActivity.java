package com.teamnexapp.teamnex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordActivity extends AppCompatActivity {
    private TextInputLayout emailLayout;
    private TextInputEditText editTextEmail;
    Button buttonReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ImageButton backToLogin = findViewById(R.id.backButton);
        emailLayout = findViewById(R.id.textInputLayoutForgotEmail);
        editTextEmail = findViewById(R.id.email);
        buttonReset = findViewById(R.id.btn_reset);

        backToLogin.setOnClickListener(view -> finish());

        buttonReset.setOnClickListener(v -> {
            String email = String.valueOf(editTextEmail.getText()).trim();

            if (email.isEmpty()) {
                emailLayout.setError(getString(R.string.reset_error));
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
                new MaterialAlertDialogBuilder(ForgotPasswordActivity.this)
                        .setTitle(R.string.reset_dialog_title)
                        .setMessage(R.string.reset_dialog_text)
                        .setPositiveButton(R.string.reset_dialog_button, (dialog, which) -> finish())
                        .show();
            } else {
                try {
                    throw task.getException();
                } catch (FirebaseAuthInvalidUserException e) {
                    emailLayout.setError(getString(R.string.reset_error_user));
                } catch (Exception e) {
                    Log.e("ForgotPasswordActivity", e.getMessage());
                    Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}