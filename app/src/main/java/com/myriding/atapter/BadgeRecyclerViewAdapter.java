package com.myriding.atapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myriding.R;
import com.myriding.model.Badge;

import java.util.ArrayList;
import java.util.List;

public class BadgeRecyclerViewAdapter extends RecyclerView.Adapter<BadgeRecyclerViewAdapter.MyViewHolder>{

    private final Context mContext;
    private List<Badge> mData = new ArrayList<>();

    public BadgeRecyclerViewAdapter(Context context) {
        this.mContext = context;
    }

    public BadgeRecyclerViewAdapter(Context context, List<Badge> data) {
        this.mContext = context;
        mData = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item_badge, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.img_badgeImage.setImageResource(mData.get(position).getImg());
        holder.tv_badgeTitle.setText(mData.get(position).getName());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_badgeTitle;
        private ImageView img_badgeImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            img_badgeImage = (ImageView) itemView.findViewById(R.id.img_badge);
            tv_badgeTitle = (TextView) itemView.findViewById(R.id.tv_badge_title);
        }
    }

    public void removeItem(int position) {
        if(position < getItemCount()) {
            mData.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addItem(int position, Badge badge) {
        position = position == -1 ? getItemCount() : position;
        mData.add(position, badge);
        notifyItemInserted(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
