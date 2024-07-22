package com.aulicious.gvood;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;
    private List<Post> postList;
    private DatabaseReference postsReference;
    private DatabaseReference usersReference;
    private Map<String, String> userIdToUsernameMap;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();
        userIdToUsernameMap = new HashMap<>();
        homeAdapter = new HomeAdapter(postList, userIdToUsernameMap, getContext());
        recyclerView.setAdapter(homeAdapter);

        postsReference = FirebaseDatabase.getInstance().getReference("posts");
        usersReference = FirebaseDatabase.getInstance().getReference("users");

        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userIdToUsernameMap.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    String username = userSnapshot.child("username").getValue(String.class);
                    userIdToUsernameMap.put(userId, username);
                }
                // Fetch posts after user data is loaded
                fetchPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });

        return view;
    }

    private void fetchPosts() {
        postsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    postList.add(post);
                }
                homeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

    // Post class should be the same as defined in PostFragment
    public static class Post {
        public String postId;
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

        public Post(String postId, String title, String description, String quantity, String city, String address, String imageUrl, String userId) {
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
