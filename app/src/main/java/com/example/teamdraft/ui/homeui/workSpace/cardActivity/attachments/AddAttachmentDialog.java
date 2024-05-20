package com.example.teamdraft.ui.homeui.workSpace.cardActivity.attachments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.example.teamdraft.R;
import com.example.teamdraft.ui.homeui.workSpace.cardActivity.OnChange;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class AddAttachmentDialog extends DialogFragment {
    private String boardId, itemId, cardId;
    Uri fileUri = null;
    OnChange onChange;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onChange = (OnChange) context;
    }

    public static AddAttachmentDialog newInstance(String boardId, String itemId, String cardId) {
        AddAttachmentDialog dialog = new AddAttachmentDialog();
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
        View dialogView = inflater.inflate(R.layout.workspace_card_attachments_dialog, null);
        builder.setView(dialogView);

        EditText editText = dialogView.findViewById(R.id.AttachmentDialogEditText);
        ConstraintLayout addFile = dialogView.findViewById(R.id.AttachmentDialogAdd);
        Button button = dialogView.findViewById(R.id.AttachmentDialogButton);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            fileUri = data.getData();
                        }
                    }
                });
        addFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            activityResultLauncher.launch(intent);
        });

        button.setOnClickListener(v -> {
            if (editText.getText().toString().isEmpty()) {
                editText.setError("Введите название");
            } else if (fileUri == null) {
                System.out.println("Файл не выбран");
            } else {
                uploadFile(fileUri, editText.getText().toString());
                onChange.onChangeAttachments();
                dismiss();
            }
        });

        return builder.create();
    }

    void uploadFile(Uri fileUri, String fileName) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("boards").child(boardId).child("attachments")
                .child(System.currentTimeMillis() + "_attachment_image.jpg");

        storageReference.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String attachmentID = UUID.randomUUID().toString();
                    @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    ItemAttachment attachment = new ItemAttachment(attachmentID, uri.toString(), fileName, date);

                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("boards").child(boardId).child("items").child(itemId).child("cards").child(cardId).child("attachments").child(attachmentID).setValue(attachment);
                })).addOnFailureListener(e -> {

                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.workspace_card_attachments_dialog, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return view;
    }
}
