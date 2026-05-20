package com.example.a24520085_buihotrucanh;

import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

/** Load avatar URL vào ImageView; URL rỗng hoặc null → placeholder. */
public final class AvatarImageLoader {
    private AvatarImageLoader() {}

    public static void load(@Nullable ImageView imageView, @Nullable String url) {
        if (imageView == null) return;
        android.content.Context ctx = imageView.getContext();
        if (ctx == null) return;

        String trimmed = url != null ? url.trim() : "";
        if (trimmed.isEmpty()) {
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
            return;
        }
        Glide.with(imageView)
                .load(trimmed)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(imageView);
    }

    /** URL hiển thị: từ post, hoặc profile hiện tại nếu là bài của user đang đăng nhập. */
    @Nullable
    public static String resolveAvatarUrl(@Nullable String postAvatarUrl, @Nullable String postUserId) {
        if (postUserId != null && !postUserId.isEmpty()
                && UserData.userId != null && UserData.userId.equals(postUserId)
                && UserData.AvatarUrl != null && !UserData.AvatarUrl.trim().isEmpty()) {
            return UserData.AvatarUrl.trim();
        }
        if (postAvatarUrl != null && !postAvatarUrl.trim().isEmpty()) {
            return postAvatarUrl.trim();
        }
        return null;
    }
}
