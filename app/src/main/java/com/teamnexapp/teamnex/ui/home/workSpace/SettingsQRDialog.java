package com.teamnexapp.teamnex.ui.home.workSpace;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.teamnexapp.teamnex.R;

public class SettingsQRDialog extends DialogFragment {
    private String boardCode;

    public static SettingsQRDialog newInstance(String boardCode) {
        SettingsQRDialog dialog = new SettingsQRDialog();
        Bundle args = new Bundle();
        args.putString("boardCode", boardCode);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            boardCode = getArguments().getString("boardCode");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_board_settings_qr, null);
        builder.setView(dialogView);

        ImageView qrCodeImageView = dialogView.findViewById(R.id.imageView19);

        Bitmap qrCodeBitmap = generateQRCode(boardCode);
        if (qrCodeBitmap != null) {
            qrCodeImageView.setImageBitmap(qrCodeBitmap);
        }

        return builder.create();
    }

    Bitmap generateQRCode(String text) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 600, 600);
            return barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}