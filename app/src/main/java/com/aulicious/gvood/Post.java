package com.aulicious.gvood;

public class Post {
    public String postId;
    public String title;
    public String description;
    public String quantity;
    public String city;
    public String address;
    public String imageUrl;
    public String userId;

    // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    public Post() {
    }

    public Post(String postId, String title, String description, String quantity, String city, String address, String imageUrl, String userId) {
        this.postId = postId;
        this.title = title;
        this.description = description;
        this.quantity = quantity;
        this.city = city;
        this.address = address;
        this.imageUrl = imageUrl;
        this.userId = userId;
    }
}
