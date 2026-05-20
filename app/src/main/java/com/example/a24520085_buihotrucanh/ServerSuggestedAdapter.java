package com.example.a24520085_buihotrucanh;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ServerSuggestedAdapter extends RecyclerView.Adapter<ServerSuggestedAdapter.VH> {

    private List<ServerEmailSuggestion> items = new ArrayList<>();

    public void setItems(List<ServerEmailSuggestion> list) {
        this.items = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_server_email_suggestion, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ServerEmailSuggestion s = items.get(position);
        h.tvEmail.setText(s.getEmail());
        h.tvSubtitle.setText(s.getSubtitle());
        h.btnAction.setOnClickListener(v ->
                Toast.makeText(v.getContext(),
                        "Cannot send request.",
                        Toast.LENGTH_LONG).show());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvEmail;
        TextView tvSubtitle;
        Button btnAction;

        VH(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tvServerEmail);
            tvSubtitle = itemView.findViewById(R.id.tvServerSubtitle);
            btnAction = itemView.findViewById(R.id.btnServerAction);
        }
    }
}
