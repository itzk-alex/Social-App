package com.example.a24520085_buihotrucanh;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProfilePostsAdapter extends RecyclerView.Adapter<ProfilePostsAdapter.VH> {

    private final List<Post> posts = new ArrayList<>();

    public void setPosts(List<Post> list) {
        posts.clear();
        if (list != null) posts.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile_post, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Post p = posts.get(position);
        h.tvAuthor.setText(p.getName() != null ? p.getName() : "");
        h.tvDate.setText(p.getDate() != null ? p.getDate() : "");
        h.tvContent.setText(p.getContent() != null ? p.getContent() : "");
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvAuthor;
        TextView tvDate;
        TextView tvContent;

        VH(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tv_profile_post_author);
            tvDate = itemView.findViewById(R.id.tv_profile_post_date);
            tvContent = itemView.findViewById(R.id.tv_profile_post_content);
        }
    }
}
