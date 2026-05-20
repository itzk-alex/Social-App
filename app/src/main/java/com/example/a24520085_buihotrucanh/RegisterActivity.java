package com.example.a24520085_buihotrucanh;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a24520085_buihotrucanh.network.ApiClient;
import com.example.a24520085_buihotrucanh.network.models.RegisterRequest;
import com.example.a24520085_buihotrucanh.network.models.RegisterResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPassword, edtConfirmPassword;
    private Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtName = findViewById(R.id.edt_reg_name);
        edtEmail = findViewById(R.id.edt_reg_email);
        edtPassword = findViewById(R.id.edt_reg_password);
        edtConfirmPassword = findViewById(R.id.edt_reg_confirm_password); // thêm dòng này
        btnCreate = findViewById(R.id.btn_create);

        btnCreate.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString();
            String confirmPassword = edtConfirmPassword.getText().toString();

            if (name.contains(" ")) {
                Toast.makeText(this, "Username must not have blank space!", Toast.LENGTH_SHORT).show();
                return;
            }

            String emailPattern = "^[a-zA-Z0-9._%+-]+@(gmail|yahoo|outlook|hotmail|icloud)\\.[a-zA-Z]{2,6}$";
            if (!email.matches(emailPattern)) {
                Toast.makeText(this, "Email invalid!", Toast.LENGTH_SHORT).show();
                return;
            }

            String passwordPattern = "^(?=.*[A-Z])(?=.*[0-9]).{6,}$";
            if (!password.matches(passwordPattern)) {
                Toast.makeText(this, "Password phải có ít nhất 6 ký tự, gồm 1 chữ hoa, 1 số", Toast.LENGTH_LONG).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            // API yêu cầu phone, UI hiện tại chưa có nên gửi rỗng.
            ApiClient.api().register(new RegisterRequest(name, email, "", password)).enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    RegisterResponse res = response.body();
                    if (res.status != null && !res.status.equalsIgnoreCase("success") && !res.status.equalsIgnoreCase("ok")) {
                        Toast.makeText(RegisterActivity.this, res.message != null ? res.message : "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}