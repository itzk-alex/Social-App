package com.example.a24520085_buihotrucanh.network.models;

import com.google.gson.annotations.SerializedName;

public class ApiPostAuthorDto {
    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("email")
    public String email;

    @SerializedName("avatar_url")
    public String avatarUrl;
}

