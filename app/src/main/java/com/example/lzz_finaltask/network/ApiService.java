package com.example.lzz_finaltask.network;

import com.example.lzz_finaltask.network.request.UserLoginRequest;
import com.example.lzz_finaltask.network.request.UserRegisterRequest;
import com.example.lzz_finaltask.network.response.BaseResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("user/login")
    Call<BaseResponse> userLogin(@Body UserLoginRequest userLoginRequest);

    @POST("user/register")
    Call<BaseResponse> userRegister(@Body UserRegisterRequest userRegisterRequest);

    @GET("news/list")
    Call<BaseResponse> getNewsList();
}
