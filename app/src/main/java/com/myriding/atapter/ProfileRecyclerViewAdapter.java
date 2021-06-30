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
import com.myriding.model.BadgePreview;

import java.util.List;

public class ProfileRecyclerViewAdapter extends RecyclerView.Adapter<ProfileRecyclerViewAdapter.MyViewHolder> {
    Context mContext;
    List<BadgePreview> mData;

    public ProfileRecyclerViewAdapter(Context mContext, List<BadgePreview> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ProfileRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.item_profile, parent, false);
        final ProfileRecyclerViewAdapter.MyViewHolder vHolder = new ProfileRecyclerViewAdapter.MyViewHolder(v);

        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.tv_numOfBadge.setText(mData.get(position).getNumOfBadgeToString());
        holder.tv_title.setText(mData.get(position).getBadgeType());
        holder.img.setImageResource(mData.get(position).getBadgeImage());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title, tv_numOfBadge;
        private ImageView img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = (TextView) itemView.findViewById(R.id.kind_badge);
            tv_numOfBadge = (TextView) itemView.findViewById(R.id.number_badge);
            img = (ImageView) itemView.findViewById(R.id.img_badge);
        }
    }
}
