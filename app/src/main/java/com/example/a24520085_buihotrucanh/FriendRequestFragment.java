package com.example.a24520085_buihotrucanh;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



public class FriendRequestFragment extends Fragment {

    private RecyclerView rvRequests;
    private FriendRequestAdapter adapter;
    private FriendViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_request, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);
        rvRequests = view.findViewById(R.id.rvFriendRequests);
        rvRequests.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.getRequests().observe(getViewLifecycleOwner(), requests -> {
            adapter = new FriendRequestAdapter(requests, new FriendRequestAdapter.OnRequestActionListener() {
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