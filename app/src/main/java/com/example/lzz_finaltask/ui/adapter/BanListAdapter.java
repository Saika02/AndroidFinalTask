package com.example.lzz_finaltask.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lzz_finaltask.R;
import com.example.lzz_finaltask.model.BanRecord;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class BanListAdapter extends RecyclerView.Adapter<BanListAdapter.ViewHolder> {
    private List<BanRecord> banRecords = new ArrayList<>();
    private OnUnbanClickListener onUnbanClickListener;

    public interface OnUnbanClickListener {
        void onUnbanClick(BanRecord banRecord, int position);
    }

    public void setOnUnbanClickListener(OnUnbanClickListener listener) {
        this.onUnbanClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ban, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BanRecord record = banRecords.get(position);
        holder.tvUsername.setText(record.getUsername());
        holder.tvReason.setText("原因：" + record.getReason());
        holder.tvAdminInfo.setText("操作人：" + record.getAdminName());
        holder.tvTime.setText("操作时间：" + record.getActionTime());



        holder.btnUnban.setOnClickListener(v -> {
            if (onUnbanClickListener != null) {
                onUnbanClickListener.onUnbanClick(record, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return banRecords.size();
    }

    public void setBanRecords(List<BanRecord> records) {
        this.banRecords = records;
        notifyDataSetChanged();
    }

    public void removeBanRecord(int position) {
        banRecords.remove(position);
        notifyItemRemoved(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername;
        TextView tvReason;
        TextView tvAdminInfo;
        TextView tvTime;
        MaterialButton btnUnban;

        ViewHolder(View view) {
            super(view);
            tvUsername = view.findViewById(R.id.tv_username);
            tvAdminInfo = view.findViewById(R.id.tv_admin_info);
            tvTime = view.findViewById(R.id.tv_ban_time);
            tvReason = view.findViewById(R.id.tv_ban_reason);
            btnUnban = view.findViewById(R.id.btn_unban);
        }
    }
}