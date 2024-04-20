package com.example.teamdraft.ui.homeui.workSpace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.teamdraft.R;
import com.example.teamdraft.ui.homeui.workSpace.dialogs.item.AddItemDialog;
import com.example.teamdraft.ui.homeui.workSpace.dialogs.item.OnChange;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WorkSpaceActivity extends AppCompatActivity implements OnChange {
    ArrayList<Item> items = new ArrayList<>();
    ItemsAdapter adapter;
    String boardIdData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_space);

        Bundle data = getIntent().getExtras();
        if (data != null) {
            boardIdData = data.getString("boardId");
        }

        ImageButton back = findViewById(R.id.imageButtonBack);
        ImageButton notifications = findViewById(R.id.imageButtonNotifications);
        ImageButton users = findViewById(R.id.imageButtonUsers);
        ImageButton settings = findViewById(R.id.imageButtonSettings);
        ListView list = findViewById(R.id.itemsListView);
        Button add = findViewById(R.id.buttonAddItem);

        back.setOnClickListener(view -> finish());

        settings.setOnClickListener(view -> {
            Intent intent = new Intent(WorkSpaceActivity.this, SettingsActivity.class);
            intent.putExtra("boardId", boardIdData);
            startActivity(intent);
        });

        //Кнопка добавления пункта
        add.setOnClickListener(view -> {
            AddItemDialog dialog = AddItemDialog.newInstance(boardIdData);
            dialog.show(getSupportFragmentManager(), "addItem");
        });

        //Настраиваем адаптер и listView
        adapter = new ItemsAdapter(WorkSpaceActivity.this, items, boardIdData, getSupportFragmentManager());
        list.setAdapter(adapter);

        //Получаем данные из БД
        updateItems();
    }

    public void updateItems() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("boards").child(boardIdData).child("items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();

                //Добавляем названия пунктов в список
                for (DataSnapshot cardSnapshot : snapshot.getChildren()) {
                    Item item = cardSnapshot.getValue(Item.class);
                    items.add(item);
                }

                //Обновляем ListView
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onChange() {
        updateItems();
    }
}