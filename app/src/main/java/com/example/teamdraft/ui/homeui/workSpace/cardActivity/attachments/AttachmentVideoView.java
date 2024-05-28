package com.example.teamdraft.ui.homeui.workSpace.cardActivity.attachments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.VideoView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.teamdraft.R;

public class AttachmentVideoView extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workspace_card_attachment_view_video);

        Intent intent = getIntent();
        String videoUri = intent.getStringExtra("fileUri");

        VideoView videoView = findViewById(R.id.AttachmentViewVideo);
        videoView.setVideoURI(Uri.parse(videoUri));

        CustomMediaController mediaController = new CustomMediaController(this);
        mediaController.setAnchorView(findViewById(R.id.AttachmentControllerLayout));

        mediaController.addOnUnhandledKeyEventListener((v, event) -> {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
            {
                mediaController.hide();
                finish();
            }
            return true;
        });

        videoView.setMediaController(mediaController);

        videoView.start();
    }
}