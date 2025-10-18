package com.myappteam.projectapp.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.Query;
import com.myappteam.projectapp.R;
import com.myappteam.projectapp.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    ArrayList<Board> items = new ArrayList<>();
    ItemBoardsAdapter adapter;
    private Query mDatabase;
    private ValueEventListener listener;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Button connect = view.findViewById(R.id.buttonConnect);
        ImageButton refresh = view.findViewById(R.id.boardsRefresh);
        ListView listView = view.findViewById(R.id.listViewBoards);
        Button create = view.findViewById(R.id.buttonCreate);

        //Кнопка подключения к доске
        connect.setOnClickListener(view12 -> {
            CreateConnectBoardDialog dialog = CreateConnectBoardDialog.newInstance(1);
            dialog.show(getChildFragmentManager(), "connectBoard");
        });

        //Настраиваем поиск доски
        EditText editText = view.findViewById(R.id.BoardSearchEditText);
        ImageButton close = view.findViewById(R.id.BoardSearchCloseButton);
        close.setOnClickListener(v1 -> {
            if (editText.getText().toString().isEmpty()) {
                editText.clearFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            } else {
                editText.setText("");
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        refresh.setOnClickListener(v -> loadBoardsData());

        //Загружаем данные всех досок и выводим на экран
        loadBoardsData();
        adapter = new ItemBoardsAdapter(requireContext(), items);
        listView.setAdapter(adapter);

        create.setOnClickListener(view1 -> {
            CreateConnectBoardDialog dialog = CreateConnectBoardDialog.newInstance(0);
            dialog.show(getChildFragmentManager(), "createBoard");
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mDatabase != null && listener != null) {
            mDatabase.addValueEventListener(listener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDatabase != null && listener != null) {
            mDatabase.removeEventListener(listener);
        }
    }

    private void loadBoardsData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            return;
        }

        String userId = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("boards").orderByChild("users/" + userId);

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items.clear();

                for (DataSnapshot boardSnapshot : dataSnapshot.getChildren()) {
                    //Получаем роль юзера
                    String userRole = boardSnapshot.child("users").child(userId).getValue(String.class);
                    if (userRole != null && (userRole.equals("owner") || userRole.equals("admin") || userRole.equals("user"))) {
                        // Добавляем доску в список
                        Board board = boardSnapshot.getValue(Board.class);
                        if (board != null) {
                            items.add(board);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}