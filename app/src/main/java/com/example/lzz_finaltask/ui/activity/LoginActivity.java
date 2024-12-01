package com.example.lzz_finaltask.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lzz_finaltask.model.User;
import com.example.lzz_finaltask.network.RetrofitManager;
import com.example.lzz_finaltask.network.response.BaseResponse;
import com.example.lzz_finaltask.R;
import com.example.lzz_finaltask.network.request.UserLoginRequest;
import com.example.lzz_finaltask.utils.GsonUtil;
import com.example.lzz_finaltask.utils.NavigationUtils;
import com.example.lzz_finaltask.utils.SharedPreferencesUtil;
import com.google.gson.internal.LinkedTreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText edUserName;
    EditText edUserPassword;
    Button loginButton;
    TextView toRegister;
    TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initViews();
        setClickListeners();
    }

    private void setClickListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edUserName.getText().toString();
                String password = edUserPassword.getText().toString();
                performLogin(username,password);
            }
        });

        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationUtils.navigateTo(LoginActivity.this, RegisterActivity.class);
            }
        });
    }

    private void performLogin(String username,String password){
        Call<BaseResponse> call = RetrofitManager
                .getApiService()
                .userLogin(new UserLoginRequest(username, password));
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                BaseResponse baseResponse = response.body();
                if (baseResponse.getCode() == 0) {
                    User user = GsonUtil.trans((LinkedTreeMap<String, Object>) baseResponse.getData(),User.class);
                    SharedPreferencesUtil.saveLoginState(LoginActivity.this,true,user);
                    NavigationUtils.navigateWithClearTask(LoginActivity.this, MainActivity.class);
                }
                else{
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText(baseResponse.getDescription());
                }
            }
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                t.printStackTrace();
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText("服务器繁忙");
            }
        });
    }

    private void initViews() {
        edUserName = findViewById(R.id.ed_userName);
        edUserPassword = findViewById(R.id.ed_userPassword);
        loginButton = findViewById(R.id.ed_login);
        toRegister = findViewById(R.id.tv_to_register);
        errorMessage = findViewById(R.id.log_tv_error_message);
        errorMessage.setVisibility(View.GONE);
    }


}