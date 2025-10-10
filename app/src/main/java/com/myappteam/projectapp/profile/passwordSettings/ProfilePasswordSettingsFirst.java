package com.myappteam.projectapp.profile.passwordSettings;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.myappteam.projectapp.R;

public class ProfilePasswordSettingsFirst extends Fragment {
    public ProfilePasswordSettingsFirst() {

    }

    public static ProfilePasswordSettingsFirst newInstance(String param1, String param2) {
        ProfilePasswordSettingsFirst fragment = new ProfilePasswordSettingsFirst();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.profile_password_fragment_first, container, false);

        ImageButton backButton = root.findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.profilePasswordConstraintLayout, new ProfilePasswordFragmentSecond())
                        .commit();
            }
        });

        return root;
    }
}