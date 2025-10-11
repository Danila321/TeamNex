package com.myappteam.projectapp.profile;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myappteam.projectapp.LoginActivity;
import com.myappteam.projectapp.R;
import com.myappteam.projectapp.profile.imageSettings.ProfileImageSettingsActivity;
import com.myappteam.projectapp.profile.passwordSettings.ProfilePasswordActivity;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    ImageView imageView;
    boolean dataChanged = false;

    boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        //Подключаем все view
        ImageButton backButton = findViewById(R.id.backButton);
        imageView = findViewById(R.id.ProfileImageView);
        Button imageSettings = findViewById(R.id.imageSettingsButton);
        TextView textViewEmail = findViewById(R.id.ProfileTextViewEmail);

        TextView textViewName = findViewById(R.id.ProfileTextViewName);
        ImageButton editNameButton = findViewById(R.id.ProfileEditNameButton);

        ImageButton editPasswordButton = findViewById(R.id.ProfileEditPasswordButton);

        TextView teamNexProStatus = findViewById(R.id.ProfileTextViewTeamNexPro);
        ImageButton openTeamNexPro = findViewById(R.id.ProfileTeamNexPro);

        TextView textViewPrivacyPolicy = findViewById(R.id.ProfilePrivacyPolicyText);
        TextView textViewSiteLink = findViewById(R.id.ProfileSiteText);

        TextView textViewLogout = findViewById(R.id.ProfileLogout);
        TextView textViewDeleteAccount = findViewById(R.id.ProfileDelete);
        //ProgressBar progressBar = findViewById(R.id.ProfileProgressBar);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //Настраиваем изменение цвета status bar
        int startColor = Color.parseColor("#F6FAFE");
        int endColor = Color.parseColor("#EAEEF2");
        ValueAnimator va1 = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
        va1.setDuration(130);
        va1.addUpdateListener(animation -> getWindow().setStatusBarColor((int) va1.getAnimatedValue()));
        ValueAnimator va2 = ValueAnimator.ofObject(new ArgbEvaluator(), endColor, startColor);
        va2.setDuration(130);
        va2.addUpdateListener(animation -> getWindow().setStatusBarColor((int) va2.getAnimatedValue()));
        AppBarLayout appBarLayout = findViewById(R.id.ProfileAppBar);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            if (verticalOffset < -602) {
                if (flag) {
                    flag = false;
                    va1.start();
                }
            } else {
                if (!flag) {
                    flag = true;
                    va2.start();
                }
            }
        });

        //Настраиваем кнопки выхода
        backButton.setOnClickListener(v -> {
            Intent result = new Intent();
            result.putExtra("imageChanged", dataChanged);
            setResult(Activity.RESULT_OK, result);
            finish();
        });
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent result = new Intent();
                result.putExtra("imageChanged", dataChanged);
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        });

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(ProfileActivity.this, "Произошла ошибка!", Toast.LENGTH_SHORT).show();
        } else {
            //Забираем данные пользователя из БД и показываем их
            //progressBar.setVisibility(View.VISIBLE);
            Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imageView);
            textViewEmail.setText(firebaseUser.getEmail());
            textViewName.setText(firebaseUser.getDisplayName());
            //progressBar.setVisibility(View.INVISIBLE);

            ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                if (data.getBooleanExtra("imageChanged", false)) {
                                    Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imageView);
                                    dataChanged = true;
                                }
                                if (data.getBooleanExtra("dataChanged", false)) {
                                    textViewName.setText(firebaseUser.getDisplayName());
                                    dataChanged = true;
                                }
                            }
                        }
                    });

            //Настраиваем кнопку для работы с изображением
            imageSettings.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, ProfileImageSettingsActivity.class);
                activityResultLauncher.launch(intent);
            });

            //Кнопка изменения имени пользователя
            editNameButton.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, UpdateNameProfile.class);
                activityResultLauncher.launch(intent);
            });

            //Кнопка изменения пароля
            editPasswordButton.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, ProfilePasswordActivity.class);
                startActivity(intent);
            });


            //Блок ссылок
            textViewPrivacyPolicy.setPaintFlags(textViewPrivacyPolicy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            textViewPrivacyPolicy.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://teamnex.tilda.ws/privacy_policy"));
                v.getContext().startActivity(intent);
            });
            textViewSiteLink.setPaintFlags(textViewSiteLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            textViewSiteLink.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://teamnex.tilda.ws/"));
                v.getContext().startActivity(intent);
            });


            //Настраиваем кнопку выхода из аккаунта
            textViewLogout.setOnClickListener(v -> showSignOutDialog());

            //Настраиваем кнопку удаления аккаунта
            textViewDeleteAccount.setOnClickListener(view -> showDeleteAccountDialog());
        }
    }

    void showSignOutDialog() {
        new MaterialAlertDialogBuilder(ProfileActivity.this)
                .setTitle(R.string.profile_exit_title)
                .setMessage(R.string.profile_exit_text)
                .setNegativeButton(R.string.profile_exit_no, (dialog, which) -> dialog.cancel())
                .setPositiveButton(R.string.profile_exit_yes, (dialog, which) -> {
                    dialog.dismiss();
                    authProfile.signOut();
                    Toast.makeText(ProfileActivity.this, R.string.profile_exit_complete, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .show();
    }

    void showDeleteAccountDialog() {
        new MaterialAlertDialogBuilder(ProfileActivity.this)
                .setTitle("Удаление аккаунта")
                .setMessage("В разработке...")
                .setPositiveButton("Ок", (dialog, which) -> dialog.cancel())
                .show();
    }
}