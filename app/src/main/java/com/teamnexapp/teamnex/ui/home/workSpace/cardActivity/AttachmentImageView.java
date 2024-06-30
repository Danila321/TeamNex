package com.teamnexapp.teamnex.ui.home.workSpace.cardActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
        String imageName = intent.getStringExtra("fileName");

        ImageButton close = findViewById(R.id.AttachmentImageCloseButton);
        close.setOnClickListener(v -> finish());

        TextView name = findViewById(R.id.AttachmentImageName);
        name.setText(imageName);

        ImageView imageView = findViewById(R.id.AttachmentViewImage);
        Picasso.get().load(imageUri).into(imageView);
    }
}