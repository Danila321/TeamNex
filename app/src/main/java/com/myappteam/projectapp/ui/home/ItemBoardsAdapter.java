package com.myappteam.projectapp.ui.home;

import androidx.activity.result.ActivityResultLauncher;
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
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.myappteam.projectapp.R;
import com.myappteam.projectapp.ui.home.workSpace.settingsActivity.SettingsActivity;
import com.myappteam.projectapp.ui.home.workSpace.WorkSpaceActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ItemBoardsAdapter extends ArrayAdapter<Board> {
    private final Context context;
    private final ArrayList<Board> data;
    private ArrayList<Board> filteredData;
    private final ItemFilter itemFilter = new ItemFilter();
    ActivityResultLauncher<Intent> activityResultLauncher;

    public ItemBoardsAdapter(@NonNull Context context, ArrayList<Board> items, ActivityResultLauncher<Intent> activityResultLauncher) {
        super(context, R.layout.boards_custom_item, items);
        this.context = context;
        this.data = items;
        this.filteredData = items;
        this.activityResultLauncher = activityResultLauncher;
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
            Glide.with(context).load(board.getImageUri()).into(boardImage);
            nameTextView.setText(board.getName());
            dateTextView.setText(getPassedTime(board.getEditDate()));
        }

        boardItem.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), WorkSpaceActivity.class);
            intent.putExtra("boardId", board.getId());
            context.startActivity(intent);
        });

        boardSettings.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            intent.putExtra("boardId", board.getId());
            activityResultLauncher.launch(intent);
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
            words.add(context.getString(R.string.board_time_minute1));
            words.add(context.getString(R.string.board_time_minute2));
            words.add(context.getString(R.string.board_time_minute3));
        } else if (minutesDifference < 24 * 60) {
            difference = minutesDifference / 60;
            words.add(context.getString(R.string.board_time_hour1));
            words.add(context.getString(R.string.board_time_hour2));
            words.add(context.getString(R.string.board_time_hour3));
        } else {
            difference = minutesDifference / (24 * 60);
            words.add(context.getString(R.string.board_time_day1));
            words.add(context.getString(R.string.board_time_day2));
            words.add(context.getString(R.string.board_time_day3));
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
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Board getItem(int position) {
        return filteredData.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return itemFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            if (filterString.isEmpty()) {
                results.values = data;
                results.count = data.size();
            } else {
                final ArrayList<Board> list = data;

                int count = list.size();
                final ArrayList<Board> result = new ArrayList<>(count);

                String filterableString;

                for (int i = 0; i < count; i++) {
                    filterableString = list.get(i).getName();
                    if (filterableString.toLowerCase().contains(filterString)) {
                        result.add(list.get(i));
                    }
                }

                results.values = result;
                results.count = result.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Board>) results.values;
            notifyDataSetChanged();
        }
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}