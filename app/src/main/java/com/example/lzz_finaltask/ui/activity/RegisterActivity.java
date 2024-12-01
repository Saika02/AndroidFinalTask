package com.example.lzz_finaltask.ui.activity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.example.lzz_finaltask.R;
import com.example.lzz_finaltask.network.RetrofitManager;
import com.example.lzz_finaltask.network.request.UserRegisterRequest;
import com.example.lzz_finaltask.network.response.BaseResponse;
import com.example.lzz_finaltask.utils.NavigationUtils;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private TextView toLogin;
    private TextInputLayout tilUsername;    // 改用TextInputLayout
    private TextInputLayout tilPassword;
    private TextInputLayout tilCheckPassword;
    private TextInputEditText edUserName;   // 保留EditText引用
    private TextInputEditText edPassword;
    private TextInputEditText edCheckPassword;
    private TextView errormsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // 初始化视图
        initViews();
        // 设置点击事件
        setClickListeners();
    }

    private void initViews() {
        toLogin = findViewById(R.id.tv_to_login);
        // 初始化TextInputLayout
        tilUsername = findViewById(R.id.til_username);
        tilPassword = findViewById(R.id.til_password);
        tilCheckPassword = findViewById(R.id.til_check_password);
        // 初始化EditText
        edUserName = findViewById(R.id.register_ed_username);
        edPassword = findViewById(R.id.register_ed_password);
        edCheckPassword = findViewById(R.id.register_ed_checkPassword);
        errormsg = findViewById(R.id.register_tv_error);
        errormsg.setVisibility(View.GONE);
    }

    private void setClickListeners() {
        findViewById(R.id.register_btn).setOnClickListener(v -> {
            String username = edUserName.getText().toString();
            String password = edPassword.getText().toString();
            String checkPassword = edCheckPassword.getText().toString();
            if (checkInfo(username, password, checkPassword)) {
                performRegister(username, password);
            }
        });
        toLogin.setOnClickListener(v -> finish());
    }

    private boolean checkInfo(String username, String password, String checkPassword) {
        // 用户名正则：4-8位，只能包含字母、数字和下划线
        String usernamePattern = "^[a-zA-Z0-9_]{4,8}$";
        // 密码正则：6-12位，必须同时包含字母和数字
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,12}$";

        boolean isValid = true;

        // 检查用户名
        if (!Pattern.matches(usernamePattern, username)) {
            tilUsername.setHelperText("用户名格式不正确");
            tilUsername.setError("用户名必须是4-8位的字母、数字或下划线");
            isValid = false;
        } else {
            tilUsername.setHelperText("用户名格式正确");
            tilUsername.setError(null);
            tilUsername.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.correct)));
        }

        // 检查密码
        boolean isPasswordValid = Pattern.matches(passwordPattern, password);
        if (!isPasswordValid) {
            tilPassword.setHelperText("密码格式不正确");
            tilPassword.setError("密码必须是6-12位，包含字母和数字");
            isValid = false;
        } else {
            tilPassword.setHelperText("密码格式正确");
            tilPassword.setError(null);
            tilPassword.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.correct)));
        }

        // 检查确认密码
        if (isPasswordValid) {
            if (!password.equals(checkPassword)) {
                tilCheckPassword.setHelperText("两次输入的密码不一致");
                tilCheckPassword.setError("请确保两次输入的密码相同");
                isValid = false;
            } else {
                tilCheckPassword.setHelperText("密码输入一致");
                tilCheckPassword.setError(null);
                tilCheckPassword.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.correct)));
            }
        } else {
            tilCheckPassword.setHelperText("请先输入正确格式的密码");
            tilCheckPassword.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.text_secondary)));
        }

        return isValid;
    }

    private void performRegister(String username, String password) {
        Call<BaseResponse> call = RetrofitManager
                .getApiService()
                .userRegister(new UserRegisterRequest(username, password));

        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                BaseResponse baseResponse = response.body();
                if (baseResponse.getCode() == 0) {
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    NavigationUtils.navigateWithClearTask(RegisterActivity.this, LoginActivity.class);
                } else {
                    errormsg.setText(baseResponse.getDescription());
                    errormsg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                t.printStackTrace();
                errormsg.setText("服务器繁忙");
                errormsg.setVisibility(View.VISIBLE);
            }
        });
    }
}