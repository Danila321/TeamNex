package com.teamnexapp.teamnex.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.teamnexapp.teamnex.R;

public class ProfileFragmentInfo extends Fragment {
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment_info, container, false);

        //ImageButton editProfile = view.findViewById(R.id.ProfileEditButton);
        //TextView textViewEmail = view.findViewById(R.id.ProfileTextViewEmail);
        //TextView textViewName = view.findViewById(R.id.ProfileTextViewName);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        //textViewName.setText(firebaseUser.getDisplayName());
        //textViewEmail.setText(firebaseUser.getEmail());

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            if (data.getBooleanExtra("dataChanged", false)) {
                                //textViewName.setText(firebaseUser.getDisplayName());
                                //dataChanged = true;
                            }
                        }
                    }
                });
        //Настраиваем кнопку редактирования данных профиля
        /*editProfile.setOnClickListener(v -> {
        Intent intent = new Intent(getContext(), UpdateProfile.class);
        activityResultLauncher.launch(intent);
        });*/

        return view;
    }
}
