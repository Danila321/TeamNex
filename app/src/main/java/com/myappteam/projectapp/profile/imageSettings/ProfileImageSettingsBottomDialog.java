package com.myappteam.projectapp.profile.imageSettings;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.myappteam.projectapp.R;

public class ProfileImageSettingsBottomDialog extends BottomSheetDialogFragment {
    private String imageUri;
    private OnChangeImage onChangeImage;

    public interface OnChangeImage {
        void onChangeImage();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onChangeImage = (OnChangeImage) context;
    }

    public static ProfileImageSettingsBottomDialog newInstance(Uri imageUri) {
        ProfileImageSettingsBottomDialog dialog = new ProfileImageSettingsBottomDialog();
        Bundle args = new Bundle();
        args.putString("imageUri", String.valueOf(imageUri));
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUri = getArguments().getString("imageUri");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_apply_image_bottom_dialog, container, false);

        ImageView photoImageView = view.findViewById(R.id.applyImagePhoto);
        Button buttonCancel = view.findViewById(R.id.applyImageCancel);
        Button buttonSave = view.findViewById(R.id.applyImageSave);

        photoImageView.setImageURI(Uri.parse(imageUri));

        buttonCancel.setOnClickListener(view1 -> dismiss());

        buttonSave.setOnClickListener(view2 -> {
            onChangeImage.onChangeImage();
            dismiss();
        });

        return view;
    }
}
