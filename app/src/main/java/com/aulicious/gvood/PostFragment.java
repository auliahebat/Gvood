package com.aulicious.gvood;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private DatabaseReference postsReference;
    private String currentUserId;

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, getContext());
        recyclerView.setAdapter(postAdapter);

        // Get the current user ID
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        postsReference = FirebaseDatabase.getInstance().getReference("posts");

        // Fetch posts for the current user
        fetchPostsForCurrentUser();

        // Set up FloatingActionButton
        FloatingActionButton fabAddPost = view.findViewById(R.id.fab_add_post);
        fabAddPost.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddPostActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void fetchPostsForCurrentUser() {
        postsReference.orderByChild("userId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        postList.add(post);
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load posts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // PostAdapter class
    private static class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

        private final List<Post> postList;
        private final Context context;

        public PostAdapter(List<Post> postList, Context context) {
            this.postList = postList;
            this.context = context;
        }

        @NonNull
        @Override
        public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
            return new PostViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
            Post post = postList.get(position);
            holder.titleTextView.setText(post.title);
            holder.userTextView.setText(post.userId); // Ideally, fetch the user name based on the userId
            holder.cityTextView.setText(post.city);
            Glide.with(holder.itemView.getContext()).load(post.imageUrl).into(holder.postImageView);

            holder.deleteButton.setOnClickListener(v -> {
                // Handle delete button click
                deletePost(post.postId, position);
            });

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("imageUrl", post.imageUrl);
                intent.putExtra("title", post.title);
                intent.putExtra("user", post.userId);
                intent.putExtra("city", post.city);
                intent.putExtra("description", post.description);
                context.startActivity(intent);
            });
        }

        private void deletePost(String postId, int position) {
            Log.d("PostFragment", "Attempting to delete post at position: " + position + ", list size: " + postList.size());
            if (position >= 0 && position < postList.size()) {
                DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);
                postRef.removeValue().addOnSuccessListener(aVoid -> {
                    postList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, postList.size());
                    Toast.makeText(context, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(context, "Post position is invalid", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        public int getItemCount() {
            return postList.size();
        }

        public static class PostViewHolder extends RecyclerView.ViewHolder {
            public ImageView postImageView;
            public TextView titleTextView;
            public TextView userTextView;
            public TextView cityTextView;
            public ImageButton deleteButton;

            public PostViewHolder(@NonNull View itemView) {
                super(itemView);
                postImageView = itemView.findViewById(R.id.post_image);
                titleTextView = itemView.findViewById(R.id.post_title);
                userTextView = itemView.findViewById(R.id.post_user);
                cityTextView = itemView.findViewById(R.id.post_city);
                deleteButton = itemView.findViewById(R.id.delete_button);
            }
        }
    }
}
