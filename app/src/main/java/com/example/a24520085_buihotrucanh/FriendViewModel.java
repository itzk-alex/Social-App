package com.example.a24520085_buihotrucanh;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class FriendViewModel extends ViewModel {
    private final MutableLiveData<List<FriendItem>> friendsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<FriendRequestItem>> requestsLiveData = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<FriendItem>> getFriends() { return friendsLiveData; }
    public LiveData<List<FriendRequestItem>> getRequests() { return requestsLiveData; }

    public void loadInitialFriends() {
        if (friendsLiveData.getValue() == null || friendsLiveData.getValue().isEmpty()) {
            List<FriendItem> initialFriends = new ArrayList<>();
            initialFriends.add(new FriendItem("Nguyễn Văn A", "2 bạn chung"));
            initialFriends.add(new FriendItem("Trần Thị B", "Mới truy cập"));
            friendsLiveData.setValue(initialFriends);
        }

        if (requestsLiveData.getValue() == null || requestsLiveData.getValue().isEmpty()) {
            List<FriendRequestItem> initialReqs = new ArrayList<>();
            initialReqs.add(new FriendRequestItem("Lê Văn E"));
            initialReqs.add(new FriendRequestItem("Trần Thị F"));
            requestsLiveData.setValue(initialReqs);
        }
    }

    public void addFriend(FriendItem newFriend) {
        List<FriendItem> current = friendsLiveData.getValue();
        if (current != null) {
            List<FriendItem> updatedList = new ArrayList<>(current);
            updatedList.add(0, newFriend);
            friendsLiveData.setValue(updatedList);
        }
    }

    public void removeRequest(FriendRequestItem item) {
        List<FriendRequestItem> current = requestsLiveData.getValue();
        if (current != null) {
            List<FriendRequestItem> updatedList = new ArrayList<>(current);
            updatedList.remove(item);
            requestsLiveData.setValue(updatedList);
        }
    }
}