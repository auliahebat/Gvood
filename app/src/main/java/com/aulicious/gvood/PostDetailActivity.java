package com.aulicious.gvood;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class PostDetailActivity extends AppCompatActivity {

    private ImageView postImageView;
    private TextView titleTextView;
    private TextView userTextView;
    private TextView cityTextView;
    private TextView descriptionTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        postImageView = findViewById(R.id.post_detail_image);
        titleTextView = findViewById(R.id.post_detail_title);
        userTextView = findViewById(R.id.post_detail_user);
        cityTextView = findViewById(R.id.post_detail_city);
        descriptionTextView = findViewById(R.id.post_detail_description);

        Intent intent = getIntent();
        if (intent != null) {
            String imageUrl = intent.getStringExtra("imageUrl");
            String title = intent.getStringExtra("title");
            String user = intent.getStringExtra("user");
            String city = intent.getStringExtra("city");
            String description = intent.getStringExtra("description");

            Glide.with(this).load(imageUrl).into(postImageView);
            titleTextView.setText(title);
            userTextView.setText(user);
            cityTextView.setText(city);
            descriptionTextView.setText(description);
        }
    }
}
