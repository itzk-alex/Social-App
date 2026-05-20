package com.example.a24520085_buihotrucanh.network.models;

import com.google.gson.annotations.SerializedName;

public class CreatePostRequest {
    @SerializedName("user_id")
    public String userId;

    @SerializedName("content")
    public String content;

    public CreatePostRequest(String userId, String content) {
        this.userId = userId;
        this.content = content;
    }
}

