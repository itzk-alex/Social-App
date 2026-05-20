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

public class SuggestedFriendAdapter extends RecyclerView.Adapter<SuggestedFriendAdapter.ViewHolder> {

    private List<ContactItem> contactList;

    public SuggestedFriendAdapter(List<ContactItem> contactList) {
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_suggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContactItem contact = contactList.get(position);

        holder.tvName.setText(contact.getName());
        holder.tvPhone.setText(contact.getPhone());

        holder.btnAddFriend.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Has sent friend request: " + contact.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return contactList != null ? contactList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvPhone;
        Button btnAddFriend;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            btnAddFriend = itemView.findViewById(R.id.btnAddFriend);
        }
    }
}