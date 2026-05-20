package com.example.a24520085_buihotrucanh;

import java.util.Objects;

public class Post {
    private String id;
    private String userId;
    private String name;
    private String date;
    private String content;

    public Post(String name, String date, String content) {
        this(null, null, name, date, content);
    }

    public Post(String id, String userId, String name, String date, String content) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.date = date;
        this.content = content;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getAuthor() { return name; }
    public String getDate() { return date; }
    public String getContent() { return content; }


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