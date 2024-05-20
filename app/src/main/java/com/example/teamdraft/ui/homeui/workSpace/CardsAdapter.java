package com.example.teamdraft.ui.homeui.workSpace;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import com.example.teamdraft.R;
import com.example.teamdraft.ui.homeui.workSpace.cardActivity.CardActivity;
import com.example.teamdraft.ui.homeui.workSpace.dialogs.card.EditCardDialog;

import java.util.ArrayList;

public class CardsAdapter extends ArrayAdapter<Card> {
    private final Context context;
    private final String boardId;
    private final String itemId;
    FragmentManager fragmentManager;

    public CardsAdapter(@NonNull Context context, ArrayList<Card> cards, FragmentManager fragmentManager, String boardId, String itemId) {
        super(context, R.layout.workspace_custom_card, cards);
        this.context = context;
        this.boardId = boardId;
        this.itemId = itemId;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.workspace_custom_card, null);
        }

        Card card = getItem(position);
        if (card != null) {
            TextView cardNameText = convertView.findViewById(R.id.CardNameView);
            cardNameText.setText(card.getName());

            ConstraintLayout cardLayout = convertView.findViewById(R.id.CardLayout);
            cardLayout.setOnClickListener(v -> {
                Intent intent = new Intent(context, CardActivity.class);
                intent.putExtra("boardId", boardId);
                intent.putExtra("itemId", itemId);
                intent.putExtra("cardId", card.getId());
                context.startActivity(intent);
            });

            ImageButton editName = convertView.findViewById(R.id.CardEditNameButton);
            editName.setOnClickListener(v -> {
                EditCardDialog dialog = EditCardDialog.newInstance(boardId, itemId, card.getId(), card.getName());
                dialog.show(fragmentManager, "editCardName");
            });
        }

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
