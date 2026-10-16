package com.walletapp.th3;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class LoginActivity extends BaseActivity {
    // Tài khoản cố định theo đề bài
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "123456";

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        applySystemInsets();

        EditText usernameView = findViewById(R.id.edit_username);
        EditText passwordView = findViewById(R.id.edit_password);
        TextView errorView = findViewById(R.id.text_login_error);
        ImageButton togglePasswordButton = findViewById(R.id.button_toggle_password);

        // Bật/tắt ẩn hiện mật khẩu
        togglePasswordButton.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                passwordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                togglePasswordButton.setImageResource(R.drawable.ic_visibility_off);
            } else {
                passwordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                togglePasswordButton.setImageResource(R.drawable.ic_visibility);
            }
            passwordView.setSelection(passwordView.getText().length());
        });

        // Xử lý nút đăng nhập
        findViewById(R.id.button_login).setOnClickListener(view -> {
            String username = usernameView.getText().toString().trim();
            String password = passwordView.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                errorView.setText(R.string.login_empty);
                errorView.setVisibility(View.VISIBLE);
                return;
            }

            if (USERNAME.equals(username) && PASSWORD.equals(password)) {
                errorView.setVisibility(View.GONE);
                startActivity(new Intent(this, MainActivity.class));
                finish(); // Đóng LoginActivity để nút Back không quay lại
            } else {
                errorView.setText(R.string.login_incorrect);
                errorView.setVisibility(View.VISIBLE);
            }
        });
    }
}
