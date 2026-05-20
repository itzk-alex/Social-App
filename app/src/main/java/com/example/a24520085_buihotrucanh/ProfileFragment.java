package com.example.a24520085_buihotrucanh;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a24520085_buihotrucanh.network.ApiClient;
import com.example.a24520085_buihotrucanh.network.models.ApiPostDto;
import com.example.a24520085_buihotrucanh.network.models.PostsResponse;
import com.example.a24520085_buihotrucanh.network.models.ProfileResponse;
import com.example.a24520085_buihotrucanh.network.models.UpdateProfileRequest;
import com.example.a24520085_buihotrucanh.network.models.UpdateProfileResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView tvTitle;
    private ImageView ivAvatar;
    private EditText edtName, edtEmail, edtPhone, edtAddress, edtAvatarUrl, edtDescription;
    private Button btnLogout, btnSave;
    private TextView tvPostsHeader;
    private RecyclerView rvProfilePosts;
    private ProfilePostsAdapter profilePostsAdapter;

    private static final String ARG_OWNER_NAME = "owner_name";
    private static final String ARG_OWNER_USER_ID = "owner_user_id";

    public static ProfileFragment newInstance(@Nullable String ownerName) {
        return newInstance(ownerName, null);
    }

    public static ProfileFragment newInstance(@Nullable String ownerName, @Nullable String ownerUserId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_OWNER_NAME, ownerName);
        args.putString(ARG_OWNER_USER_ID, ownerUserId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTitle = view.findViewById(R.id.tv_profile_title);
        ivAvatar = view.findViewById(R.id.iv_prof_avatar);
        edtName = view.findViewById(R.id.edt_prof_name);
        edtEmail = view.findViewById(R.id.edt_prof_email);
        edtPhone = view.findViewById(R.id.edt_prof_phone);
        edtAddress = view.findViewById(R.id.edt_prof_address);
        edtAvatarUrl = view.findViewById(R.id.edt_prof_avatar_url);
        edtDescription = view.findViewById(R.id.edt_prof_description);
        btnSave = view.findViewById(R.id.btn_save);
        btnLogout = view.findViewById(R.id.btn_logout);
        tvPostsHeader = view.findViewById(R.id.tv_profile_posts_header);
        rvProfilePosts = view.findViewById(R.id.rv_profile_posts);

        rvProfilePosts.setLayoutManager(new LinearLayoutManager(requireContext()));
        profilePostsAdapter = new ProfilePostsAdapter();
        rvProfilePosts.setAdapter(profilePostsAdapter);

        String ownerName = getArguments() != null
                ? getArguments().getString(ARG_OWNER_NAME, null)
                : null;
        String ownerUserId = getArguments() != null
                ? getArguments().getString(ARG_OWNER_USER_ID, null)
                : null;

        if (ownerName != null && !Objects.equals(ownerName, UserData.registeredName)) {
            displayOtherProfile(ownerName, ownerUserId);
        } else {
            displayMyProfile();

            btnSave.setOnClickListener(v -> {
                String newName = edtName.getText().toString().trim();
                String newAddress = edtAddress.getText().toString().trim();
                String newDescription = edtDescription.getText().toString().trim();
                String newPhoneRaw = edtPhone.getText().toString().trim();
                String newAvatar = edtAvatarUrl.getText().toString().trim();

                String phoneDigits = newPhoneRaw.replaceAll("\\s+", "");
                if (!phoneDigits.isEmpty() && !phoneDigits.matches("^[0-9]{9,15}$")) {
                    Toast.makeText(requireContext(), "Invalid phone number (9–15 numbers)!", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String phoneToSend = phoneDigits;

                if (UserData.userId == null || UserData.userId.isEmpty()) {
                    UserData.registeredName = newName;
                    UserData.Address = newAddress;
                    UserData.Description = newDescription;
                    UserData.Phone = phoneToSend;
                    UserData.AvatarUrl = newAvatar;
                    tvTitle.setText("Hello, " + UserData.registeredName + "!");
                    loadAvatar(UserData.AvatarUrl);
                    Toast.makeText(requireContext(), "Updated!", Toast.LENGTH_SHORT).show();
                    return;
                }

                ApiClient.api().updateProfile(
                        UserData.userId,
                        new UpdateProfileRequest(newName, newAddress, newAvatar, newDescription, phoneToSend)
                ).enqueue(new Callback<UpdateProfileResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UpdateProfileResponse> call,
                                           @NonNull Response<UpdateProfileResponse> response) {
                        if (!response.isSuccessful() || response.body() == null || response.body().user == null) {
                            Toast.makeText(requireContext(), "Update failed!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        com.example.a24520085_buihotrucanh.network.models.UserDto u = response.body().user;
                        UserData.registeredName = u.name != null ? u.name : newName;
                        UserData.Address = u.address != null ? u.address : newAddress;
                        UserData.Description = u.description != null ? u.description : newDescription;
                        UserData.Phone = u.phone != null ? u.phone : phoneToSend;
                        UserData.AvatarUrl = u.avatarUrl != null ? u.avatarUrl : newAvatar;

                        tvTitle.setText("Hello, " + UserData.registeredName + "!");
                        edtPhone.setText(UserData.Phone);
                        edtAvatarUrl.setText(UserData.AvatarUrl);
                        loadAvatar(UserData.AvatarUrl);
                        Toast.makeText(requireContext(), "Updated!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NonNull Call<UpdateProfileResponse> call, @NonNull Throwable t) {
                        Toast.makeText(requireContext(), "Internet connection failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });

            btnLogout.setOnClickListener(v -> {
                UserData.clearSession();
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }
    }

    private void loadAvatar(@Nullable String url) {
        AvatarImageLoader.load(ivAvatar, url);
    }

    private void loadProfilePosts(@Nullable String userId) {
        if (userId == null || userId.isEmpty()) {
            profilePostsAdapter.setPosts(new ArrayList<>());
            tvPostsHeader.setText(getString(R.string.profile_posts_header_empty));
            return;
        }
        tvPostsHeader.setText(getString(R.string.profile_posts_header_loading));
        ApiClient.api().getPostsByUser(userId).enqueue(new Callback<PostsResponse>() {
            @Override
            public void onResponse(@NonNull Call<PostsResponse> call, @NonNull Response<PostsResponse> response) {
                List<Post> list = new ArrayList<>();
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    for (ApiPostDto p : response.body().data) {
                        list.add(Post.fromApiDto(p));
                    }
                }
                profilePostsAdapter.setPosts(list);
                tvPostsHeader.setText(getString(R.string.profile_posts_header_count, list.size()));
            }

            @Override
            public void onFailure(@NonNull Call<PostsResponse> call, @NonNull Throwable t) {
                profilePostsAdapter.setPosts(new ArrayList<>());
                tvPostsHeader.setText(getString(R.string.profile_posts_header_error));
            }
        });
    }

    private void displayMyProfile() {
        tvTitle.setText("Hello, " + UserData.registeredName + "!");
        edtName.setText(UserData.registeredName);
        edtEmail.setText(UserData.registeredEmail);
        edtPhone.setText(UserData.Phone);
        edtAddress.setText(UserData.Address);
        edtAvatarUrl.setText(UserData.AvatarUrl);
        edtDescription.setText(UserData.Description);
        loadAvatar(UserData.AvatarUrl);

        btnSave.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.VISIBLE);

        edtName.setEnabled(true);
        edtEmail.setEnabled(true);
        edtPhone.setEnabled(true);
        edtAddress.setEnabled(true);
        edtAvatarUrl.setEnabled(true);
        edtDescription.setEnabled(true);

        boolean hasRemoteSession = UserData.userId != null && !UserData.userId.isEmpty();
        if (hasRemoteSession) {
            edtEmail.setEnabled(false);
        }

        if (hasRemoteSession) {
            loadProfilePosts(UserData.userId);
            ApiClient.api().getProfile(UserData.userId).enqueue(new Callback<ProfileResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProfileResponse> call,
                                       @NonNull Response<ProfileResponse> response) {
                    if (!response.isSuccessful() || response.body() == null || response.body().user == null) return;

                    com.example.a24520085_buihotrucanh.network.models.UserDto u = response.body().user;
                    UserData.registeredName = u.name != null ? u.name : UserData.registeredName;
                    UserData.registeredEmail = u.email != null ? u.email : UserData.registeredEmail;
                    UserData.Address = u.address != null ? u.address : UserData.Address;
                    UserData.Description = u.description != null ? u.description : UserData.Description;
                    UserData.Phone = u.phone != null ? u.phone : UserData.Phone;
                    UserData.AvatarUrl = u.avatarUrl != null ? u.avatarUrl : UserData.AvatarUrl;

                    tvTitle.setText("Hello, " + UserData.registeredName + "!");
                    edtName.setText(UserData.registeredName);
                    edtEmail.setText(UserData.registeredEmail);
                    edtPhone.setText(UserData.Phone);
                    edtAddress.setText(UserData.Address);
                    edtAvatarUrl.setText(UserData.AvatarUrl);
                    edtDescription.setText(UserData.Description);
                    loadAvatar(UserData.AvatarUrl);
                }

                @Override
                public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {}
            });
        } else {
            loadProfilePosts(null);
        }
    }

    private void displayOtherProfile(@Nullable String name, @Nullable String userId) {
        btnSave.setVisibility(View.GONE);
        btnLogout.setVisibility(View.GONE);

        edtName.setEnabled(false);
        edtEmail.setEnabled(false);
        edtPhone.setEnabled(false);
        edtAddress.setEnabled(false);
        edtAvatarUrl.setEnabled(false);
        edtDescription.setEnabled(false);

        if (userId != null && !userId.isEmpty()) {
            tvTitle.setText(name != null && !name.isEmpty() ? name : "Profile");
            loadProfilePosts(userId);
            ApiClient.api().getProfile(userId).enqueue(new Callback<ProfileResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProfileResponse> call,
                                       @NonNull Response<ProfileResponse> response) {
                    if (!response.isSuccessful() || response.body() == null || response.body().user == null) {
                        Toast.makeText(requireContext(), "Cannot load profile!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    com.example.a24520085_buihotrucanh.network.models.UserDto u = response.body().user;
                    String dispName = u.name != null ? u.name : name;
                    tvTitle.setText(dispName != null ? dispName : "Profile");
                    edtName.setText(u.name != null ? u.name : "");
                    edtEmail.setText(u.email != null ? u.email : "");
                    edtPhone.setText(u.phone != null ? u.phone : "");
                    edtAddress.setText(u.address != null ? u.address : "");
                    edtAvatarUrl.setText(u.avatarUrl != null ? u.avatarUrl : "");
                    edtDescription.setText(u.description != null ? u.description : "");
                    loadAvatar(u.avatarUrl);
                }

                @Override
                public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                    Toast.makeText(requireContext(), "Internet connection failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        loadProfilePosts(null);

        String email = "", address = "", description = "";
        for (String[] user : UserData.userList) {
            if (name != null && user[0].equals(name)) {
                email = user[1];
                address = user.length > 3 ? user[3] : "";
                description = user.length > 4 ? user[4] : "";
                break;
            }
        }

        tvTitle.setText(name != null ? name + "!" : "Profile");
        edtName.setText(name != null ? name : "");
        edtEmail.setText(email);
        edtPhone.setText("");
        edtAvatarUrl.setText("");
        edtAddress.setText(address.isEmpty() ? "Secret Location" : address);
        edtDescription.setText(description.isEmpty() ? "No description available." : description);
        loadAvatar(null);
    }
}
