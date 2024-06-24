package com.teamnexapp.teamnex.ui.home.workSpace.cardActivity.users;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.teamnexapp.teamnex.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddUserDialog extends DialogFragment {
    private String boardId, itemId, cardId;
    ListView listView;
    AddUsersAdapter adapter;
    ProgressBar progressBar;
    ArrayList<UserType> users = new ArrayList<>();

    public static AddUserDialog newInstance(String boardId, String itemId, String cardId) {
        AddUserDialog dialog = new AddUserDialog();
        Bundle args = new Bundle();
        args.putString("boardId", boardId);
        args.putString("itemId", itemId);
        args.putString("cardId", cardId);
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
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.workspace_card_adduser_dialog, null);
        builder.setView(dialogView);

        progressBar = dialogView.findViewById(R.id.progressBarUsers);

        //Настраиваем listview участников карточки
        ArrayList<String> data = new ArrayList<>();
        data.add(boardId);
        data.add(itemId);
        data.add(cardId);
        listView = dialogView.findViewById(R.id.cardUsersList);
        adapter = new AddUsersAdapter(requireContext(), users, data, this);
        listView.setAdapter(adapter);

        //Получаем данные
        getUsers();

        ImageButton close = dialogView.findViewById(R.id.UserDialogClose);
        close.setOnClickListener(v -> {
            dismiss();
        });

        return builder.create();
    }

    void getUsers() {
        ArrayList<String> cardUsersIds = new ArrayList<>();
        ArrayList<String> boardUsersIds = new ArrayList<>();

        users.clear();

        listView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("boards").child(boardId).child("items").child(itemId).child("cards").child(cardId).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    cardUsersIds.add(userSnapshot.getKey());
                }

                mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            if (cardUsersIds.contains(userSnapshot.getKey())) {
                                UserType user = new UserType(userSnapshot.getValue(User.class), true);
                                users.add(user);
                            }
                        }

                        mDatabase.child("boards").child(boardId).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    if (!cardUsersIds.contains(userSnapshot.getKey())) {
                                        boardUsersIds.add(userSnapshot.getKey());
                                    }
                                }

                                mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                            if (boardUsersIds.contains(userSnapshot.getKey())) {
                                                UserType user = new UserType(userSnapshot.getValue(User.class), false);
                                                users.add(user);
                                            }
                                        }

                                        listView.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);

                                        //Обновляем адаптер
                                        adapter.notifyDataSetChanged();

                                        //Устанавливаем высоту listview
                                        int height = adapter.getCount() * 180;
                                        ViewGroup.LayoutParams params = listView.getLayoutParams();
                                        params.height = height;
                                        listView.setLayoutParams(params);
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

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int pixelsWidth = getResources().getDimensionPixelSize(R.dimen.dialog_edit_width);
            getDialog().getWindow().setLayout(pixelsWidth, WindowManager.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
