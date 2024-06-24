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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.teamnexapp.teamnex.R;
import com.teamnexapp.teamnex.databinding.FragmentSettingsBinding;

import java.util.Locale;

public class SettingsFragment extends Fragment implements OnChangeLanguage {
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

        ConstraintLayout language = root.findViewById(R.id.settingsLanguage);
        language.setOnClickListener(v -> {
            BottomSheetDialog dialog = BottomSheetDialog.newInstance(currentLanguage);
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