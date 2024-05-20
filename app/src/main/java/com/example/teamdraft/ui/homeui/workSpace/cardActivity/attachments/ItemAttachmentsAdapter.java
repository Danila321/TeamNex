package com.example.teamdraft.ui.homeui.workSpace.cardActivity.attachments;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.teamdraft.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemAttachmentsAdapter extends ArrayAdapter<ItemAttachment> {
    public ItemAttachmentsAdapter(@NonNull Context context, ArrayList<ItemAttachment> attachments) {
        super(context, R.layout.workspace_card_attachments_item, attachments);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.workspace_card_attachments_item, null);
        }

        ItemAttachment item = getItem(position);

        ImageView image = convertView.findViewById(R.id.AttachmentItemImage);
        Picasso.get().load(item.getImage()).into(image);

        TextView name = convertView.findViewById(R.id.AttachmentItemName);
        name.setText(item.getName());

        return convertView;
    }
}
