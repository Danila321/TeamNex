package com.example.teamdraft.ui.homeui.boards;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.teamdraft.R;
import com.example.teamdraft.ui.homeui.workSpace.BoardSettingsActivity;
import com.example.teamdraft.ui.homeui.workSpace.WorkSpaceActivity;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        ImageView boardImage = convertView.findViewById(R.id.boardImageView);
        TextView nameTextView = convertView.findViewById(R.id.boardItemName);
        TextView dateTextView = convertView.findViewById(R.id.boardDate);
        ImageButton boardSettings = convertView.findViewById(R.id.boardSettingsButton);

        if (board != null) {
            Picasso.get().load(board.getImageUri()).into(boardImage);
            nameTextView.setText(board.getName());
            dateTextView.setText(getPassedTime(board.getEditDate()));
        }

        boardItem.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), WorkSpaceActivity.class);
            context.startActivity(intent);
        });

        boardSettings.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), BoardSettingsActivity.class);
            intent.putExtra("boardId", board.getId());
            context.startActivity(intent);
        });

        return convertView;
    }

    @SuppressLint("SimpleDateFormat")
    public String getPassedTime(String dbDateText) {
        Date dbDateTime;
        try {
            dbDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dbDateText);
        } catch (ParseException e) {
            return "неизвестно";
        }

        //Вычисляем разницу в миллисекундах
        long timeDifference = new Date().getTime() - dbDateTime.getTime();

        //Преобразуем разницу в минуты
        long minutesDifference = timeDifference / (60 * 1000);

        //Выводим результат в текстовом формате
        ArrayList<String> words = new ArrayList<>();
        long difference;
        if (minutesDifference < 60) {
            difference = minutesDifference;
            words.add(" минуту назад");
            words.add(" минуты назад");
            words.add(" минут назад");
        } else if (minutesDifference < 24 * 60) {
            difference = minutesDifference / 60;
            words.add(" час назад");
            words.add(" часа назад");
            words.add(" часов назад");
        } else {
            difference = minutesDifference / (24 * 60);
            words.add(" день назад");
            words.add(" дня назад");
            words.add(" дней назад");
        }

        //Преобразуем окончание для слова
        if (difference % 100 >= 11 && difference % 100 <= 19) {
            return difference + words.get(2);
        } else {
            int lastDigit = (int) difference % 10;
            switch (lastDigit) {
                case 1:
                    return difference + words.get(0);
                case 2:
                case 3:
                case 4:
                    return difference + words.get(1);
                default:
                    return difference + words.get(2);
            }
        }
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}