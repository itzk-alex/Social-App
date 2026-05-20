package com.example.a24520085_buihotrucanh;

/** Một email trên server chưa nằm trong danh sách bạn hiện tại (gợi ý kết nối). */
public class ServerEmailSuggestion {
    private final String email;
    private final String subtitle;

    public ServerEmailSuggestion(String email, String subtitle) {
        this.email = email;
        this.subtitle = subtitle;
    }

    public String getEmail() {
        return email;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
