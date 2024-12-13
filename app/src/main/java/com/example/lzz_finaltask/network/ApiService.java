package com.example.lzz_finaltask.network;

import com.example.lzz_finaltask.network.request.UserLoginRequest;
import com.example.lzz_finaltask.network.request.UserRegisterRequest;
import com.example.lzz_finaltask.network.response.BaseResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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
    @POST("file/upload/avatar")
    Call<BaseResponse> uploadAvatar(
            @Part MultipartBody.Part file,
            @Part("userId") RequestBody userId
    );


    @POST("user/addUserFavorite")
    Call<BaseResponse> addUserFavorite(@Query("userId") Long userId,@Query("newsId") Long newsId);
    @POST("user/removeUserFavorite")
    Call<BaseResponse> removeUserFavorite(@Query("userId") Long userId,@Query("newsId") Long newsId);
    @POST("user/addHistory")
    Call<BaseResponse> addHistory(@Query("userId") Long userId, @Query("newsId") Long newsId);

    @DELETE("user/clearHistories")
    Call<BaseResponse> clearHistories(@Query("userId") Long userId);




    @GET("news/list")
    Call<BaseResponse> getNewsList();

    @GET("news/getNewsByType")
    Call<BaseResponse>getNewsByType(@Query("newsType") String newsType);

    @GET("news/checkIsFavorite")
    Call<BaseResponse> checkIsFavorite(@Query("userId") Long userId,@Query("newsId") Long newsId);

    @GET("news/getFavoriteNews")
    Call<BaseResponse> getFavoriteNews(@Query("userId") Long userId);
    @GET("news/getBrowsingHistories")
    Call<BaseResponse> getBrowsingHistories(@Query("userId") Long userId);

    @GET("news/content")
    Call<BaseResponse> getNewsContent(@Query("id") Long newsId);

    @GET("news/search")
    Call<BaseResponse> searchNews(@Query("keyword") String keyword);
}
