package com.teamnexapp.teamnex.ui.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.teamnexapp.teamnex.R;
import com.teamnexapp.teamnex.databinding.FragmentSettingsBinding;

import java.util.Locale;

public class SettingsFragment extends Fragment implements OnChangeSettings {
    private FragmentSettingsBinding binding;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String currentLanguage = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE).getString("language", "ru");
        TextView languageTextView = root.findViewById(R.id.settingsLanguageText);
        if (currentLanguage.equals("ru")) {
            languageTextView.setText("Русский");
        } else {
            languageTextView.setText("English");
        }

        boolean currentTheme = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE).getBoolean("theme", true);
        TextView themeTextView = root.findViewById(R.id.settingsThemeText);
        if (currentTheme) {
            themeTextView.setText("Светлая");
        } else {
            themeTextView.setText("Тёмная");
        }

        ConstraintLayout language = root.findViewById(R.id.settingsLanguage);
        language.setOnClickListener(v -> {
            BottomSheetDialogLanguage dialog = BottomSheetDialogLanguage.newInstance(currentLanguage);
            dialog.show(getChildFragmentManager(), "chooseLanguageDialog");
        });

        ConstraintLayout theme = root.findViewById(R.id.settingsTheme);
        theme.setOnClickListener(v -> {
            BottomSheetDialogTheme dialogTheme = BottomSheetDialogTheme.newInstance(currentTheme);
            dialogTheme.show(getChildFragmentManager(), "chooseThemeDialog");
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

    @Override
    public void onChangeTheme(boolean theme) {
        if (theme){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit().putBoolean("theme", theme).apply();
        getActivity().recreate();
    }
}