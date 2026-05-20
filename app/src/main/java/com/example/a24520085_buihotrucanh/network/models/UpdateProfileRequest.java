package com.example.a24520085_buihotrucanh.network.models;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileRequest {
    @SerializedName("name")
    public String name;

    @SerializedName("address")
    public String address;

    @SerializedName("avatar_url")
    public String avatarUrl;

    @SerializedName("description")
    public String description;

    @SerializedName("phone")
    public String phone;

    public UpdateProfileRequest(String name, String address, String avatarUrl, String description, String phone) {
        this.name = name;
        this.address = address;
        this.avatarUrl = avatarUrl;
        this.description = description;
        this.phone = phone;
    }
}

