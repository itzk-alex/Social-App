package com.example.a24520085_buihotrucanh.network.models;

import com.google.gson.annotations.SerializedName;

public class UserDto {
    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("address")
    public String address;

    @SerializedName("avatar_url")
    public String avatarUrl;

    @SerializedName("description")
    public String description;

    @SerializedName("email")
    public String email;

    @SerializedName("phone")
    public String phone;

    @SerializedName("created_at")
    public String createdAt;
}

