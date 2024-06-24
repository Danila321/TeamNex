package com.teamnexapp.teamnex.ui.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.teamnexapp.teamnex.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    private String lang;
    private OnChangeLanguage onChangeLanguage;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onChangeLanguage = (OnChangeLanguage) getParentFragment();
    }

    public static BottomSheetDialog newInstance(String lang) {
        BottomSheetDialog dialog = new BottomSheetDialog();
        Bundle args = new Bundle();
        args.putString("lang", lang);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lang = getArguments().getString("lang");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_bottom_sheet_dialog, container, false);

        ConstraintLayout russian = view.findViewById(R.id.languageRussian);
        ConstraintLayout english = view.findViewById(R.id.languageEnglish);
        ImageView russianCheck = view.findViewById(R.id.languageRussianCheck);
        ImageView englishCheck = view.findViewById(R.id.languageEnglishCheck);

        if (lang.equals("ru")){
            englishCheck.setVisibility(View.INVISIBLE);
            english.setOnClickListener(v -> {
                onChangeLanguage.onChangeLanguage("en");
                dismiss();
            });
        } else {
            russianCheck.setVisibility(View.INVISIBLE);
            russian.setOnClickListener(v -> {
                onChangeLanguage.onChangeLanguage("ru");
                dismiss();
            });
        }

        return view;
    }
}
