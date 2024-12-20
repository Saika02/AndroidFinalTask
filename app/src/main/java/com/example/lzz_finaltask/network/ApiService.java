package com.example.lzz_finaltask.network;

import com.example.lzz_finaltask.model.Comment;
import com.example.lzz_finaltask.network.request.AddNewsRequest;
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

    @GET("user/getUserById")
    Call<BaseResponse> getUserById(@Query("userId") Long userId);

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
    Call<BaseResponse> addUserFavorite(@Query("userId") Long userId, @Query("newsId") Long newsId);

    @POST("user/removeUserFavorite")
    Call<BaseResponse> removeUserFavorite(@Query("userId") Long userId, @Query("newsId") Long newsId);

    @POST("user/addHistory")
    Call<BaseResponse> addHistory(@Query("userId") Long userId, @Query("newsId") Long newsId);

//    @POST("news/sendComment")
//    Call<BaseResponse> sendComment(@Query("userId") Long userId, @Query("newsId") Long newsId, @Query("comment") String comment);

    @POST("news/sendComment")
    Call<BaseResponse> sendComment(@Body Comment comment);

    @DELETE("user/clearHistories")
    Call<BaseResponse> clearHistories(@Query("userId") Long userId);


    @DELETE("user/removeOneHistory")
    Call<BaseResponse> removeOneHistory(@Query("userId") Long userId, @Query("newsId") Long newsId);


    @GET("news/list")
    Call<BaseResponse> getNewsList();

    @GET("news/getNewsByType")
    Call<BaseResponse> getNewsByType(@Query("newsType") String newsType);

    @GET("news/checkIsFavorite")
    Call<BaseResponse> checkIsFavorite(@Query("userId") Long userId, @Query("newsId") Long newsId);

    @GET("news/getFavoriteNews")
    Call<BaseResponse> getFavoriteNews(@Query("userId") Long userId);

    @GET("news/getBrowsingHistories")
    Call<BaseResponse> getBrowsingHistories(@Query("userId") Long userId);

    @GET("news/content")
    Call<BaseResponse> getNewsContent(@Query("id") Long newsId);

    @GET("news/search")
    Call<BaseResponse> searchNews(@Query("keyword") String keyword);

    @GET("news/getNewsComment")
    Call<BaseResponse> getNewsComment(@Query("newsId") Long newsId);

    @DELETE("news/delComment")
    Call<BaseResponse> deleteComment(@Query("commentId") Long commentId);

    @GET("admin/banUser")
    Call<BaseResponse> banUser(@Query("userId") Long userId, @Query("adminId") Long adminId, @Query("reason") String reason);

    @GET("admin/getBannedUsers")
    Call<BaseResponse> getBannedUsers(@Query("adminId") Long adminId);

    @GET("admin/unbanUser")
    Call<BaseResponse> unbanUser(@Query("userId") Long userId);


    @POST("admin/addNews")
    Call<BaseResponse> addNews(@Body AddNewsRequest addNewsRequest);

    @DELETE("admin/deleteNews")
    Call<BaseResponse> deleteNews(@Query("newsId") Long newsId);

}
