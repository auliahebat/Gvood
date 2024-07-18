package com.aulicious.gvood;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private TextView tvUsername, tvEmail, tvPhone;
    private CircleImageView profileImage;
    private DatabaseReference database;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUsername = view.findViewById(R.id.profile_username);
        tvEmail = view.findViewById(R.id.profile_email);
        tvPhone = view.findViewById(R.id.profile_phone);
        profileImage = view.findViewById(R.id.profile_image);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String uid = user.getUid();
            database = FirebaseDatabase.getInstance().getReference("users").child(uid);
            storage = FirebaseStorage.getInstance();

            loadUserProfile(uid);

            profileImage.setOnClickListener(v -> openImageChooser());

            ImageButton editUsernameButton = view.findViewById(R.id.edit_username);
            editUsernameButton.setOnClickListener(v -> showEditUsernameDialog());

            ImageButton editPhoneButton = view.findViewById(R.id.edit_phone);
            editPhoneButton.setOnClickListener(v -> showEditPhoneDialog());
        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

        return view;
    }

    private void loadUserProfile(String uid) {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);
                    String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);

                    tvUsername.setText(username);
                    tvEmail.setText(email);
                    tvPhone.setText(phone);

                    if (profileImageUrl != null) {
                        Glide.with(getContext()).load(profileImageUrl).into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load profile data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadProfileImage();
        }
    }

    private void uploadProfileImage() {
        if (imageUri != null) {
            String uid = mAuth.getCurrentUser().getUid();
            StorageReference ref = storage.getReference("profile_images/" + uid + "_" + UUID.randomUUID().toString());
            ref.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    database.child("profileImageUrl").setValue(uri.toString());
                    Toast.makeText(getActivity(), "Profile image updated", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void showEditUsernameDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_username, null);
        dialogBuilder.setView(dialogView);

        EditText editTextUsername = dialogView.findViewById(R.id.edit_text_username);
        editTextUsername.setText(tvUsername.getText().toString());

        dialogBuilder.setTitle("Edit Username");
        dialogBuilder.setPositiveButton("Save", (dialog, which) -> {
            String newUsername = editTextUsername.getText().toString().trim();
            if (!newUsername.isEmpty()) {
                saveUsername(newUsername);
            } else {
                Toast.makeText(getActivity(), "Username cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }


    private void showEditPhoneDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_phone, null);
        dialogBuilder.setView(dialogView);

        EditText editTextPhone = dialogView.findViewById(R.id.edit_text_phone);
        editTextPhone.setText(tvPhone.getText().toString());

        dialogBuilder.setTitle("Edit Phone Number");
        dialogBuilder.setPositiveButton("Save", (dialog, which) -> {
            String newPhone = editTextPhone.getText().toString().trim();
            if (!newPhone.isEmpty()) {
                savePhone(newPhone);
            } else {
                Toast.makeText(getActivity(), "Phone number cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void saveUsername(String newUsername) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            database.child("username").setValue(newUsername)
                    .addOnSuccessListener(aVoid -> {
                        tvUsername.setText(newUsername);
                        Toast.makeText(getActivity(), "Username updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to update username: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void saveEmail(String newEmail) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updateEmail(newEmail)
                    .addOnSuccessListener(aVoid -> {
                        database.child("email").setValue(newEmail);
                        tvEmail.setText(newEmail);
                        Toast.makeText(getActivity(), "Email updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to update email: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void savePhone(String newPhone) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            database.child("phone").setValue(newPhone)
                    .addOnSuccessListener(aVoid -> {
                        tvPhone.setText(newPhone);
                        Toast.makeText(getActivity(), "Phone number updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to update phone number: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
