package com.teamnexapp.teamnex;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class LoadingDialog {
    private AlertDialog dialog;
    private final Activity activity;
    private final String text;

    public LoadingDialog(Activity activity, String text) {
        this.activity = activity;
        this.text = text;
    }

    @SuppressLint("InflateParams")
    public void startDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_loading, null);
        builder.setView(dialogView);
        builder.setCancelable(false);

        TextView textView = dialogView.findViewById(R.id.dialogLoadingText);
        textView.setText(text);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        int pixelsWidth = activity.getResources().getDimensionPixelSize(R.dimen.dialog_loading_width);
        dialog.getWindow().setLayout(pixelsWidth, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void dismissDialog() {
        dialog.dismiss();
    }
}
