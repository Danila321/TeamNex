package com.teamnexapp.teamnex.ui.feedback;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.teamnexapp.teamnex.R;
import com.teamnexapp.teamnex.databinding.FragmentFeedbackBinding;

public class FeedbackFragment extends Fragment {
    private FragmentFeedbackBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFeedbackBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Кнопка копирования кода в буфер обмена
        ImageButton copyEmail = root.findViewById(R.id.FeedbackButtonCopyCode);
        copyEmail.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("email", "teamdraftsup@gmail.com");
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getActivity(), R.string.feedback_copy_text, Toast.LENGTH_SHORT).show();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}