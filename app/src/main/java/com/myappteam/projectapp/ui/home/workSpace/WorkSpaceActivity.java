package com.myappteam.projectapp.ui.home.workSpace;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;

import com.myappteam.projectapp.BaseActivity;
import com.myappteam.projectapp.GetUserRole;
import com.myappteam.projectapp.R;
import com.myappteam.projectapp.ui.home.workSpace.dialogs.item.AddItemDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myappteam.projectapp.ui.home.workSpace.settingsActivity.SettingsActivity;
import com.myappteam.projectapp.ui.home.workSpace.usersActivity.UsersActivity;

import java.util.ArrayList;

public class WorkSpaceActivity extends BaseActivity {
    ArrayList<Item> items = new ArrayList<>();
    ItemsAdapter adapter;
    String boardIdData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspace);

        Bundle data = getIntent().getExtras();
        if (data != null) {
            boardIdData = data.getString("boardId");
        }

        ImageButton back = findViewById(R.id.imageButtonBack);
        ImageButton users = findViewById(R.id.imageButtonUsers);
        ImageButton settings = findViewById(R.id.imageButtonSettings);

        back.setOnClickListener(view -> finish());

        users.setOnClickListener(v -> {
            Intent intent = new Intent(WorkSpaceActivity.this, UsersActivity.class);
            intent.putExtra("boardId", boardIdData);
            startActivity(intent);
        });

        settings.setOnClickListener(view -> {
            Intent intent = new Intent(WorkSpaceActivity.this, SettingsActivity.class);
            intent.putExtra("boardId", boardIdData);
            startActivity(intent);
        });

        GetUserRole.getUserRole(boardIdData, this::roleManager);
    }

    void roleManager(String role) {
        RecyclerView list = findViewById(R.id.itemsListView);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        horizontalLayoutManager.findFirstVisibleItemPosition();
        list.setLayoutManager(horizontalLayoutManager);

        list.addItemDecoration(new DotsIndicatorDecoration(this));

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(list);

        list.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                list.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                //Получаем высоту горизонтального списка для ограничения внутреннего списка
                int listViewHeight = list.getHeight();

                Button add = findViewById(R.id.buttonAddItem);
                switch (role) {
                    case "owner":
                    case "admin":
                        //Кнопка добавления пункта
                        add.setOnClickListener(view -> {
                            AddItemDialog dialog = AddItemDialog.newInstance(boardIdData);
                            dialog.show(getSupportFragmentManager(), "addItem");
                        });
                        //Настраиваем адаптер и listView
                        adapter = new ItemsAdapter(WorkSpaceActivity.this, items, boardIdData, getSupportFragmentManager(), true, listViewHeight);
                        break;
                    case "user":
                        //Настраиваем адаптер и listView
                        adapter = new ItemsAdapter(WorkSpaceActivity.this, items, boardIdData, getSupportFragmentManager(), false, listViewHeight);
                        //Убираем кнопку добавления
                        add.setVisibility(View.GONE);
                        break;
                }
                list.setAdapter(adapter);

                //Получаем данные из БД
                updateItems();
            }
        });
    }

    public void updateItems() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("boards").child(boardIdData).child("items").addValueEventListener(new ValueEventListener() {
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
}