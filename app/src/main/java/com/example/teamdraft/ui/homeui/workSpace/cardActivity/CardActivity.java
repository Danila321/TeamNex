package com.example.teamdraft.ui.homeui.workSpace.cardActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.teamdraft.R;

import java.util.ArrayList;
import java.util.List;

public class CardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        RecyclerView horizontalRecyclerView = findViewById(R.id.horizontalRecyclerView);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        horizontalRecyclerView.setLayoutManager(horizontalLayoutManager);

        // Создаем список элементов для RecyclerView
        List<String> data = new ArrayList<>();
        data.add("Данила");
        data.add("Виктор");
        data.add("Андрей");
        // Создаем адаптер
        CardUsersAdapter adapter = new CardUsersAdapter(data);
        // Устанавливаем адаптер для RecyclerView
        horizontalRecyclerView.setAdapter(adapter);
    }
}