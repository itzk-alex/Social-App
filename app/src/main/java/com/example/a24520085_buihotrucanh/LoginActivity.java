package com.example.a24520085_buihotrucanh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a24520085_buihotrucanh.network.ApiClient;
import com.example.a24520085_buihotrucanh.network.models.LoginRequest;
import com.example.a24520085_buihotrucanh.network.models.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnSignIn;
    private TextView tvGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edt_login_email);
        edtPassword = findViewById(R.id.edt_login_password);
        btnSignIn = findViewById(R.id.btn_sign_in);
        tvGoToRegister = findViewById(R.id.tv_go_to_register);

        btnSignIn.setOnClickListener(v -> {
            String email = edtEmail.getText().toString();
            String pass = edtPassword.getText().toString();

            ApiClient.api().login(new LoginRequest(email, pass)).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (!response.isSuccessful() || response.body() == null || response.body().user == null) {
                        Toast.makeText(LoginActivity.this, "Không thể đăng nhập!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    LoginResponse res = response.body();
                    UserData.userId = res.user.id != null ? res.user.id : "";
                    UserData.registeredName = res.user.name != null ? res.user.name : "";
                    UserData.registeredEmail = res.user.email != null ? res.user.email : email;
                    UserData.registeredPassword = pass;
                    UserData.Address = res.user.address != null ? res.user.address : "";
                    UserData.Description = res.user.description != null ? res.user.description : "";
                    UserData.Phone = res.user.phone != null ? res.user.phone : "";
                    UserData.AvatarUrl = res.user.avatarUrl != null ? res.user.avatarUrl : "";

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}