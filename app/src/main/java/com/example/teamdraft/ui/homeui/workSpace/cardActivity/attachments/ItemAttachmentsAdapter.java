package com.example.teamdraft.ui.homeui.workSpace.cardActivity.attachments;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.teamdraft.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemAttachmentsAdapter extends ArrayAdapter<ItemAttachment> {
    Context context;

    public ItemAttachmentsAdapter(@NonNull Context context, ArrayList<ItemAttachment> attachments) {
        super(context, R.layout.workspace_card_attachments_item, attachments);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.workspace_card_attachments_item, null);
        }

        ItemAttachment item = getItem(position);

        ImageView imageView = convertView.findViewById(R.id.attachmentItemImage);
        ImageView imageType = convertView.findViewById(R.id.attachmentItemImageType);

        TextView fileTypeTextView = convertView.findViewById(R.id.attachmentFileTypeTextView);
        String fileUri = item.getFile();
        String fileType = item.getFileType();
        if (fileType.equals("png") || fileType.equals("jpg") || fileType.equals("jpeg") || fileType.equals("webp")) {
            Picasso.get().load(fileUri).into(imageView);
            Picasso.get().load(R.drawable.picture).into(imageType);
            //Показываем тип файла
            fileTypeTextView.setText("Фото");
            //Настраиваем открытие фотографии
            imageView.setOnClickListener(v -> {
                Intent intent = new Intent(context, AttachmentImageView.class);
                intent.putExtra("fileUri", fileUri);
                context.startActivity(intent);
            });
        } else if (fileType.equals("mp4") || fileType.equals("mkv") || fileType.equals("avi")) {
            Glide.with(context)
                    .setDefaultRequestOptions(new RequestOptions().frame(0).fitCenter())
                    .load(fileUri)
                    .into(imageView);
            Picasso.get().load(R.drawable.video).into(imageType);
            //Показываем тип файла
            fileTypeTextView.setText("Видео");
            //Настраиваем открытие видео
            imageView.setOnClickListener(v -> {
                Intent intent = new Intent(context, AttachmentVideoView.class);
                intent.putExtra("fileUri", fileUri);
                context.startActivity(intent);
            });
        } else {
            ConstraintLayout grayLayout = convertView.findViewById(R.id.attachmentItemBackground);
            grayLayout.setVisibility(View.VISIBLE);
            TextView typeTextView = convertView.findViewById(R.id.attachmentItemBackgroundType);
            typeTextView.setVisibility(View.VISIBLE);
            typeTextView.setText(fileType);
            //Показываем тип файла
            fileTypeTextView.setText("Файл");
        }

        TextView name = convertView.findViewById(R.id.AttachmentItemName);
        name.setText(item.getName());

        return convertView;
    }
}
