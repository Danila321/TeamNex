package com.example.teamdraft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UpdateProfile extends AppCompatActivity {
    private EditText editTextName;
    Button buttonUpdate;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ImageButton backButton = findViewById(R.id.backButton);
        TextView title = findViewById(R.id.ActionTitle);
        EditText editTextEmail = findViewById(R.id.email);
        EditText editTextPassword = findViewById(R.id.password);
        EditText editTextPasswordAgain = findViewById(R.id.password_again);
        editTextName = findViewById(R.id.name);
        buttonUpdate = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);

        backButton.setOnClickListener(view -> finish());

        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        title.setText("Профиль");
        editTextEmail.setVisibility(View.GONE);
        editTextPassword.setVisibility(View.GONE);
        editTextPasswordAgain.setVisibility(View.GONE);
        buttonUpdate.setText("Обновить");

        if (firebaseUser != null) {
            showData(firebaseUser);
        }

        buttonUpdate.setOnClickListener(v -> updateData(firebaseUser));
    }

    private void showData(FirebaseUser firebaseUser) {
        progressBar.setVisibility(View.VISIBLE);
        editTextName.setText(firebaseUser.getDisplayName());
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void updateData(FirebaseUser firebaseUser) {
        String name = String.valueOf(editTextName.getText());
        if (name.length() == 0) {
            editTextName.setError("Введите свое имя");
        } else {
            progressBar.setVisibility(View.VISIBLE);
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
            firebaseUser.updateProfile(profileUpdates);
            Toast.makeText(UpdateProfile.this, "Профиль успешно обновлен", Toast.LENGTH_SHORT).show();
            finish();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}