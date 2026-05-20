package com.example.a24520085_buihotrucanh;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.a24520085_buihotrucanh.network.ApiClient;
import com.example.a24520085_buihotrucanh.network.models.ProfileResponse;
import com.example.a24520085_buihotrucanh.network.models.UpdateProfileRequest;
import com.example.a24520085_buihotrucanh.network.models.UpdateProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView tvTitle;
    private EditText edtName, edtEmail, edtAddress, edtDescription;
    private Button btnLogout, btnSave;

    private static final String ARG_OWNER_NAME = "owner_name";

    public static ProfileFragment newInstance(String ownerName) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_OWNER_NAME, ownerName);
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

        tvTitle        = view.findViewById(R.id.tv_profile_title);
        edtName        = view.findViewById(R.id.edt_prof_name);
        edtEmail       = view.findViewById(R.id.edt_prof_email);
        edtAddress     = view.findViewById(R.id.edt_prof_address);
        edtDescription = view.findViewById(R.id.edt_prof_description);
        btnSave        = view.findViewById(R.id.btn_save);
        btnLogout      = view.findViewById(R.id.btn_logout);

        String ownerName = getArguments() != null
                ? getArguments().getString(ARG_OWNER_NAME, null)
                : null;

        if (ownerName != null && !ownerName.equals(UserData.registeredName)) {
            displayOtherProfile(ownerName);
        } else {
            displayMyProfile();

            btnSave.setOnClickListener(v -> {
                String newName        = edtName.getText().toString().trim();
                String newAddress     = edtAddress.getText().toString().trim();
                String newDescription = edtDescription.getText().toString().trim();

                if (UserData.userId == null || UserData.userId.isEmpty()) {
                    UserData.registeredName = newName;
                    UserData.Address = newAddress;
                    UserData.Description = newDescription;
                    tvTitle.setText("Hello, " + UserData.registeredName + "!");
                    Toast.makeText(requireContext(), "Updated!", Toast.LENGTH_SHORT).show();
                    return;
                }

                ApiClient.api().updateProfile(
                        UserData.userId,
                        new UpdateProfileRequest(newName, newAddress, UserData.AvatarUrl, newDescription, UserData.Phone)
                ).enqueue(new Callback<UpdateProfileResponse>() {
                    @Override
                    public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
                        if (!response.isSuccessful() || response.body() == null || response.body().user == null) {
                            Toast.makeText(requireContext(), "Update thất bại!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        UserData.registeredName = response.body().user.name != null ? response.body().user.name : newName;
                        UserData.Address = response.body().user.address != null ? response.body().user.address : newAddress;
                        UserData.Description = response.body().user.description != null ? response.body().user.description : newDescription;
                        UserData.Phone = response.body().user.phone != null ? response.body().user.phone : UserData.Phone;
                        UserData.AvatarUrl = response.body().user.avatarUrl != null ? response.body().user.avatarUrl : UserData.AvatarUrl;

                        tvTitle.setText("Hello, " + UserData.registeredName + "!");
                        Toast.makeText(requireContext(), "Updated!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
                        Toast.makeText(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void displayMyProfile() {
        tvTitle.setText("Hello, " + UserData.registeredName + "!");
        edtName.setText(UserData.registeredName);
        edtEmail.setText(UserData.registeredEmail);
        edtAddress.setText(UserData.Address);
        edtDescription.setText(UserData.Description);

        btnSave.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.VISIBLE);

        edtName.setEnabled(true);
        edtEmail.setEnabled(true);
        edtAddress.setEnabled(true);
        edtDescription.setEnabled(true);

        if (UserData.userId != null && !UserData.userId.isEmpty()) {
            ApiClient.api().getProfile(UserData.userId).enqueue(new Callback<ProfileResponse>() {
                @Override
                public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                    if (!response.isSuccessful() || response.body() == null || response.body().user == null) return;

                    UserData.registeredName = response.body().user.name != null ? response.body().user.name : UserData.registeredName;
                    UserData.registeredEmail = response.body().user.email != null ? response.body().user.email : UserData.registeredEmail;
                    UserData.Address = response.body().user.address != null ? response.body().user.address : UserData.Address;
                    UserData.Description = response.body().user.description != null ? response.body().user.description : UserData.Description;
                    UserData.Phone = response.body().user.phone != null ? response.body().user.phone : UserData.Phone;
                    UserData.AvatarUrl = response.body().user.avatarUrl != null ? response.body().user.avatarUrl : UserData.AvatarUrl;

                    tvTitle.setText("Hello, " + UserData.registeredName + "!");
                    edtName.setText(UserData.registeredName);
                    edtEmail.setText(UserData.registeredEmail);
                    edtAddress.setText(UserData.Address);
                    edtDescription.setText(UserData.Description);
                }

                @Override
                public void onFailure(Call<ProfileResponse> call, Throwable t) {}
            });
        }
    }

    private void displayOtherProfile(String name) {
        String email = "", address = "", description = "";
        for (String[] user : UserData.userList) {
            if (user[0].equals(name)) {
                email       = user[1];
                address     = user.length > 3 ? user[3] : "";
                description = user.length > 4 ? user[4] : "";
                break;
            }
        }

        tvTitle.setText(name + "!");
        edtName.setText(name);
        edtEmail.setText(email);
        edtAddress.setText(address.isEmpty() ? "Secret Location" : address);
        edtDescription.setText(description.isEmpty() ? "No description available." : description);

        btnSave.setVisibility(View.GONE);
        btnLogout.setVisibility(View.GONE);

        edtName.setEnabled(false);
        edtEmail.setEnabled(false);
        edtAddress.setEnabled(false);
        edtDescription.setEnabled(false);
    }
}