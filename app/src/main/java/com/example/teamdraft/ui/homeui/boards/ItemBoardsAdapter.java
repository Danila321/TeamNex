package com.example.teamdraft.ui.homeui.boards;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.teamdraft.R;

import java.util.ArrayList;

public class ItemBoardsAdapter extends ArrayAdapter<Board> {

    public ItemBoardsAdapter(@NonNull Context context, ArrayList<Board> items) {
        super(context, R.layout.boards_custom_item, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.boards_custom_item, null);
        }
        Board board = getItem(position);

        TextView nameTextView = convertView.findViewById(R.id.boardItemName);

        if (board != null) {
            nameTextView.setText(board.getName());
        }

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}