package com.myriding.atapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.myriding.R;
import com.myriding.activity.CourseViewDetailActivity;
import com.myriding.model.PopularCourse;

import java.text.DecimalFormat;
import java.util.List;

public class PopularCourseAdapter extends RecyclerView.Adapter<PopularCourseAdapter.MyViewHolder> {
    DecimalFormat likeFormat = new DecimalFormat("#,###");
    private Context mContext;
    List<PopularCourse> mData;

    public PopularCourseAdapter(Context mContext, List<PopularCourse> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public PopularCourseAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_popular_course, parent, false);
        final PopularCourseAdapter.MyViewHolder vHolder = new PopularCourseAdapter.MyViewHolder(v);

        // <-- 인기 라이딩 코스의 게시글 클릭 이벤트
        vHolder.item_popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int postID =  mData.get(vHolder.getAdapterPosition()).getId();
                String postName = mData.get(vHolder.getAdapterPosition()).getTitle();
                // Toast.makeText(mContext, "Click Popular " + postID, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(view.getContext(), CourseViewDetailActivity.class);
                intent.putExtra("post_id", postID);
                intent.putExtra("post_name", postName);
                mContext.startActivity(intent);
            }
        });
        // -->

        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PopularCourseAdapter.MyViewHolder holder, int position) {
        holder.tv_title.setText(mData.get(position).getTitle());
        holder.tv_distance.setText(String.format("%.2f", mData.get(position).getDistance()) + "km");
        holder.tv_like.setText(likeFormat.format(mData.get(position).getLike()));

        String imgString = mData.get(position).getImg().substring(22);
        byte[] imageBytes = Base64.decode(imgString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        holder.img.setImageBitmap(decodedImage);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView item_popular;
        private TextView tv_title, tv_distance, tv_like;
        private ImageView img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            item_popular = (CardView) itemView.findViewById(R.id.cardView_popular_course);

            tv_title = (TextView) itemView.findViewById(R.id.title_popularCourse);
            tv_distance = (TextView) itemView.findViewById(R.id.distance_popularCourse);
            tv_like = (TextView) itemView.findViewById(R.id.like_popularCourse);
            img = (ImageView) itemView.findViewById(R.id.img_popularCourse);
        }
    }
}
