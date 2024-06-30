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

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.teamnexapp.teamnex.R;

public class BottomSheetDialogTheme extends BottomSheetDialogFragment {
    private boolean theme;
    private OnChangeSettings onChangeSettings;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onChangeSettings = (OnChangeSettings) getParentFragment();
    }

    public static BottomSheetDialogTheme newInstance(boolean theme) {
        BottomSheetDialogTheme dialog = new BottomSheetDialogTheme();
        Bundle args = new Bundle();
        args.putBoolean("theme", theme);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            theme = getArguments().getBoolean("theme");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_bottom_sheet_dialog_theme, container, false);

        ConstraintLayout light = view.findViewById(R.id.themeLight);
        ConstraintLayout dark = view.findViewById(R.id.themeDark);
        ImageView lightCheck = view.findViewById(R.id.themeLightCheck);
        ImageView darkCheck = view.findViewById(R.id.themeDarkCheck);

        if (theme) {
            darkCheck.setVisibility(View.INVISIBLE);
            dark.setOnClickListener(v -> {
                onChangeSettings.onChangeTheme(false);
                dismiss();
            });
        } else {
            lightCheck.setVisibility(View.INVISIBLE);
            light.setOnClickListener(v -> {
                onChangeSettings.onChangeTheme(true);
                dismiss();
            });
        }

        return view;
    }
}
