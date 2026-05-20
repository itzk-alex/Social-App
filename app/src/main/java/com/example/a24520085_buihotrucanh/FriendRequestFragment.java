package com.example.a24520085_buihotrucanh;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class FriendRequestFragment extends Fragment {

    private RecyclerView rvRequests;
    private TextView tvEmpty;
    private FriendRequestAdapter adapter;
    private FriendViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_request, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);
        rvRequests = view.findViewById(R.id.rvFriendRequests);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        rvRequests.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel.getRequests().observe(getViewLifecycleOwner(), requests -> {
            boolean empty = requests == null || requests.isEmpty();
            tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            rvRequests.setVisibility(empty ? View.GONE : View.VISIBLE);
            if (empty) {
                tvEmpty.setText(R.string.friend_requests_empty_api);
            }
            adapter = new FriendRequestAdapter(requests != null ? requests : Collections.emptyList(), new FriendRequestAdapter.OnRequestActionListener() {
                @Override
                public void onAccept(FriendRequestItem item) {
                    viewModel.addFriend(new FriendItem(item.getName(), "Mới kết bạn"));
                    viewModel.removeRequest(item);
                }

                @Override
                public void onDecline(FriendRequestItem item) {
                    viewModel.removeRequest(item);
                }
            });
            rvRequests.setAdapter(adapter);
        });

        return view;
    }
}
