package com.myappteam.projectapp.profile.passwordSettings;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myappteam.projectapp.LoadingDialog;
import com.myappteam.projectapp.R;

public class ProfilePasswordFragmentSecond extends Fragment {
    private String oldPassword;
    private TextInputLayout passwordLayout, passwordAgainLayout;
    private TextInputEditText passwordEditText, passwordAgainEditText;
    Button buttonUpdate;

    public ProfilePasswordFragmentSecond() {

    }

    public static ProfilePasswordFragmentSecond newInstance(String oldPassword) {
        ProfilePasswordFragmentSecond fragment = new ProfilePasswordFragmentSecond();
        Bundle args = new Bundle();
        args.putString("oldPassword", oldPassword);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            oldPassword = getArguments().getString("oldPassword");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.profile_password_fragment_second, container, false);


        ImageButton backButton = root.findViewById(R.id.backButton);

        passwordLayout = root.findViewById(R.id.ProfilePasswordLayout);
        passwordEditText = root.findViewById(R.id.ProfilePassword);
        passwordAgainLayout = root.findViewById(R.id.ProfilePasswordAgainLayout);
        passwordAgainEditText = root.findViewById(R.id.ProfilePasswordAgain);

        buttonUpdate = root.findViewById(R.id.btn);


        backButton.setOnClickListener(v -> getActivity().finish());

        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        buttonUpdate.setOnClickListener(v -> {
            String password = String.valueOf(passwordEditText.getText()).trim();
            String passwordAgain = String.valueOf(passwordAgainEditText.getText()).trim();
            if (validatePassword(password, passwordAgain)) {
                updatePassword(firebaseUser, password);
            }
        });

        return root;
    }

    boolean validatePassword(String password, String passwordAgain) {
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
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        //Показываем загрузочный диалог
        LoadingDialog loadingDialog = new LoadingDialog(getActivity(), "Меняем пароль...");
        loadingDialog.startDialog();

        firebaseUser.reauthenticate(EmailAuthProvider.getCredential(firebaseUser.getEmail(), oldPassword)).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseUser.updatePassword(password).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        loadingDialog.dismissDialog();
                        getActivity().finish();
                        Toast.makeText(getContext(), "Новый пароль успешно установлен!", Toast.LENGTH_SHORT).show();
                    } else {

                    }
                });
            } else {

            }
        });
    }
}