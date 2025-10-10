package com.myappteam.projectapp.profile.passwordSettings;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myappteam.projectapp.R;

public class ProfilePasswordFragmentSecond extends Fragment {
    private String password;

    public ProfilePasswordFragmentSecond() {
        // Required empty public constructor
    }

    public static ProfilePasswordFragmentSecond newInstance(String password) {
        ProfilePasswordFragmentSecond fragment = new ProfilePasswordFragmentSecond();
        Bundle args = new Bundle();
        args.putString("password", password);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            password = getArguments().getString("password");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.profile_password_fragment_second, container, false);



        return root;
    }
}