package com.example.lzz_finaltask.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lzz_finaltask.R;
import com.example.lzz_finaltask.model.NewsType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewsTypeAdapter extends RecyclerView.Adapter<NewsTypeAdapter.NewsTypeViewHolder> {
    // 新闻类型常量
    public static final String TYPE_YAOWEN = "yaowen20200213";
    public static final String TYPE_GUONEI = "guonei";
    public static final String TYPE_GUOJI = "guoji";
    public static final String TYPE_WAR = "war";
    public static final String TYPE_TECH = "tech";
    public static final String TYPE_MONEY = "money";
    public static final String TYPE_SPORTS = "sports";
    public static final String TYPE_ENT = "ent";

    // 新闻类型描述
    public static final String DESC_YAOWEN = "要闻";
    public static final String DESC_GUONEI = "国内";
    public static final String DESC_GUOJI = "国际";
    public static final String DESC_WAR = "军事";
    public static final String DESC_TECH = "科技";
    public static final String DESC_MONEY = "财经";
    public static final String DESC_SPORTS = "体育";
    public static final String DESC_ENT = "娱乐";

    private Context context;
    private OnItemClickListener onItemClickListener;
    private List<NewsType> newsTypes;

    public NewsTypeAdapter(Context context) {
        this.context = context;
        initDefaultNewsTypes();
    }

    private void initDefaultNewsTypes() {
        newsTypes = new ArrayList<>();
        // 初始化固定的新闻类型
        newsTypes.add(createNewsType(TYPE_YAOWEN, DESC_YAOWEN, R.drawable.ic_yaowen));
        newsTypes.add(createNewsType(TYPE_GUONEI, DESC_GUONEI, R.drawable.ic_guonei));
        newsTypes.add(createNewsType(TYPE_GUOJI, DESC_GUOJI, R.drawable.ic_guoji));
        newsTypes.add(createNewsType(TYPE_WAR, DESC_WAR, R.drawable.ic_war));
        newsTypes.add(createNewsType(TYPE_TECH, DESC_TECH, R.drawable.ic_tech));
        newsTypes.add(createNewsType(TYPE_MONEY, DESC_MONEY, R.drawable.ic_money));
        newsTypes.add(createNewsType(TYPE_SPORTS, DESC_SPORTS, R.drawable.ic_sports));
        newsTypes.add(createNewsType(TYPE_ENT, DESC_ENT, R.drawable.ic_ent));
    }

    private NewsType createNewsType(String type, String desc, int iconResId) {
        NewsType newsType = new NewsType();
        newsType.setType(type);
        newsType.setTypeDesc(desc);
        newsType.setIconResId(iconResId); // 修改NewsType类，使用本地资源ID而不是URL
        return newsType;
    }

    @NonNull
    @Override
    public NewsTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_newstype, parent, false);
        return new NewsTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsTypeViewHolder holder, int position) {
        NewsType newsType = newsTypes.get(position);
        holder.tvType.setText(newsType.getTypeDesc());
        holder.ivIcon.setImageResource(newsType.getIconResId());

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(newsType, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsTypes.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    static class NewsTypeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvType;

        public NewsTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvType = itemView.findViewById(R.id.tv_type);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(NewsType newsType, int position);
    }
}