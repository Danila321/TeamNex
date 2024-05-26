package com.example.teamdraft.ui.homeui.workSpace.cardActivity.checkList;

import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import com.example.teamdraft.R;
import com.example.teamdraft.ui.homeui.workSpace.cardActivity.OnChange;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class ItemChecklistAdapter extends ArrayAdapter<ItemChecklist> {
    ArrayList<String> data;
    FragmentManager fragmentManager;
    OnChange onChange;

    public ItemChecklistAdapter(@NonNull Context context, ArrayList<ItemChecklist> items, FragmentManager fragmentManager, ArrayList<String> data, OnChange onChange) {
        super(context, R.layout.workspace_card_checklist_item, items);
        this.data = data;
        this.fragmentManager = fragmentManager;
        this.onChange = onChange;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.workspace_card_checklist_item, null);
        }

        ItemChecklist item = getItem(position);

        //Выводим название
        TextView name = convertView.findViewById(R.id.checklistItemName);
        name.setText(item.getName());

        //Настраиваем CheckBox
        CheckBox checkBox = convertView.findViewById(R.id.checkBox);
        checkBox.setChecked(item.getChecked());
        if (checkBox.isChecked()) {
            name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            name.setPaintFlags(name.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
        checkBox.setOnClickListener(v -> {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("boards").child(data.get(0)).child("items").child(data.get(1)).child("cards").child(data.get(2)).child("checklist").child(item.getId()).child("checked").setValue(checkBox.isChecked());
            if (checkBox.isChecked()) {
                name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                name.setPaintFlags(name.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
            onChange.onCheckItemChecklist();
        });

        //Настраиваем блок даты
        ImageButton dateButton = convertView.findViewById(R.id.checklistItemDate);
        ConstraintLayout dateLayout = convertView.findViewById(R.id.checklistItemDateLayout);
        if (item.getDate().isEmpty()) {
            dateButton.setVisibility(View.VISIBLE);
            dateLayout.setVisibility(View.GONE);
            //Кнопка добавления даты
            dateButton.setOnClickListener(v -> {
                ChooseDateDialog dialog = ChooseDateDialog.newInstance(data.get(0), data.get(1), data.get(2), item.getId(), null);
                dialog.show(fragmentManager, "chooseDateDialog");
            });
        } else {
            dateLayout.setVisibility(View.VISIBLE);
            dateButton.setVisibility(View.GONE);
            dateLayout.setOnClickListener(v -> {
                DateChooseActionDialog dialog = DateChooseActionDialog.newInstance(data.get(0), data.get(1), data.get(2), item.getId(), item.getDate());
                dialog.show(fragmentManager, "chooseDialog");
            });
            TextView dateTextView = convertView.findViewById(R.id.checklistItemDateText);
            try {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate date = LocalDate.parse(item.getDate(), inputFormatter);

                if (checkBox.isChecked()) {
                    dateLayout.setBackgroundResource(R.drawable.workspace_button_date_green_12dp);
                } else {
                    if (date.isAfter(LocalDate.now())) {
                        dateLayout.setBackgroundResource(R.drawable.workspace_button_date_gray_12dp);
                    } else if (date.isEqual(LocalDate.now())) {
                        dateLayout.setBackgroundResource(R.drawable.workspace_button_date_yellow_12dp);
                    } else if (date.isBefore(LocalDate.now())) {
                        dateLayout.setBackgroundResource(R.drawable.workspace_button_date_red_12dp);
                    }
                }

                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (checkBox.isChecked()) {
                        dateLayout.setBackgroundResource(R.drawable.workspace_button_date_green_12dp);
                    } else {
                        if (date.isAfter(LocalDate.now())) {
                            dateLayout.setBackgroundResource(R.drawable.workspace_button_date_gray_12dp);
                        } else if (date.isEqual(LocalDate.now())) {
                            dateLayout.setBackgroundResource(R.drawable.workspace_button_date_yellow_12dp);
                        } else if (date.isBefore(LocalDate.now())) {
                            dateLayout.setBackgroundResource(R.drawable.workspace_button_date_red_12dp);
                        }
                    }
                });

                DateTimeFormatter outputFormatter;
                if (LocalDate.now().getYear() == date.getYear()) {
                    outputFormatter = DateTimeFormatter.ofPattern("d MMM", new Locale("ru"));
                } else {
                    outputFormatter = DateTimeFormatter.ofPattern("d MMM, yyyy", new Locale("ru"));
                }

                dateTextView.setText(date.format(outputFormatter));
            } catch (Exception ignored) {

            }
        }

        //Кнопка удаления задачи
        ImageButton delete = convertView.findViewById(R.id.checklistItemDelete);
        delete.setOnClickListener(v -> {
            DeleteChecklistItemDialog dialog = DeleteChecklistItemDialog.newInstance(data.get(0), data.get(1), data.get(2), item.getId());
            dialog.show(fragmentManager, "deleteChecklistTask");
        });

        return convertView;
    }
}
