package com.example.lzz_finaltask.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lzz_finaltask.R;
import com.example.lzz_finaltask.model.News;
import com.example.lzz_finaltask.model.User;
import com.example.lzz_finaltask.network.RetrofitManager;
import com.example.lzz_finaltask.network.response.BaseResponse;
import com.example.lzz_finaltask.utils.GsonUtil;
import com.example.lzz_finaltask.utils.SharedPreferencesUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsViewHolder> {
    private List<News> newsList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private User currentUser;


    public static final int PAGE_TYPE_MAIN = 0;      // 主页
    public static final int PAGE_TYPE_FAVORITE = 1;  // 收藏页
    public static final int PAGE_TYPE_HISTORY = 2;   // 历史页

    private int pageType = PAGE_TYPE_MAIN;

    public void setPageType(int pageType) {
        this.pageType = pageType;
    }

    public NewsListAdapter(Context context) {
        this.context = context;
        this.newsList = new ArrayList<>();
        this.currentUser = SharedPreferencesUtil.getUser(context);
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = newsList.get(position);

        if (news.getIsDeleted() == 1) {
            // 新闻已被删除，显示特殊样式
            holder.tvTitle.setText("该新闻已被删除");
            holder.tvType.setVisibility(View.GONE);
            holder.tvTime.setVisibility(View.GONE);
            holder.ivNews.setImageResource(R.drawable.ic_news_deleted);
            holder.itemView.setAlpha(0.5f);  // 降低透明度
            holder.itemView.setOnClickListener(null);  // 禁用点击
        } else {
            // 正常显示新闻
            holder.tvTitle.setText(news.getTitle());
            holder.tvType.setText(news.getTypeDesc());
            holder.tvTime.setText(news.getPublishTime());
            holder.tvType.setVisibility(View.VISIBLE);
            holder.tvTime.setVisibility(View.VISIBLE);
            holder.itemView.setAlpha(1.0f);
            // 使用Glide加载图片
            Glide.with(context)
                    .load(news.getImageUrl())
                    .placeholder(R.drawable.modern_news_cover)
                    .error(R.drawable.modern_news_cover)
                    .into(holder.ivNews);

            // 设置点击事件
            holder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(news, position);
                }
            });
        }



        if (currentUser != null && currentUser.getRole() == 1) {
            holder.itemView.setOnLongClickListener(v -> {
                showOperationDialog(v.getContext(), news, position);
                return true;
            });
        }
    }

    private void showOperationDialog(Context context, News news, int position) {
        // 判断是否有权限操作
        boolean isAdmin = currentUser != null && currentUser.getRole() == 1;

        List<String> options = new ArrayList<>();

        switch (pageType) {
            case PAGE_TYPE_MAIN:
                if (isAdmin) {
                    options.add("删除新闻");
                }
                break;

            case PAGE_TYPE_FAVORITE:
                options.add("取消收藏");
                break;

            case PAGE_TYPE_HISTORY:
                options.add("删除记录");
                break;
        }

        if (options.isEmpty()) return;

        new MaterialAlertDialogBuilder(context)
                .setItems(options.toArray(new String[0]), (dialog, which) -> {
                    switch (pageType) {
                        case PAGE_TYPE_MAIN:
                            if (which == 0 && isAdmin) {
                                deleteNews(context, news, position);
                            }
                            break;

                        case PAGE_TYPE_FAVORITE:
                            if (which == 0) {
                                removeFavorite(context, news, position);
                            } else if (which == 1 && isAdmin) {
                                deleteNews(context, news, position);
                            }
                            break;

                        case PAGE_TYPE_HISTORY:
                            if (which == 0) {
                                removeHistory(context, news, position);
                            } else if (which == 1 && isAdmin) {
                                deleteNews(context, news, position);
                            }
                            break;
                    }
                })
                .show();
    }

    // 取消收藏
    private void removeFavorite(Context context, News news, int position) {
        Long userId = currentUser.getUserId();
        Call<BaseResponse> call = RetrofitManager.getApiService()
                .removeUserFavorite(userId, news.getNewsId());
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body() != null && response.body().getCode() == 0) {
                    newsList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                    Toast.makeText(context, "已取消收藏", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(context, "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 删除浏览记录
    private void removeHistory(Context context, News news, int position) {
        Long userId = currentUser.getUserId();
        Call<BaseResponse> call = RetrofitManager.getApiService()
                .removeOneHistory(userId, news.getNewsId());
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body() != null && response.body().getCode() == 0) {
                    newsList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                    Toast.makeText(context, "已删除记录", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(context, "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteNews(Context context, News news, int position) {
        Call<BaseResponse> call = RetrofitManager.getApiService().deleteNews(news.getNewsId());
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body() != null) {
                    BaseResponse body = response.body();
                    // 先检查code，再检查data
                    if (body.getCode() == 0) {
                        boolean success = Boolean.TRUE.equals(GsonUtil.trans((LinkedTreeMap<String, Object>) body.getData(), Boolean.class));
                        if (success) {
                            // 删除成功
                            newsList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, getItemCount());
                            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                        } else {
                            // 业务层面的失败
                            new MaterialAlertDialogBuilder(context)
                                    .setTitle("删除失败")
                                    .setMessage("该新闻可能已被删除或您没有权限删除")
                                    .setPositiveButton("确定", null)
                                    .show();
                        }
                    } else {
                        // 系统错误
                        String errorMsg = body.getMessage();
                        new MaterialAlertDialogBuilder(context)
                                .setTitle("删除失败")
                                .setMessage(TextUtils.isEmpty(errorMsg) ? "未知错误" : errorMsg)
                                .setPositiveButton("确定", null)
                                .show();
                    }
                } else {
                    Toast.makeText(context, "服务器响应异常", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                new MaterialAlertDialogBuilder(context)
                        .setTitle("网络请求失败")
                        .setMessage("错误信息：" + t.getMessage())
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // ViewHolder类
    static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView ivNews;
        TextView tvTitle;
        TextView tvTime;
        TextView tvType;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivNews = itemView.findViewById(R.id.iv_news);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvType = itemView.findViewById(R.id.tv_type);
        }
    }

    // 点击事件接口
    public interface OnItemClickListener {
        void onItemClick(News news, int position);
    }
}