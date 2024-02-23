package com.example.teamdraft.ui.homeui.workSpace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.teamdraft.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ItemsAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String id;
    FragmentManager fragmentManager;

    public ItemsAdapter(@NonNull Context context, ArrayList<String> items, String id, FragmentManager fragmentManager) {
        super(context, R.layout.workspace_custom_item, items);
        this.context = context;
        this.id = id;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.workspace_custom_item, null);
        }

        String itemName = getItem(position);

        TextView itemNameView = convertView.findViewById(R.id.itemName);
        itemNameView.setText(itemName);

        //Настраиваем ListView карточек
        ArrayList<Card> cards = new ArrayList<>();
        ListView listView = convertView.findViewById(R.id.cardsListView);
        CardsAdapter adapter = new CardsAdapter(context, cards);
        listView.setAdapter(adapter);

        //Получаем данные карточек
        updateCards(itemName, adapter, cards);

        Button addCard = convertView.findViewById(R.id.buttonAddCard);
        addCard.setOnClickListener(view -> {
            AddItemCardDialog dialog = AddItemCardDialog.newInstance(id, itemName);
            dialog.show(fragmentManager, "addCard");
        });

        ImageButton edit = convertView.findViewById(R.id.imageButtonEditCard);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ImageButton delete = convertView.findViewById(R.id.imageButtonDeleteCard);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }

    private void updateCards(String itemName, CardsAdapter adapter, ArrayList<Card> cards) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("boards").child(id).child("items").child(itemName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Добавляем названия карточек в список
                for (DataSnapshot cardSnapshot : snapshot.getChildren()) {
                    Card card = cardSnapshot.getValue(Card.class);
                    cards.add(card);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
