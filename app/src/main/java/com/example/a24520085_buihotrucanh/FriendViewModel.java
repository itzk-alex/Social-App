package com.example.a24520085_buihotrucanh;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.a24520085_buihotrucanh.network.ApiClient;
import com.example.a24520085_buihotrucanh.network.ApiEmailParser;
import com.example.a24520085_buihotrucanh.network.models.FriendsResponse;
import com.example.a24520085_buihotrucanh.network.models.UserDto;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendViewModel extends ViewModel {
    private final MutableLiveData<List<FriendItem>> friendsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<FriendRequestItem>> requestsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Integer> friendCountLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<List<ServerEmailSuggestion>> serverSuggestionsLiveData = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<FriendItem>> getFriends() { return friendsLiveData; }
    public LiveData<List<FriendRequestItem>> getRequests() { return requestsLiveData; }
    public LiveData<Integer> getFriendCount() { return friendCountLiveData; }
    public LiveData<List<ServerEmailSuggestion>> getServerSuggestions() { return serverSuggestionsLiveData; }

    public void loadInitialFriends() {
        refreshFriendsAndSuggestions(null);
    }

    public void refreshFriendsAndSuggestions(@Nullable Runnable onComplete) {
        String uid = UserData.userId;
        if (uid == null || uid.isEmpty()) {
            friendsLiveData.setValue(new ArrayList<>());
            requestsLiveData.setValue(new ArrayList<>());
            friendCountLiveData.setValue(0);
            serverSuggestionsLiveData.setValue(new ArrayList<>());
            if (onComplete != null) onComplete.run();
            return;
        }

        ApiClient.api().getFriends(uid).enqueue(new Callback<FriendsResponse>() {
            @Override
            public void onResponse(Call<FriendsResponse> call, Response<FriendsResponse> response) {
                List<FriendItem> list = new ArrayList<>();
                FriendsResponse body = response.body();
                Set<String> friendEmailsLower = new HashSet<>();

                if (response.isSuccessful() && body != null && body.friends != null) {
                    for (UserDto u : body.friends) {
                        if (u == null) continue;
                        String displayName = u.name != null ? u.name : "?";
                        String subtitle;
                        if (u.email != null && !u.email.isEmpty()) {
                            subtitle = u.email;
                            friendEmailsLower.add(u.email.trim().toLowerCase(Locale.ROOT));
                        } else if (u.phone != null && !u.phone.isEmpty()) {
                            subtitle = u.phone;
                        } else {
                            subtitle = "Bạn bè";
                        }
                        list.add(new FriendItem(displayName, subtitle));
                    }
                    friendCountLiveData.setValue(body.count > 0 ? body.count : list.size());
                } else {
                    friendCountLiveData.setValue(0);
                }
                friendsLiveData.setValue(list);
                requestsLiveData.setValue(new ArrayList<>());

                final Set<String> friendEmailsFinal = friendEmailsLower;
                ApiClient.api().getAllUserEmails().enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        List<String> rawEmails = new ArrayList<>();
                        if (response.isSuccessful() && response.body() != null) {
                            rawEmails = ApiEmailParser.parseEmailStrings(response.body());
                        }

                        String my = UserData.registeredEmail != null
                                ? UserData.registeredEmail.trim().toLowerCase(Locale.ROOT) : "";

                        List<ServerEmailSuggestion> suggestions = new ArrayList<>();
                        for (String em : rawEmails) {
                            if (em == null) continue;
                            String trimmed = em.trim();
                            if (trimmed.isEmpty()) continue;
                            String lower = trimmed.toLowerCase(Locale.ROOT);
                            if (lower.equals(my) || friendEmailsFinal.contains(lower)) continue;
                            suggestions.add(new ServerEmailSuggestion(trimmed, "Có tài khoản trên hệ thống"));
                        }
                        serverSuggestionsLiveData.setValue(suggestions);
                        if (onComplete != null) onComplete.run();
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        serverSuggestionsLiveData.setValue(new ArrayList<>());
                        if (onComplete != null) onComplete.run();
                    }
                });
            }

            @Override
            public void onFailure(Call<FriendsResponse> call, Throwable t) {
                friendsLiveData.setValue(new ArrayList<>());
                friendCountLiveData.setValue(0);
                serverSuggestionsLiveData.setValue(new ArrayList<>());
                if (onComplete != null) onComplete.run();
            }
        });
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
