package com.example.teamdraft.ui.homeui.boards;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.teamdraft.R;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentBoards extends Fragment implements OnCreateConnectBoard {
    ArrayList<Board> items = new ArrayList<>();
    ItemBoardsAdapter adapter;

    public FragmentBoards() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_boards, container, false);

        Button connect = view.findViewById(R.id.buttonConnect);
        ChipGroup chipGroup = view.findViewById(R.id.chipGroupBoard);
        ConstraintLayout searchLayout = view.findViewById(R.id.BoardSearchLayout);
        ImageButton find = view.findViewById(R.id.boardsFind);
        ImageButton sort = view.findViewById(R.id.boardsSort);
        ListView listView = view.findViewById(R.id.listViewBoards);
        Button create = view.findViewById(R.id.buttonCreate);

        //Кнопка подключения к доске
        connect.setOnClickListener(view12 -> {
            CreateConnectBoardDialog dialog = CreateConnectBoardDialog.newInstance(1);
            dialog.show(getChildFragmentManager(), "connectBoard");
        });

        //Кнопка поиска доски
        find.setOnClickListener(v -> {
            EditText editText = view.findViewById(R.id.BoardSearchEditText);

            //Настраиваем анимацию открытия
            Animation animationShow = AnimationUtils.loadAnimation(requireContext(), R.anim.find_board_edittext_anim);
            animationShow.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    chipGroup.setVisibility(View.INVISIBLE);
                    find.setVisibility(View.INVISIBLE);
                    searchLayout.setVisibility(View.VISIBLE);

                    editText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            searchLayout.startAnimation(animationShow);

            //Настраиваем закрытие
            ImageButton close = view.findViewById(R.id.BoardSearchCloseButton);
            close.setOnClickListener(v1 -> {
                if (editText.getText().toString().isEmpty()) {
                    chipGroup.setVisibility(View.VISIBLE);
                    find.setVisibility(View.VISIBLE);
                    searchLayout.setVisibility(View.INVISIBLE);
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
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            if (data.getBooleanExtra("dataChanged", false)) {
                                loadBoardsData();
                            }
                        }
                    }
                });

        //Загружаем данные всех досок и выводим на экран
        loadBoardsData();
        adapter = new ItemBoardsAdapter(requireContext(), items, activityResultLauncher);
        listView.setAdapter(adapter);

        create.setOnClickListener(view1 -> {
            CreateConnectBoardDialog dialog = CreateConnectBoardDialog.newInstance(0);
            dialog.show(getChildFragmentManager(), "createBoard");
        });

        return view;
    }

    private void loadBoardsData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabase.child("boards")
                .orderByChild("users/" + userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
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
                });
    }

    @Override
    public void onChange() {
        loadBoardsData();
    }
}