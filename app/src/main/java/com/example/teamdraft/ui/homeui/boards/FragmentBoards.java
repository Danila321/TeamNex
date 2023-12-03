package com.example.teamdraft.ui.homeui.boards;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.teamdraft.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentBoards extends Fragment {
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
        ImageButton find = view.findViewById(R.id.boardsFind);
        ImageButton sort = view.findViewById(R.id.boardsSort);
        ListView listView = view.findViewById(R.id.listViewBoards);
        Button create = view.findViewById(R.id.buttonCreate);

        connect.setOnClickListener(view12 -> {
            CreateConnectBoardDialog dialog = CreateConnectBoardDialog.newInstance(1);
            dialog.show(getChildFragmentManager(), "connectBoard");
        });

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

    private void loadBoardsData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabase.child("boards")
                .orderByChild("users/" + userId)
                .equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        items.clear();

                        for (DataSnapshot boardSnapshot : dataSnapshot.getChildren()) {
                            // Добавляем доску в список
                            Board board = boardSnapshot.getValue(Board.class);
                            if (board != null) {
                                items.add(board);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}