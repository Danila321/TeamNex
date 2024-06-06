package com.example.teamdraft.ui.home.workSpace;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.example.teamdraft.R;
import com.example.teamdraft.ui.home.workSpace.cardActivity.CardActivity;
import com.example.teamdraft.ui.home.workSpace.cardActivity.checkList.ItemChecklist;
import com.example.teamdraft.ui.home.workSpace.dialogs.card.EditCardDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class CardsAdapter extends ArrayAdapter<Card> {
    private final Context context;
    private final String boardId;
    private final String itemId;
    FragmentManager fragmentManager;
    boolean isAdmin;

    public CardsAdapter(@NonNull Context context, ArrayList<Card> cards, FragmentManager fragmentManager, String boardId, String itemId, boolean isAdmin) {
        super(context, R.layout.workspace_custom_card, cards);
        this.context = context;
        this.boardId = boardId;
        this.itemId = itemId;
        this.fragmentManager = fragmentManager;
        this.isAdmin = isAdmin;
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

            getChecklistData(convertView, card.getId());
            getAttachmentsCount(convertView, card.getId());
            getUsersCount(convertView, card.getId());

            if (isAdmin){
                ImageButton editName = convertView.findViewById(R.id.CardEditNameButton);
                editName.setVisibility(View.VISIBLE);
                editName.setOnClickListener(v -> {
                    EditCardDialog dialog = EditCardDialog.newInstance(boardId, itemId, card.getId(), card.getName());
                    dialog.show(fragmentManager, "editCardName");
                });
            }
        }

        return convertView;
    }

    void getChecklistData(View convertView, String cardId) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("boards").child(boardId).child("items").child(itemId).child("cards").child(cardId).child("checklist").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ConstraintLayout checklistLayout = convertView.findViewById(R.id.cardChecklistLayout);
                if (snapshot.exists()) {
                    checklistLayout.setVisibility(View.VISIBLE);
                    TextView checklistText = convertView.findViewById(R.id.cardChecklistText);
                    LocalDate date = LocalDate.MAX;
                    String dateText = "";
                    int checkedItems = 0;
                    int allItems = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ItemChecklist item = dataSnapshot.getValue(ItemChecklist.class);
                        if (item.getChecked()) {
                            checkedItems++;
                        } else {
                            if (!item.getDate().isEmpty()) {
                                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                LocalDate itemDate = LocalDate.parse(item.getDate(), inputFormatter);
                                if (date.isAfter(itemDate)) {
                                    date = itemDate;
                                }
                            }
                        }
                        allItems++;
                    }
                    if (checkedItems == allItems) {
                        checklistLayout.setBackgroundResource(R.drawable.workspace_item_date_green_8dp);
                        checklistText.setTextColor(ContextCompat.getColor(context, R.color.white));
                        ImageView image = convertView.findViewById(R.id.cardChecklistImage);
                        Picasso.get().load(R.drawable.checklist_white).into(image);
                    } else if (date != LocalDate.MAX) {
                        //Проверяем наличие даты
                        if (date.isAfter(LocalDate.now())) {
                            checklistLayout.setBackgroundResource(R.drawable.workspace_item_date_gray_8dp);
                        } else if (date.isEqual(LocalDate.now())) {
                            checklistLayout.setBackgroundResource(R.drawable.workspace_item_date_yellow_8dp);
                        } else if (date.isBefore(LocalDate.now())) {
                            checklistLayout.setBackgroundResource(R.drawable.workspace_item_date_red_8dp);
                        }
                        DateTimeFormatter outputFormatter;
                        if (LocalDate.now().getYear() == date.getYear()) {
                            outputFormatter = DateTimeFormatter.ofPattern("d MMM", new Locale("ru"));
                        } else {
                            outputFormatter = DateTimeFormatter.ofPattern("d MMM, yyyy", new Locale("ru"));
                        }
                        dateText = "  • " + date.format(outputFormatter);
                    }
                    checklistText.setText(checkedItems + "/" + allItems + dateText);
                } else {
                    checklistLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void getAttachmentsCount(View convertView, String cardId) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("boards").child(boardId).child("items").child(itemId).child("cards").child(cardId).child("attachments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int items = (int) snapshot.getChildrenCount();
                    ConstraintLayout attachmentsLayout = convertView.findViewById(R.id.cardAttachmentsLayout);
                    attachmentsLayout.setVisibility(View.VISIBLE);
                    TextView attachmentsText = convertView.findViewById(R.id.cardAttachmentsText);
                    attachmentsText.setText(String.valueOf(items));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void getUsersCount(View convertView, String cardId){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("boards").child(boardId).child("items").child(itemId).child("cards").child(cardId).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int items = (int) snapshot.getChildrenCount();
                    ConstraintLayout usersLayout = convertView.findViewById(R.id.cardUsersLayout);
                    usersLayout.setVisibility(View.VISIBLE);
                    TextView usersText = convertView.findViewById(R.id.cardUsersText);
                    usersText.setText(String.valueOf(items));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
