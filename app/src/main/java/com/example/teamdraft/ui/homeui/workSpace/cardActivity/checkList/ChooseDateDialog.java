package com.example.teamdraft.ui.homeui.workSpace.cardActivity.checkList;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.example.teamdraft.ui.homeui.workSpace.cardActivity.OnChange;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ChooseDateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private String boardId, itemId, cardId, taskId, dateString;
    OnChange onChange;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onChange = (OnChange) context;
    }

    public static ChooseDateDialog newInstance(String boardId, String itemId, String cardId, String taskId, String dateString) {
        ChooseDateDialog dialog = new ChooseDateDialog();
        Bundle args = new Bundle();
        args.putString("boardId", boardId);
        args.putString("itemId", itemId);
        args.putString("cardId", cardId);
        args.putString("taskId", taskId);
        args.putString("dateString", dateString);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            boardId = getArguments().getString("boardId");
            itemId = getArguments().getString("itemId");
            cardId = getArguments().getString("cardId");
            taskId = getArguments().getString("taskId");
            dateString = getArguments().getString("dateString");
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LocalDate date;
        if (dateString == null){
            date = LocalDate.now();
        } else {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            date = LocalDate.parse(dateString, inputFormatter);
        }
        return new DatePickerDialog(requireContext(), this, date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String monthString = String.valueOf(month);
        String dayString = String.valueOf(dayOfMonth);
        if (month < 10){
            monthString = "0" + month;
        }
        if (dayOfMonth < 10){
            dayString = "0" + dayOfMonth;
        }
        String dateString = dayString + "-" + monthString + "-" + year;
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("boards").child(boardId).child("items").child(itemId).child("cards").child(cardId).child("checklist").child(taskId).child("date").setValue(dateString);
        onChange.onChangeCheckList();
    }
}
