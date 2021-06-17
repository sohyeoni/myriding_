package com.myriding.atapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
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
import com.myriding.model.MyCourse;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class MyCourseAdapter extends RecyclerView.Adapter<MyCourseAdapter.MyViewHolder> {
    DecimalFormat likeFormat = new DecimalFormat("#,###");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Context mContext;
    List<MyCourse> mData;

    public MyCourseAdapter(Context mContext, List<MyCourse> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyCourseAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_my_course, parent, false);
        final MyCourseAdapter.MyViewHolder vHolder = new MyCourseAdapter.MyViewHolder(v);

        vHolder.item_myCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int postId = mData.get(vHolder.getAdapterPosition()).getId();
//                Toast.makeText(mContext, "Click Popular " + postId, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(view.getContext(), CourseViewDetailActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("post_id", postId);
                mContext.startActivity(intent);
            }
        });

        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyCourseAdapter.MyViewHolder holder, int position) {
        String date = dateFormat.format(mData.get(position).getDate());

        holder.tv_title.setText(mData.get(position).getTitle());
        holder.tv_point.setText(mData.get(position).getStartPoint() + " ~ " + mData.get(position).getEndPoint());
        holder.tv_date.setText(date);
        holder.tv_distance.setText(String.format("%.2f", mData.get(position).getDistance()) + "km");
        holder.tv_time.setText(mData.get(position).getTime() + "분");
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
        private CardView item_myCourse;
        private TextView tv_title, tv_point, tv_date, tv_distance, tv_time, tv_like;
        private ImageView img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            item_myCourse = (CardView) itemView.findViewById(R.id.cardView_my_course);
            tv_title = (TextView) itemView.findViewById(R.id.title_myCourse);
            tv_point = (TextView) itemView.findViewById(R.id.point_myCourse);
            tv_date = (TextView) itemView.findViewById(R.id.date_myCourse);
            tv_distance = (TextView) itemView.findViewById(R.id.distance_myCourse);
            tv_time = (TextView) itemView.findViewById(R.id.time_myCourse);
            tv_like = (TextView) itemView.findViewById(R.id.like_myCourse);
            img = (ImageView) itemView.findViewById(R.id.img_myCourse);

            tv_point.setSingleLine(true);
            tv_point.setEllipsize(TextUtils.TruncateAt.END);
        }
    }
}
