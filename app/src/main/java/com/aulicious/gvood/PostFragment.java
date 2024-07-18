package com.aulicious.gvood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class PostFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText quantityEditText;
    private EditText cityEditText;
    private EditText addressEditText;
    private ImageView itemPhotoImageView;
    private Button submitButton;
    private ImageButton ImageButton;

    private Uri imageUri;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Database, Storage, and Auth references
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");
        storageReference = FirebaseStorage.getInstance().getReference("images");
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        // Initialize UI elements
        titleEditText = view.findViewById(R.id.title);
        descriptionEditText = view.findViewById(R.id.description);
        quantityEditText = view.findViewById(R.id.quantity);
        cityEditText = view.findViewById(R.id.city);
        addressEditText = view.findViewById(R.id.address);
        submitButton = view.findViewById(R.id.submit_button);
        itemPhotoImageView = view.findViewById(R.id.item_photo);
        ImageButton = view.findViewById(R.id.item_photo); // Change to photo_container

        // Set onClickListener for the photo container to open the image chooser
        ImageButton.setOnClickListener(v -> openFileChooser());

        // Set onClickListener for the submit button
        submitButton.setOnClickListener(v -> submitPost());

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                itemPhotoImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void submitPost() {
        // Get data from EditText fields
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String quantity = quantityEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        // Validate input
        if (title.isEmpty() || description.isEmpty() || quantity.isEmpty() ||
                city.isEmpty() || address.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if image is selected
        if (imageUri == null) {
            Toast.makeText(getActivity(), "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user ID
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = user.getUid();

        // Upload image to Firebase Storage
        final StorageReference fileReference = storageReference.child(UUID.randomUUID().toString() + ".jpg");

        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            savePostToDatabase(title, description, quantity, city, address, imageUrl, userId);
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                    Log.e("Upload Error", e.getMessage());
                });
    }

    private void savePostToDatabase(String title, String description, String quantity, String city, String address, String imageUrl, String userId) {
        // Generate unique ID for the post
        String postId = databaseReference.push().getKey();

        // Create a Post object
        Post post = new Post(title, description, quantity, city, address, imageUrl, userId);

        // Save post to Firebase Realtime Database
        if (postId != null) {
            databaseReference.child(postId).setValue(post)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Post submitted successfully", Toast.LENGTH_SHORT).show();
                            clearForm();
                        } else {
                            Toast.makeText(getActivity(), "Failed to submit post", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void clearForm() {
        titleEditText.setText("");
        descriptionEditText.setText("");
        quantityEditText.setText("");
        cityEditText.setText("");
        addressEditText.setText("");
        itemPhotoImageView.setImageResource(R.drawable.baseline_camera_alt_24); // reset to default image
        imageUri = null;
    }

    // Post class to structure the data
    public static class Post {
        public String title;
        public String description;
        public String quantity;
        public String city;
        public String address;
        public String imageUrl;
        public String userId;

        public Post() {
            // Default constructor required for calls to DataSnapshot.getValue(Post.class)
        }

        public Post(String title, String description, String quantity, String city, String address, String imageUrl, String userId) {
            this.title = title;
            this.description = description;
            this.quantity = quantity;
            this.city = city;
            this.address = address;
            this.imageUrl = imageUrl;
            this.userId = userId;
        }
    }
}

