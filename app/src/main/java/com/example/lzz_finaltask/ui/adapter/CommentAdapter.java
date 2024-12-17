package com.example.lzz_finaltask.ui.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lzz_finaltask.R;
import com.example.lzz_finaltask.model.Comment;
import com.example.lzz_finaltask.model.User;
import com.example.lzz_finaltask.network.RetrofitManager;
import com.example.lzz_finaltask.network.response.BaseResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private List<Comment> comments = new ArrayList<>();
    private User currentUser;

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public CommentAdapter(User currentUser) {
        this.currentUser = currentUser;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.userName.setText(comment.getUsername());
        holder.commentTime.setText(comment.getCreateTime());
        holder.commentContent.setText(comment.getContent());

        // 加载头像
        Glide.with(holder.userAvatar)
                .load(RetrofitManager.getBaseUrl() + comment.getAvatarUrl())
                .into(holder.userAvatar);

        // 设置长按事件
        holder.itemView.setOnLongClickListener(v -> {
            showOperationDialog(v.getContext(), comment, position);
            return true;
        });
    }

    private void showOperationDialog(Context context, Comment comment, int position) {
        // 判断是否有权限操作（管理员或评论作者）
        boolean isAdmin = currentUser != null && currentUser.getRole() == 1;
        boolean isCommentOwner = currentUser != null &&
                currentUser.getUserId().equals(comment.getUserId());

        if (!isAdmin && !isCommentOwner) {
            return; // 无权限操作
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        List<String> options = new ArrayList<>();

        // 添加可用的操作选项
        options.add("删除评论");
        if (isAdmin) {
            options.add("封禁用户");
        }

        builder.setItems(options.toArray(new String[0]), (dialog, which) -> {
            switch (which) {
                case 0: // 删除评论
                    deleteComment(context, comment, position);
                    break;
                case 1: // 封禁用户（仅管理员可见）
                    if (isAdmin) {
                        showBanReasonDialog(context, comment.getUserId(), comment.getUsername());
                    }
                    break;
            }
        });

        builder.show();
    }

    // 显示封禁理由输入对话框
    private void showBanReasonDialog(Context context, Long userId, String username) {
        // 使用 null 作为 parent
        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_ban_reason, null, false);

        EditText input = dialogView.findViewById(R.id.edit_ban_reason);

        // 创建并显示对话框
        new AlertDialog.Builder(context)
                .setTitle("封禁用户: " + username)
                .setView(dialogView)  // 设置自定义视图
                .setPositiveButton("确定", (dialog, which) -> {
                    String reason = input.getText().toString().trim();
                    if (reason.isEmpty()) {
                        Toast.makeText(context, "请输入封禁理由", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    banUser(context, userId, currentUser.getUserId(), reason);
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    // 修改封禁用户的方法，添加理由参数
    private void banUser(Context context, Long userId, Long adminId, String reason) {
        Call<BaseResponse> call = RetrofitManager.getApiService().banUser(userId, adminId, reason);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body() != null && response.body().getCode() == 0) {
                    Toast.makeText(context, "封禁成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,
                            "操作失败：" + response.body().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(context, "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteComment(Context context, Comment comment, int position) {
        Call<BaseResponse> call = RetrofitManager.getApiService().deleteComment(comment.getId());
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body() != null && response.body().getCode() == 0) {
                    // 先找到评论的当前位置
                    int currentPosition = comments.indexOf(comment);
                    if (currentPosition != -1) {  // 确保评论还在列表中
                        comments.remove(currentPosition);
                        notifyItemRemoved(currentPosition);
                        // 更新后面项目的位置
                        notifyItemRangeChanged(currentPosition, getItemCount());
                    }
                    Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,
                            "删除失败：" + response.body().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(context, "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView userName;
        TextView commentTime;
        TextView commentContent;

        ViewHolder(View view) {
            super(view);
            userAvatar = view.findViewById(R.id.user_avatar);
            userName = view.findViewById(R.id.user_name);
            commentTime = view.findViewById(R.id.comment_time);
            commentContent = view.findViewById(R.id.comment_content);
        }
    }
}