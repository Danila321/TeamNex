package com.teamnexapp.teamnex.ui.home.workSpace.cardActivity.attachments;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.teamnexapp.teamnex.R;
import com.squareup.picasso.Picasso;

public class AttachmentImageView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workspace_card_attachment_view_image);

        Intent intent = getIntent();
        String imageUri = intent.getStringExtra("fileUri");

        ImageView imageView = findViewById(R.id.AttachmentViewImage);
        Picasso.get().load(imageUri).into(imageView);
    }
}