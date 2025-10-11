package com.myappteam.projectapp.profile.passwordSettings;

import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.myappteam.projectapp.R;

public class ProfilePasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_password_activity);

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.md_theme_primary));

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.profileConstraintLayout, new ProfilePasswordSettingsFirst())
                .commit();
    }
}