package com.example.a24520085_buihotrucanh;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    private List<FriendRequestItem> requestList;
    private OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onAccept(FriendRequestItem item);
        void onDecline(FriendRequestItem item);
    }

    public FriendRequestAdapter(List<FriendRequestItem> requestList, OnRequestActionListener listener) {
        this.requestList = requestList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendRequestItem item = requestList.get(position);
        holder.tvName.setText(item.getName());

        holder.btnAccept.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAccept(item);
            }
        });

        holder.btnDecline.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDecline(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList != null ? requestList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        Button btnAccept, btnDecline;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvReqName);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
        }
    }
}