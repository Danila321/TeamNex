package com.example.teamdraft.ui.homeui.boards;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.teamdraft.R;
import com.example.teamdraft.ui.homeui.workSpace.WorkSpaceActivity;

import java.util.ArrayList;

public class ItemBoardsAdapter extends ArrayAdapter<Board> {
    private final Context context;

    public ItemBoardsAdapter(@NonNull Context context, ArrayList<Board> items) {
        super(context, R.layout.boards_custom_item, items);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.boards_custom_item, null);
        }
        Board board = getItem(position);

        ConstraintLayout boardItem = convertView.findViewById(R.id.boardItem);
        TextView nameTextView = convertView.findViewById(R.id.boardItemName);

        boardItem.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), WorkSpaceActivity.class);
            context.startActivity(intent);
        });
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