package com.teamnexapp.teamnex.ui.home.workSpace;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teamnexapp.teamnex.R;
import com.teamnexapp.teamnex.ui.home.workSpace.dialogs.card.AddCardDialog;
import com.teamnexapp.teamnex.ui.home.workSpace.dialogs.item.DeleteItemDialog;
import com.teamnexapp.teamnex.ui.home.workSpace.dialogs.item.EditItemDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
    private final ArrayList<Item> items;
    private final Context context;
    private final String boardId;
    private final int listViewHeight;
    FragmentManager fragmentManager;
    boolean isAdmin;

    public ItemsAdapter(Context context, ArrayList<Item> items, String boardId, FragmentManager fragmentManager, boolean isAdmin, int listViewHeight) {
        this.context = context;
        this.items = items;
        this.boardId = boardId;
        this.fragmentManager = fragmentManager;
        this.isAdmin = isAdmin;
        this.listViewHeight = listViewHeight;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.workspace_custom_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);

        holder.itemNameView.setText(item.getName());

        ArrayList<Card> cards = new ArrayList<>();
        CardsAdapter adapter = new CardsAdapter(context, cards, fragmentManager, boardId, item.getId(), isAdmin);
        holder.cardsListView.setAdapter(adapter);
        updateCards(item.getId(), adapter, holder.cardsListView, cards);

        if (isAdmin) {
            //Кнопка добавления карточки
            holder.addCard.setOnClickListener(view -> {
                AddCardDialog dialog = AddCardDialog.newInstance(boardId, item.getId());
                dialog.show(fragmentManager, "addCard");
            });

            //Кнопка изменения названия
            holder.edit.setOnClickListener(v -> {
                EditItemDialog dialog = EditItemDialog.newInstance(boardId, item.getId(), item.getName());
                dialog.show(fragmentManager, "editItemName");
            });

            //Кнопка удаления
            holder.delete.setOnClickListener(v -> {
                DeleteItemDialog dialog = DeleteItemDialog.newInstance(boardId, item.getId());
                dialog.show(fragmentManager, "deleteItem");
            });
        } else {
            holder.addCard.setVisibility(View.GONE);
            holder.edit.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameView;
        ListView cardsListView;
        Button addCard;
        ImageButton edit, delete;

        public ViewHolder(View view) {
            super(view);
            itemNameView = view.findViewById(R.id.itemName);
            cardsListView = view.findViewById(R.id.cardsListView);
            addCard = view.findViewById(R.id.buttonAddCard);
            edit = view.findViewById(R.id.imageButtonEditCard);
            delete = view.findViewById(R.id.imageButtonDeleteCard);
        }
    }

    private void updateCards(String itemId, CardsAdapter adapter, ListView list, ArrayList<Card> cards) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("boards").child(boardId).child("items").child(itemId).child("cards").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Добавляем названия карточек в список
                for (DataSnapshot cardSnapshot : snapshot.getChildren()) {
                    Card card = cardSnapshot.getValue(Card.class);
                    cards.add(card);
                }

                adapter.notifyDataSetChanged();

                //Устанавливаем высоту listView
                list.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onGlobalLayout() {
                        list.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int height = 0;
                        for (int i = 0; i < adapter.getCount(); i++) {
                            View itemView = adapter.getView(i, null, list);
                            itemView.measure(
                                    View.MeasureSpec.makeMeasureSpec(list.getWidth(), View.MeasureSpec.EXACTLY),
                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                            );
                            height += itemView.getMeasuredHeight();
                        }
                        //Коэффициент ограничения
                        int heightRestriction = Math.round(context.getResources().getDisplayMetrics().density * 170);
                        if (height > listViewHeight - heightRestriction) {
                            height = listViewHeight - heightRestriction;
                        }
                        ViewGroup.LayoutParams params = list.getLayoutParams();
                        params.height = height;
                        list.setLayoutParams(params);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
