package com.myappteam.projectapp.profile.passwordSettings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myappteam.projectapp.LoadingDialog;
import com.myappteam.projectapp.R;

public class ProfilePasswordSettingsFirst extends Fragment {
    TextInputLayout oldPasswordLayout;
    TextInputEditText oldPasswordEditText;

    public ProfilePasswordSettingsFirst() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.profile_password_fragment_first, container, false);


        ImageButton backButton = root.findViewById(R.id.backButton);

        oldPasswordLayout = root.findViewById(R.id.ProfileOldPasswordLayout);
        oldPasswordEditText = root.findViewById(R.id.ProfileOldPassword);

        Button buttonContinue = root.findViewById(R.id.password_next);


        backButton.setOnClickListener(v -> getActivity().finish());

        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        buttonContinue.setOnClickListener(v -> {
            String password = String.valueOf(oldPasswordEditText.getText()).trim();
            if (password.isEmpty()){
                oldPasswordLayout.setError("Введите ваш текущий пароль");
            } else {
                oldPasswordLayout.setErrorEnabled(false);
                validate(firebaseUser, password, checked -> {
                    if (checked){
                        getParentFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.profile_password_open, R.anim.profile_password_hide)
                                .replace(R.id.profileConstraintLayout, ProfilePasswordFragmentSecond.newInstance(password))
                                .commit();
                    } else {
                        oldPasswordLayout.setError("Неверный пароль");
                    }
                });
            }
        });

        return root;
    }

    private void validate(FirebaseUser user, String password, OnCheckPasswordListener onCheckPassword){
        //Закрываем клавиатуру
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        LoadingDialog loadingDialog = new LoadingDialog(getActivity(), "Проверяем пароль...");
        loadingDialog.startDialog();
        user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), password)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingDialog.dismissDialog();
                onCheckPassword.onChecked(task.isSuccessful());
            }
        });
    }

    public interface OnCheckPasswordListener{
        void onChecked(boolean checked);
    }
}