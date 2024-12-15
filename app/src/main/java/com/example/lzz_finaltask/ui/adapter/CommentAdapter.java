package com.example.lzz_finaltask.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lzz_finaltask.R;
import com.example.lzz_finaltask.model.Comment;
import com.example.lzz_finaltask.network.RetrofitManager;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private List<Comment> comments = new ArrayList<>();

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
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

        Glide.with(holder.userAvatar).load(RetrofitManager.getBaseUrl()+comment.getAvatarUrl()).into(holder.userAvatar);
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