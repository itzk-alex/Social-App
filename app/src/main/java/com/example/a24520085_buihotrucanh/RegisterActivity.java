package com.example.a24520085_buihotrucanh;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a24520085_buihotrucanh.network.ApiClient;
import com.example.a24520085_buihotrucanh.network.models.RegisterRequest;
import com.example.a24520085_buihotrucanh.network.models.RegisterResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPhone, edtPassword, edtConfirmPassword;
    private Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtName = findViewById(R.id.edt_reg_name);
        edtEmail = findViewById(R.id.edt_reg_email);
        edtPhone = findViewById(R.id.edt_reg_phone);
        edtPassword = findViewById(R.id.edt_reg_password);
        edtConfirmPassword = findViewById(R.id.edt_reg_confirm_password); // thêm dòng này
        btnCreate = findViewById(R.id.btn_create);

        btnCreate.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phoneRaw = edtPhone.getText().toString().trim();
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

            String phoneDigits = phoneRaw.replaceAll("\\s+", "");
            if (phoneDigits.isEmpty()) {
                Toast.makeText(this, "Please input phone number!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!phoneDigits.matches("^[0-9]{9,15}$")) {
                Toast.makeText(this, "Invalid phone number (9–15 numbers)!", Toast.LENGTH_SHORT).show();
                return;
            }

            String passwordPattern = "^(?=.*[A-Z])(?=.*[0-9]).{6,}$";
            if (!password.matches(passwordPattern)) {
                Toast.makeText(this, "Password need to have at least 6 letters, 1 capital letter, 1 number", Toast.LENGTH_LONG).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Confirm password do not match!", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiClient.api().register(new RegisterRequest(name, email, phoneDigits, password)).enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                    if (!response.isSuccessful()) {
                        String msg = "Registration failed!";
                        try {
                            if (response.errorBody() != null) {
                                String err = response.errorBody().string().trim();
                                if (!err.isEmpty() && err.length() <= 300) {
                                    msg = err;
                                }
                            }
                        } catch (IOException ignored) { }
                        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (response.body() == null) {
                        Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    RegisterResponse res = response.body();
                    if (res.status != null && !res.status.equalsIgnoreCase("success") && !res.status.equalsIgnoreCase("ok")) {
                        Toast.makeText(RegisterActivity.this, res.message != null ? res.message : "Registration failed!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(RegisterActivity.this, "Registration succesful!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Internet connection failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}