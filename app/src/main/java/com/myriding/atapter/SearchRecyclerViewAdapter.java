package com.myriding.atapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myriding.R;
import com.myriding.activity.CourseViewDetailActivity;
import com.myriding.model.Search;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.MyViewHolder> {
    DecimalFormat likeFormat = new DecimalFormat("#,###");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    Context mContext;
    List<Search> mData;

    public SearchRecyclerViewAdapter(Context mContext, List<Search> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public SearchRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search, parent, false);
        final SearchRecyclerViewAdapter.MyViewHolder vHolder = new SearchRecyclerViewAdapter.MyViewHolder(view);

        // <-- 검색결과의 게시물 클릭 이벤트 (코스 상세보기로 이동)
        vHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CourseViewDetailActivity.class);
                intent.putExtra("post_id",  mData.get(vHolder.getAdapterPosition()).getId());
                mContext.startActivity(intent);
            }
        });

        return vHolder;
    }

    public void onBindViewHolder(@NonNull SearchRecyclerViewAdapter.MyViewHolder holder, int position) {
        String imgString = mData.get(position).getCourseImage().substring(22);
        byte[] imageBytes = Base64.decode(imgString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        holder.img_course.setImageBitmap(decodedImage);
        holder.tv_courseName.setText(mData.get(position).getCourseName());
        holder.tv_courseDistance.setText(String.format("%.2f km", mData.get(position).getCourseDistance()));
        holder.tv_courseTime.setText(mData.get(position).getCourseTime() + "분");
        holder.tv_courseLike.setText(likeFormat.format(mData.get(position).getCourseLike()));
        holder.tv_courseCreatedDate.setText(dateFormat.format(mData.get(position).getDate()));
        holder.tv_coursePoint.setText(mData.get(position).getStartPoint() + " ~ " + mData.get(position).getEndPoint());
        holder.tv_courseTryCount.setText(likeFormat.format(mData.get(position).getTryCount()) + "명이 라이딩했어요!");

        holder.tv_coursePoint.setSingleLine(true);
        holder.tv_coursePoint.setEllipsize(TextUtils.TruncateAt.END);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout item;
        private ImageView img_course;
        private TextView tv_courseName;
        private TextView tv_courseDistance;
        private TextView tv_courseTime;
        private TextView tv_courseLike;
        private TextView tv_courseCreatedDate;
        private TextView tv_coursePoint;
        private TextView tv_courseTryCount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            item                 = (RelativeLayout) itemView.findViewById(R.id.search_item_layout);
            img_course           = (ImageView) itemView.findViewById(R.id.search_post_image);
            tv_courseName        = (TextView) itemView.findViewById(R.id.search_course_name);
            tv_courseDistance    = (TextView) itemView.findViewById(R.id.search_course_distance);
            tv_courseTime        = (TextView) itemView.findViewById(R.id.search_course_time);
            tv_courseLike        = (TextView) itemView.findViewById(R.id.search_course_like);
            tv_courseCreatedDate = (TextView) itemView.findViewById(R.id.search_course_date);
            tv_coursePoint       = (TextView) itemView.findViewById(R.id.search_course_point);
            tv_courseTryCount    = (TextView) itemView.findViewById(R.id.search_course_try_num);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}