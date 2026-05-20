package com.example.a24520085_buihotrucanh.network.models;

import com.google.gson.annotations.SerializedName;

public class ApiPostDto {
    @SerializedName("id")
    public String id;

    @SerializedName("user_id")
    public String userId;

    @SerializedName("content")
    public String content;

    @SerializedName("created_at")
    public String createdAt;

    @SerializedName("author")
    public ApiPostAuthorDto author;
}

