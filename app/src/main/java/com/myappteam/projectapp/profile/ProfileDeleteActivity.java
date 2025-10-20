package com.myappteam.projectapp.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myappteam.projectapp.BaseActivity;
import com.myappteam.projectapp.LoadingDialog;
import com.myappteam.projectapp.LoginActivity;
import com.myappteam.projectapp.R;
import com.myappteam.projectapp.profile.passwordSettings.ProfilePasswordSettingsFirst;
import com.myappteam.projectapp.ui.home.Board;

public class ProfileDeleteActivity extends BaseActivity {
    TextInputLayout oldPasswordLayout;
    TextInputEditText oldPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_delete);

        ImageButton backButton = findViewById(R.id.backButtonDeleteActivity);
        oldPasswordLayout = findViewById(R.id.ProfileDeleteOldPasswordLayout);
        oldPasswordEditText = findViewById(R.id.ProfileDeleteOldPassword);
        Button buttonDelete = findViewById(R.id.profileDeleteButton);
        Button cancelButton = findViewById(R.id.profile_delete_cancel);

        //Настраиваем кнопки выхода
        backButton.setOnClickListener(v -> finish());

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        buttonDelete.setOnClickListener(v -> {
            String password = String.valueOf(oldPasswordEditText.getText()).trim();
            if (password.isEmpty()) {
                oldPasswordLayout.setError(getString(R.string.profile_password1_error));
            } else {
                oldPasswordLayout.setErrorEnabled(false);
                validate(firebaseUser, password, checked -> {
                    if (checked) {
                        new MaterialAlertDialogBuilder(ProfileDeleteActivity.this)
                                .setTitle(getString(R.string.profile_delete_dialog_title))
                                .setMessage(getString(R.string.profile_delete_dialog_text))
                                .setPositiveButton(getString(R.string.profile_delete_dialog_button_delete), (dialog, which) -> deleteAccount(auth, firebaseUser, password))
                                .setNegativeButton(getString(R.string.profile_delete_dialog_button_cancel), (dialog, which) -> dialog.cancel())
                                .show();
                    } else {
                        oldPasswordLayout.setError(getString(R.string.profile_password1_error2));
                    }
                });
            }
        });

        cancelButton.setOnClickListener(v -> finish());
    }

    private void validate(FirebaseUser user, String password, ProfilePasswordSettingsFirst.OnCheckPasswordListener onCheckPassword) {
        //Закрываем клавиатуру
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        LoadingDialog loadingDialog = new LoadingDialog(this, getString(R.string.profile_password1_dialog));
        loadingDialog.startDialog();
        user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), password)).addOnCompleteListener(task -> {
            loadingDialog.dismissDialog();
            onCheckPassword.onChecked(task.isSuccessful());
        });
    }

    private void deleteAccount(FirebaseAuth auth, FirebaseUser user, String password) {
        //Закрываем клавиатуру
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        //Показываем загрузочный диалог
        LoadingDialog loadingDialog = new LoadingDialog(this, getString(R.string.profile_delete_dialog_wait));
        loadingDialog.startDialog();

        DatabaseReference boardsRef = FirebaseDatabase.getInstance().getReference().child("boards");
        user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), password)).addOnCompleteListener(task -> {
            user.delete().addOnCompleteListener(task1 -> {
                if (task.isSuccessful()) {
                    //Удаляем все доски, где юзер админ
                    boardsRef.orderByChild("users/" + user.getUid()).equalTo("owner").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot boardSnapshot : snapshot.getChildren()) {
                                    Board board = boardSnapshot.getValue(Board.class);
                                    boardsRef.child(board.getId()).removeValue();
                                }
                            }

                            //Удаляем юзера из досок, где он админ
                            boardsRef.orderByChild("users/" + user.getUid()).equalTo("admin").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot boardSnapshot : snapshot.getChildren()) {
                                            Board board = boardSnapshot.getValue(Board.class);
                                            boardsRef.child(board.getId()).child("users").child(user.getUid()).removeValue();

                                            //Удаляем юзера из карточек, если он там есть
                                            DataSnapshot itemsSnapshot = boardSnapshot.child("items");
                                            if (itemsSnapshot.exists()) {
                                                for (DataSnapshot itemSnapshot : itemsSnapshot.getChildren()) {
                                                    DataSnapshot cardsSnapshot = itemSnapshot.child("cards");
                                                    if (cardsSnapshot.exists()) {
                                                        for (DataSnapshot cardSnapshot : cardsSnapshot.getChildren()) {
                                                            if (cardSnapshot.child("users").hasChild(user.getUid())) {
                                                                cardSnapshot.getRef().child("users").child(user.getUid()).removeValue();
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    //Удаляем юзера из досок, где он участник
                                    boardsRef.orderByChild("users/" + user.getUid()).equalTo("user").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                for (DataSnapshot boardSnapshot : snapshot.getChildren()) {
                                                    Board board = boardSnapshot.getValue(Board.class);
                                                    boardsRef.child(board.getId()).child("users").child(user.getUid()).removeValue();

                                                    //Удаляем юзера из карточек, если он там есть
                                                    DataSnapshot itemsSnapshot = boardSnapshot.child("items");
                                                    if (itemsSnapshot.exists()) {
                                                        for (DataSnapshot itemSnapshot : itemsSnapshot.getChildren()) {
                                                            DataSnapshot cardsSnapshot = itemSnapshot.child("cards");
                                                            if (cardsSnapshot.exists()) {
                                                                for (DataSnapshot cardSnapshot : cardsSnapshot.getChildren()) {
                                                                    if (cardSnapshot.child("users").hasChild(user.getUid())) {
                                                                        cardSnapshot.getRef().child("users").child(user.getUid()).removeValue();
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            //Удаляем юзера из списка юзеров
                                            FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).removeValue();

                                            //Зыкрываем диалог
                                            loadingDialog.dismissDialog();
                                            auth.signOut();
                                            Toast.makeText(ProfileDeleteActivity.this, getString(R.string.profile_delete_success), Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
        });
    }
}