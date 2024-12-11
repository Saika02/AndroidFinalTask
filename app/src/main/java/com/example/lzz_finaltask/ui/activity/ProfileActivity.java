package com.example.lzz_finaltask.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.lzz_finaltask.R;
import com.example.lzz_finaltask.model.User;
import com.example.lzz_finaltask.network.RetrofitManager;
import com.example.lzz_finaltask.network.response.BaseResponse;
import com.example.lzz_finaltask.utils.GsonUtil;
import com.example.lzz_finaltask.utils.NavigationUtils;
import com.example.lzz_finaltask.utils.SharedPreferencesUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private ShapeableImageView ivAvatar;
    private TextView tvUsername;
    private TextView tvUserId;
    private MaterialButton btnLogout;
    private LinearLayout llFavorites;
    private LinearLayout llHistory;
    private BottomNavigationView bottomNavigationView;
    private ShapeableImageView avatarImageView;
    private static final int PICK_IMAGE = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_info);

        initViews();
        loadUserInfo();
        setClickListeners();
    }


    private void initViews() {
        ivAvatar = findViewById(R.id.iv_avatar);
        tvUsername = findViewById(R.id.tv_username);
        tvUserId = findViewById(R.id.tv_user_id);
        btnLogout = findViewById(R.id.btn_logout);
        llFavorites = findViewById(R.id.ll_favorites);
        llHistory = findViewById(R.id.ll_history);
        bottomNavigationView = findViewById(R.id.person_bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
        avatarImageView = findViewById(R.id.iv_avatar);
    }

    private void loadUserInfo() {
        // 从SharedPreferences获取用户信息
        User user = SharedPreferencesUtil.getUser(ProfileActivity.this);
        tvUsername.setText(user.getUsername());
        tvUserId.setText("ID: " + user.getUserId());

        Glide.with(this)
                .load(RetrofitManager.getBaseUrl()+user.getAvatarUrl())
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.ic_default_avatar)
                .centerCrop()
                .into(ivAvatar);
    }

    private void setClickListeners() {
        // 我的收藏
        llFavorites.setOnClickListener(v -> {
            NavigationUtils.navigateTo(ProfileActivity.this,FavoriteActivity.class);
        });

        // 浏览历史
        llHistory.setOnClickListener(v -> {
            NavigationUtils.navigateTo(ProfileActivity.this,HistoryActivity.class);
        });
        // 退出登录
        btnLogout.setOnClickListener(v -> showLogoutDialog());


        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_profile) {
                return true;
            } else if (itemId == R.id.navigation_home) {
                NavigationUtils.navigateWithClearTask(ProfileActivity.this, MainActivity.class);
                return true;
            }
            else if(itemId == R.id.navigation_explore){
                NavigationUtils.navigateWithClearTask(ProfileActivity.this,DiscoveryActivity.class);
                return true;
            }
            return false;
        });

        ivAvatar.setOnClickListener(v ->
                showSelectPictrueDialog()
        );

    }

    private void showLogoutDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("退出登录")
                .setMessage("确定要退出登录吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", (dialog, which) -> {
                    SharedPreferencesUtil.clearLoginState(ProfileActivity.this);
                    NavigationUtils.navigateWithClearTask(ProfileActivity.this, LoginActivity.class);
                })
                .show();
    }

    private void showSelectPictrueDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("上传头像")
                .setMessage("是否需要上传头像")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  // 使用 MediaStore
                        pickImage.launch(intent);
                    }
                }).show();
    }

    private void uploadAvatar(Uri imageUri){
        try {
            // 使用 Okio 读取文件
            BufferedSource source = Okio.buffer(Okio.source(getContentResolver().openInputStream(imageUri)));
            byte[] bytes = source.readByteArray();
            source.close();
            User user = SharedPreferencesUtil.getUser(ProfileActivity.this);
            String fileName = "avatar_"+user.getUserId()+"_"+System.currentTimeMillis()+".jpg";
            RequestBody fileBody = RequestBody.create(
                    MediaType.parse(getContentResolver().getType(imageUri)),
                    bytes
            );
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file",fileName,fileBody);
            RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(user.getUserId()));
            Call<BaseResponse> call = RetrofitManager.getApiService().uploadAvatar(filePart, userId);
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    BaseResponse body = response.body();
                    if(body.getCode() == 0){
                        String avatarUrl =  (String) body.getData();
                        user.setAvatarUrl(avatarUrl);
                        SharedPreferencesUtil.updateUser(ProfileActivity.this,user);
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(ProfileActivity.this,"上传失败",Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    Glide.with(this)
                            .load(selectedImageUri)
                            .centerCrop()
                            .into(ivAvatar);
                    uploadAvatar(selectedImageUri);
                }
            }
    );

} 