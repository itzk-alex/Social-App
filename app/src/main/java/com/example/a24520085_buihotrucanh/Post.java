package com.example.a24520085_buihotrucanh;

import com.example.a24520085_buihotrucanh.network.models.ApiPostDto;

import java.util.Objects;

public class Post {
    private String id;
    private String userId;
    private String name;
    private String date;
    private String content;
    private String avatarUrl;

    public Post(String name, String date, String content) {
        this(null, null, name, date, content, null);
    }

    public Post(String name, String date, String content, String avatarUrl) {
        this(null, null, name, date, content, avatarUrl);
    }

    public Post(String id, String userId, String name, String date, String content) {
        this(id, userId, name, date, content, null);
    }

    public Post(String id, String userId, String name, String date, String content, String avatarUrl) {
        this.id = id;
        this.userId = userId;
        this.name = name != null ? name : "";
        this.date = date != null ? date : "";
        this.content = content != null ? content : "";
        this.avatarUrl = avatarUrl;
    }

    public static Post fromApiDto(ApiPostDto p) {
        if (p == null) {
            return new Post("", "", "");
        }
        String authorName = UserData.registeredName;
        String avatarUrl = null;
        if (p.author != null) {
            if (p.author.name != null) authorName = p.author.name;
            avatarUrl = p.author.avatarUrl;
        }
        String createdAt = p.createdAt != null ? p.createdAt : "";
        String content = p.content != null ? p.content : "";
        return new Post(p.id, p.userId, authorName, createdAt, content, avatarUrl);
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getAuthor() { return name; }
    public String getDate() { return date; }
    public String getContent() { return content; }
    public String getAvatarUrl() { return avatarUrl; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        Post post = (Post) o;
        if (id != null && post.id != null) return Objects.equals(id, post.id);
        return Objects.equals(name, post.name)
                && Objects.equals(date, post.date)
                && Objects.equals(content, post.content);
    }

    @Override
    public int hashCode() {
        if (id != null) return Objects.hash(id);
        return Objects.hash(name, date, content);
    }
}
