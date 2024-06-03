package com.example.teamdraft.ui.home.workSpace.cardActivity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.teamdraft.R;
import com.example.teamdraft.ui.home.workSpace.Card;
import com.example.teamdraft.ui.home.workSpace.cardActivity.users.User;
import com.example.teamdraft.ui.home.workSpace.cardActivity.attachments.AddAttachmentDialog;
import com.example.teamdraft.ui.home.workSpace.cardActivity.attachments.ItemAttachment;
import com.example.teamdraft.ui.home.workSpace.cardActivity.attachments.ItemAttachmentsAdapter;
import com.example.teamdraft.ui.home.workSpace.cardActivity.checkList.AddChecklistItemDialog;
import com.example.teamdraft.ui.home.workSpace.cardActivity.checkList.ItemChecklist;
import com.example.teamdraft.ui.home.workSpace.cardActivity.checkList.ItemChecklistAdapter;
import com.example.teamdraft.ui.home.workSpace.cardActivity.users.AddUserDialog;
import com.example.teamdraft.ui.home.workSpace.cardActivity.users.CardUsersAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CardActivity extends AppCompatActivity {
    String boardId, itemId, cardId;
    String descriptionText;
    ListView checkList, attachmentsList;
    ArrayList<User> users = new ArrayList<>();
    ArrayList<ItemChecklist> checkListItems = new ArrayList<>();
    ArrayList<ItemAttachment> attachmentItems = new ArrayList<>();
    CardUsersAdapter adapter;
    ItemChecklistAdapter checkListAdapter;
    ItemAttachmentsAdapter attachmentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        Bundle data = getIntent().getExtras();
        if (data != null) {
            boardId = data.getString("boardId");
            itemId = data.getString("itemId");
            cardId = data.getString("cardId");
        }

        //Получаем все данные
        getData();
        getUsers();
        getChecklistItems();
        updateChecklistIndicators();
        getAttachmentItems();

        //Настраиваем listView пользователей
        RecyclerView horizontalRecyclerView = findViewById(R.id.horizontalRecyclerView);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        horizontalRecyclerView.setLayoutManager(horizontalLayoutManager);
        adapter = new CardUsersAdapter(users);
        horizontalRecyclerView.setAdapter(adapter);

        //Кнопка добавления пользователей
        ImageButton addUserButton = findViewById(R.id.cardButtonAddUser);
        addUserButton.setOnClickListener(v -> {
            AddUserDialog dialog = AddUserDialog.newInstance(boardId, itemId, cardId);
            dialog.show(getSupportFragmentManager(), "addUser");
        });

        //Кнопка изменения описания
        Button editDescription = findViewById(R.id.buttonEditDescription);
        editDescription.setOnClickListener(v -> {
            EditDescriptionDialog dialog = EditDescriptionDialog.newInstance(boardId, itemId, cardId, descriptionText);
            dialog.show(getSupportFragmentManager(), "editDescription");
        });

        //Настраиваем listView чеклиста
        ArrayList<String> boardData = new ArrayList<>();
        boardData.add(boardId);
        boardData.add(itemId);
        boardData.add(cardId);
        checkList = findViewById(R.id.checkList);
        checkListAdapter = new ItemChecklistAdapter(this, checkListItems, getSupportFragmentManager(), boardData);
        checkList.setAdapter(checkListAdapter);

        Button addChecklistItem = findViewById(R.id.addCheckListItem);
        addChecklistItem.setOnClickListener(v -> {
            AddChecklistItemDialog dialog = AddChecklistItemDialog.newInstance(boardId, itemId, cardId);
            dialog.show(getSupportFragmentManager(), "addChecklistItem");
        });

        //Настраиваем listView вложений
        attachmentsList = findViewById(R.id.attachmentsList);
        attachmentsAdapter = new ItemAttachmentsAdapter(this, attachmentItems);
        attachmentsList.setAdapter(attachmentsAdapter);

        Button addAttachmentItem = findViewById(R.id.addAttachment);
        addAttachmentItem.setOnClickListener(v -> {
            AddAttachmentDialog dialog = AddAttachmentDialog.newInstance(boardId, itemId, cardId);
            dialog.show(getSupportFragmentManager(), "addAttachment");
        });
    }

    void getData() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("boards").child(boardId).child("items").child(itemId).child("cards").child(cardId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Card card = snapshot.getValue(Card.class);
                TextView cardTitle = findViewById(R.id.cardTitle);
                TextView description = findViewById(R.id.descriptionText);
                //Получаем название
                cardTitle.setText(card.getName());
                //Получаем описание
                descriptionText = card.getDescription();
                if (descriptionText.isEmpty()) {
                    description.setText("Добавьте описание");
                } else {
                    description.setText(descriptionText);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void getUsers() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("boards").child(boardId).child("items").child(itemId).child("cards").child(cardId).child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> usersId = new ArrayList<>();
                users.clear();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    usersId.add(userSnapshot.getKey());
                }

                mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            if (usersId.contains(userSnapshot.getKey())) {
                                users.add(userSnapshot.getValue(User.class));
                            }
                        }

                        //Обновляем адаптер
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void getChecklistItems() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("boards").child(boardId).child("items").child(itemId).child("cards").child(cardId).child("checklist").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                checkListItems.clear();

                for (DataSnapshot cardSnapshot : snapshot.getChildren()) {
                    ItemChecklist item = cardSnapshot.getValue(ItemChecklist.class);
                    checkListItems.add(item);
                }

                //Обновляем адаптер
                checkListAdapter.notifyDataSetChanged();

                //Устанавливаем высоту listView
                checkList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onGlobalLayout() {
                        checkList.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        int height = 0;
                        for (int i = 0; i < checkListAdapter.getCount(); i++) {
                            View itemView = checkListAdapter.getView(i, null, checkList);
                            itemView.measure(
                                    View.MeasureSpec.makeMeasureSpec(checkList.getWidth(), View.MeasureSpec.EXACTLY),
                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                            );
                            height += itemView.getMeasuredHeight();
                        }
                        ViewGroup.LayoutParams params = checkList.getLayoutParams();
                        params.height = height;
                        checkList.setLayoutParams(params);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void updateChecklistIndicators() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("boards").child(boardId).child("items").child(itemId).child("cards").child(cardId).child("checklist").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int completedItemsCount = 0;
                for (DataSnapshot cardSnapshot : snapshot.getChildren()) {
                    ItemChecklist item = cardSnapshot.getValue(ItemChecklist.class);
                    if (item.getChecked()) {
                        completedItemsCount++;
                    }
                }

                //Обновляем индикаторы чеклиста
                TextView percentTextView = findViewById(R.id.checklistPercentText);
                ProgressBar progressBar = findViewById(R.id.checklistProgressBar);
                int percent = (int) (((float) completedItemsCount / checkListItems.size()) * 100);
                percentTextView.setText(percent + "%");
                ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), percent);
                progressAnimator.setDuration(900);
                progressAnimator.start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void getAttachmentItems() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("boards").child(boardId).child("items").child(itemId).child("cards").child(cardId).child("attachments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                attachmentItems.clear();

                for (DataSnapshot cardSnapshot : snapshot.getChildren()) {
                    ItemAttachment item = cardSnapshot.getValue(ItemAttachment.class);
                    attachmentItems.add(item);
                }

                //Обновляем адаптер
                attachmentsAdapter.notifyDataSetChanged();

                //Устанавливаем высоту listView
                attachmentsList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onGlobalLayout() {
                        attachmentsList.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        int height = 0;
                        for (int i = 0; i < attachmentsAdapter.getCount(); i++) {
                            View itemView = attachmentsAdapter.getView(i, null, attachmentsList);
                            itemView.measure(
                                    View.MeasureSpec.makeMeasureSpec(attachmentsList.getWidth(), View.MeasureSpec.EXACTLY),
                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                            );
                            height += itemView.getMeasuredHeight();
                        }
                        ViewGroup.LayoutParams params = attachmentsList.getLayoutParams();
                        params.height = height;
                        attachmentsList.setLayoutParams(params);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}