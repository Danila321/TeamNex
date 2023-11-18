package com.example.teamdraft.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.teamdraft.R;
import com.example.teamdraft.databinding.FragmentHomeBinding;
import com.example.teamdraft.ui.homeui.boards.FragmentBoards;
import com.example.teamdraft.ui.homeui.groups.FragmentGroups;
import com.example.teamdraft.ui.homeui.notifications.FragmentNotifications;
import com.example.teamdraft.ui.homeui.archive.FragmentArchive;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        loadFragment(new FragmentBoards());
        BottomNavigationView bottomNavigationView = root.findViewById(R.id.nav_view2);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_board) {
                loadFragment(new FragmentBoards());
                return true;
            } else if (itemId == R.id.navigation_groups) {
                loadFragment(new FragmentGroups());
                return true;
            } else if (itemId == R.id.navigation_notifications) {
                loadFragment(new FragmentNotifications());
                return true;
            } else if (itemId == R.id.navigation_ele) {
                loadFragment(new FragmentArchive());
                return true;
            }
            return false;
        });

        return root;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}