package com.example.teamdraft.ui.settings;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.teamdraft.R;
import com.example.teamdraft.databinding.FragmentSettingsBinding;

import java.util.Locale;

public class SettingsFragment extends Fragment implements OnChangeLanguage {
    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ConstraintLayout language = root.findViewById(R.id.settingsLanguage);
        language.setOnClickListener(v -> {
            String lang = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE).getString("language", "");
            BottomSheetDialog dialog = BottomSheetDialog.newInstance(lang);
            dialog.show(getChildFragmentManager(), "chooseLanguageDialog");
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onChangeLanguage(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = getActivity().getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit().putString("language", language).apply();
        getActivity().recreate();
    }
}