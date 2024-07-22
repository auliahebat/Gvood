package com.aulicious.gvood;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aulicious.gvood.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailActivity extends AppCompatActivity {

    private ImageView postImageView;
    private TextView titleTextView;
    private TextView userTextView;
    private TextView cityTextView;
    private TextView addressTextView; // Add this line
    private TextView descriptionTextView;
    private Button messageButton;
    private CircleImageView userProfileImageView; // Add this line
    private DatabaseReference postDatabase;
    private DatabaseReference userDatabase;

    private String userId;
    private String phoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        postImageView = findViewById(R.id.post_detail_image);
        titleTextView = findViewById(R.id.post_detail_title);
        userTextView = findViewById(R.id.post_detail_user);
        cityTextView = findViewById(R.id.post_detail_city);
        addressTextView = findViewById(R.id.post_detail_address); // Initialize address TextView
        descriptionTextView = findViewById(R.id.post_detail_description);
        messageButton = findViewById(R.id.message_button); // Initialize button
        userProfileImageView = findViewById(R.id.user_profile_image); // Initialize CircleImageView

        Intent intent = getIntent();
        if (intent != null) {
            String postId = intent.getStringExtra("postId");

            if (postId != null) {
                postDatabase = FirebaseDatabase.getInstance().getReference("posts").child(postId);
                loadPostDetails(postId);
            } else {
                Toast.makeText(this, "Post ID is missing", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Intent is null", Toast.LENGTH_SHORT).show();
            finish();
        }

        messageButton.setOnClickListener(v -> {
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                String url = "https://wa.me/" + phoneNumber;
                Intent whatsappIntent = new Intent(Intent.ACTION_VIEW);
                whatsappIntent.setData(Uri.parse(url));
                startActivity(whatsappIntent);
            } else {
                Toast.makeText(PostDetailActivity.this, "Phone number not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPostDetails(String postId) {
        postDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    String title = dataSnapshot.child("title").getValue(String.class);
                    userId = dataSnapshot.child("userId").getValue(String.class); // Change this to match your data structure
                    String city = dataSnapshot.child("city").getValue(String.class);
                    String address = dataSnapshot.child("address").getValue(String.class); // Get address
                    String description = dataSnapshot.child("description").getValue(String.class);

                    Glide.with(PostDetailActivity.this).load(imageUrl).into(postImageView);
                    titleTextView.setText(title);
                    cityTextView.setText(city);
                    descriptionTextView.setText(description);
                    if (address != null) {
                        addressTextView.setText(address); // Update address text view
                    } else {
                        addressTextView.setText("Address not available");
                    }

                    if (userId != null) {
                        loadUserDetails(userId);
                    }
                } else {
                    Toast.makeText(PostDetailActivity.this, "Post not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PostDetailActivity.this, "Error fetching post data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserDetails(String userId) {
        userDatabase = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String userProfileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class); // Get profile image URL
                    phoneNumber = dataSnapshot.child("phone").getValue(String.class); // Get phone number
                    if (username != null) {
                        userTextView.setText(username);
                    } else {
                        userTextView.setText("Username not available");
                    }
                    if (userProfileImageUrl != null) {
                        Glide.with(PostDetailActivity.this).load(userProfileImageUrl).into(userProfileImageView);
                    } else {
                        userProfileImageView.setImageResource(R.drawable.baseline_person_24); // Set default image
                    }
                } else {
                    userTextView.setText("User not found");
                    userProfileImageView.setImageResource(R.drawable.baseline_person_24); // Set default image
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PostDetailActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
