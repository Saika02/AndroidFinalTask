package com.example.lzz_finaltask.network;

import com.example.lzz_finaltask.network.request.UserLoginRequest;
import com.example.lzz_finaltask.network.request.UserRegisterRequest;
import com.example.lzz_finaltask.network.response.BaseResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {
    @POST("user/login")
    Call<BaseResponse> userLogin(@Body UserLoginRequest userLoginRequest);

    @POST("user/register")
    Call<BaseResponse> userRegister(@Body UserRegisterRequest userRegisterRequest);

    @Multipart
    @POST("file/upload/avatar")  // 这里改成你后端的实际接口地址
    Call<BaseResponse> uploadAvatar(
            @Part MultipartBody.Part file,
            @Part("userId") RequestBody userId
    );

    @GET("news/list")
    Call<BaseResponse> getNewsList();

    @GET("news/detail")
    Call<BaseResponse> getNewsDetail(@Query("url") String url);
}
