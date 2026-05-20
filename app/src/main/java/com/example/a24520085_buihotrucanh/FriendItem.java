package com.example.a24520085_buihotrucanh;

public class FriendItem {
    private String name;
    private String mutualFriends;

    public FriendItem(String name, String mutualFriends) {
        this.name = name;
        this.mutualFriends = mutualFriends;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMutualFriends() {
        return mutualFriends;
    }

    public void setMutualFriends(String mutualFriends) {
        this.mutualFriends = mutualFriends;
    }
}