package com.teamnexapp.teamnex.ui.home.workSpace.cardActivity.users;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.teamnexapp.teamnex.R;
import java.util.ArrayList;

public class CardUsersAdapter extends RecyclerView.Adapter<CardUsersAdapter.ViewHolder> {
    private final ArrayList<User> users;
    private Context context;

    public CardUsersAdapter(ArrayList<User> data, Context context) {
        this.context = context;
        users = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workspace_card_member_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = users.get(position);
        holder.name.setText(user.getName());
        Glide.with(context).load(Uri.parse(user.getPhoto())).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewUserName);
            image = itemView.findViewById(R.id.cardUsersImageView);
        }
    }
}