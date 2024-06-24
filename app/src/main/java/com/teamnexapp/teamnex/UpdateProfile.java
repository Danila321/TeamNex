package com.teamnexapp.teamnex;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateProfile extends AppCompatActivity {
    private EditText editTextName;
    Button buttonUpdate;

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

        backButton.setOnClickListener(view -> finish());

        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        title.setText(R.string.profile_title);
        editTextEmail.setVisibility(View.GONE);
        editTextPassword.setVisibility(View.GONE);
        editTextPasswordAgain.setVisibility(View.GONE);
        buttonUpdate.setText(R.string.profile_button_update);

        if (firebaseUser != null) {
            editTextName.setText(firebaseUser.getDisplayName());
        }

        buttonUpdate.setOnClickListener(v -> updateData(firebaseUser));
    }

    private void updateData(FirebaseUser firebaseUser) {
        String name = String.valueOf(editTextName.getText());
        if (name.isEmpty()) {
            editTextName.setError("Введите свое имя");
        } else {
            //Закрываем клавиатуру
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            //Показываем загрузочный диалог
            LoadingDialog loadingDialog = new LoadingDialog(this, "Обновляем данные...");
            loadingDialog.startDialog();
            //Обновляем данные пользователя
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
            firebaseUser.updateProfile(profileUpdates).addOnSuccessListener(unused -> {
                //Загружаем данные в БД
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid()).child("name");
                usersRef.setValue(name);

                loadingDialog.dismissDialog();
                Toast.makeText(UpdateProfile.this, "Профиль успешно обновлен", Toast.LENGTH_SHORT).show();

                Intent result = new Intent();
                result.putExtra("dataChanged", true);
                setResult(Activity.RESULT_OK, result);
                finish();
            });
        }
    }
}