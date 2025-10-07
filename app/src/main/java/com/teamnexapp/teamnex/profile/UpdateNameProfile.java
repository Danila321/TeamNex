package com.teamnexapp.teamnex.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.teamnexapp.teamnex.LoadingDialog;
import com.teamnexapp.teamnex.R;

public class UpdateNameProfile extends AppCompatActivity {
    private TextInputEditText editTextName;
    private TextInputLayout nameLayout;
    Button buttonUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.md_theme_primary));

        ImageButton backButton = findViewById(R.id.backButton);
        TextView title = findViewById(R.id.ActionTitle);
        TextInputLayout editTextEmail = findViewById(R.id.emailLayout);
        TextInputLayout editTextPassword = findViewById(R.id.passwordLayout);
        TextInputLayout editTextPasswordAgain = findViewById(R.id.passwordAgainLayout);
        editTextName = findViewById(R.id.name);
        nameLayout = findViewById(R.id.nameLayout);
        buttonUpdate = findViewById(R.id.btn);

        backButton.setOnClickListener(view -> finish());

        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        CardView cardView = findViewById(R.id.cardView5);
        cardView.setVisibility(View.INVISIBLE);

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
        String name = String.valueOf(editTextName.getText()).trim();
        if (name.isEmpty()) {
            nameLayout.setError("Введите свое имя");
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
                Toast.makeText(UpdateNameProfile.this, "Профиль успешно обновлен", Toast.LENGTH_SHORT).show();

                Intent result = new Intent();
                result.putExtra("dataChanged", true);
                setResult(Activity.RESULT_OK, result);
                finish();
            });
        }
    }
}