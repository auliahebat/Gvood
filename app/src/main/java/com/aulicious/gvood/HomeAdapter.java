package com.aulicious.gvood;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.aulicious.gvood.HomeFragment.Post;

import java.util.List;
import java.util.Map;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.PostViewHolder> {

    private final List<Post> postList;
    private final Map<String, String> userIdToUsernameMap;
    private final Context context;

    public HomeAdapter(List<Post> postList, Map<String, String> userIdToUsernameMap, Context context) {
        this.postList = postList;
        this.userIdToUsernameMap = userIdToUsernameMap;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.titleTextView.setText(post.title);
        holder.userTextView.setText(userIdToUsernameMap.get(post.userId)); // Display username
        holder.cityTextView.setText(post.city);
        Glide.with(holder.itemView.getContext()).load(post.imageUrl).into(holder.postImageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("postId", post.postId); // Send postId to PostDetailActivity
            intent.putExtra("imageUrl", post.imageUrl);
            intent.putExtra("title", post.title);
            intent.putExtra("user", userIdToUsernameMap.get(post.userId));
            intent.putExtra("city", post.city);
            intent.putExtra("description", post.description);
            context.startActivity(intent);
        });
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

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postImageView = itemView.findViewById(R.id.post_image);
            titleTextView = itemView.findViewById(R.id.post_title);
            userTextView = itemView.findViewById(R.id.post_user);
            cityTextView = itemView.findViewById(R.id.post_city);
        }
    }
}
