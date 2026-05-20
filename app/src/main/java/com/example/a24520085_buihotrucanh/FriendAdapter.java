package com.example.a24520085_buihotrucanh;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private List<FriendItem> friendList;

    public FriendAdapter(List<FriendItem> friendList) {
        this.friendList = friendList;
    }

    public void filterList(List<FriendItem> filteredList) {
        this.friendList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendItem friend = friendList.get(position);

        holder.tvFriendName.setText(friend.getName());
        holder.tvMutualFriends.setText(friend.getMutualFriends());

        holder.btnMessage.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Open conversation with " + friend.getName(), Toast.LENGTH_SHORT).show();
        });

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Open profile " + friend.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return friendList != null ? friendList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFriendName;
        TextView tvMutualFriends;
        Button btnMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFriendName = itemView.findViewById(R.id.tvFriendName);
            tvMutualFriends = itemView.findViewById(R.id.tvMutualFriends);
            btnMessage = itemView.findViewById(R.id.btnMessage);
        }
    }
}