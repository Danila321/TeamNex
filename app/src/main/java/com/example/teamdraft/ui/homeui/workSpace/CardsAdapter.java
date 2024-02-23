package com.example.teamdraft.ui.homeui.workSpace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.teamdraft.R;

import java.util.ArrayList;

public class CardsAdapter extends ArrayAdapter<Card> {
    private final Context context;

    public CardsAdapter(@NonNull Context context, ArrayList<Card> items) {
        super(context, R.layout.workspace_custom_card, items);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.workspace_custom_card, null);
        }

        Card card = getItem(position);
        if (card != null){
            TextView cardName = convertView.findViewById(R.id.CardNameView);
            cardName.setText(card.getName());
        }

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
