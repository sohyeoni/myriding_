package com.myriding.atapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myriding.R;
import com.myriding.http.RetrofitAPI;
import com.myriding.http.RetrofitClient;
import com.myriding.model.Rank;
import com.myriding.model.RankData;
import com.myriding.model.RankProfileResponse;
import com.myriding.model.Token;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankRecyclerViewAdapter extends RecyclerView.Adapter<RankRecyclerViewAdapter.MyViewHolder> {
    final static String TAG = "RankRecyclerViewAdapter";

    DecimalFormat scoreFormat = new DecimalFormat("#,###");

    Context mContext;
    List<Rank> mData;
    Dialog myDialog;

    TextView tv_profileRankName, tv_profileRankScore, tv_profileRankDistance, tv_profileRankTime, tv_profileRankAvgSpd, tv_profileRankMaxSpd;
    ImageView img_profileRankImg;

    public RankRecyclerViewAdapter(Context mContext, List<Rank> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_rank, parent, false);
        final MyViewHolder vHolder = new MyViewHolder(v);

        /* 랭킹 프로필 상세보기창 설정 */
        myDialog = new Dialog(mContext);
        myDialog.setContentView(R.layout.rank_profile);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        tv_profileRankName = (TextView) myDialog.findViewById(R.id.profile_rank_name);
        tv_profileRankScore = (TextView) myDialog.findViewById(R.id.profile_rank_score);
        tv_profileRankDistance = (TextView) myDialog.findViewById(R.id.profile_rank_distance);
        tv_profileRankTime = (TextView) myDialog.findViewById(R.id.profile_rank_time);
        tv_profileRankAvgSpd = (TextView) myDialog.findViewById(R.id.profile_rank_avgSpd);
        tv_profileRankMaxSpd = (TextView) myDialog.findViewById(R.id.profile_rank_maxSpd);
        img_profileRankImg = (ImageView) myDialog.findViewById(R.id.profile_rank_img);

        vHolder.item_rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int userId =  mData.get(vHolder.getAdapterPosition()).getId();
                getRankProfile(userId, vHolder);
            }
        });

        return vHolder;
    }

    @SuppressLint("Range")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        /* 1 ~ 10위 사용자 정보 표시(본인 순위, 이름, 점수, 프로필 사진) */
        holder.tv_number.setText(mData.get(position).getMyRankNum() + "");
        holder.tv_name.setText(mData.get(position).getNickname());
        holder.tv_score.setText(scoreFormat.format(mData.get(position).getScore()) + "점");

        try {
            String imgString = mData.get(position).getImg().substring(22);
            byte[] imageBytes = Base64.decode(imgString, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.img.setImageBitmap(decodedImage);
        } catch(Exception e) {
            holder.img.setImageResource(R.drawable.img_user);
        }

        /* 랭킹 1 ~ 3위 텍스트 색, Bold 설정 */
        switch (mData.get(position).getMyRankNum()) {
            case 1:
                holder.tv_number.setTextColor(Color.parseColor("#EAC048"));
                holder.tv_name.setTextColor(Color.parseColor("#EAC048"));
                holder.tv_score.setTextColor(Color.parseColor("#EAC048"));
                holder.tv_number.setTypeface(null, Typeface.BOLD);
                holder.tv_name.setTypeface(null, Typeface.BOLD);
                holder.tv_score.setTypeface(null, Typeface.BOLD);
                break;
            case 2:
                holder.tv_number.setTextColor(Color.parseColor("#BDBDBD"));
                holder.tv_name.setTextColor(Color.parseColor("#BDBDBD"));
                holder.tv_score.setTextColor(Color.parseColor("#BDBDBD"));
                holder.tv_number.setTypeface(null, Typeface.BOLD);
                holder.tv_name.setTypeface(null, Typeface.BOLD);
                holder.tv_score.setTypeface(null, Typeface.BOLD);
                break;
            case 3:
                holder.tv_number.setTextColor(Color.parseColor("#CE9067"));
                holder.tv_name.setTextColor(Color.parseColor("#CE9067"));
                holder.tv_score.setTextColor(Color.parseColor("#CE9067"));
                holder.tv_number.setTypeface(null, Typeface.BOLD);
                holder.tv_name.setTypeface(null, Typeface.BOLD);
                holder.tv_score.setTypeface(null, Typeface.BOLD);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout item_rank;
        private TextView tv_number;
        private TextView tv_name;
        private TextView tv_score;
        private ImageView img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            item_rank = (LinearLayout) itemView.findViewById(R.id.rank_item_id);
            tv_number = (TextView) itemView.findViewById(R.id.rank_number);
            tv_name = (TextView) itemView.findViewById(R.id.rank_name);
            tv_score = (TextView) itemView.findViewById(R.id.rank_score);
            img = (ImageView) itemView.findViewById(R.id.rank_img);
        }
    }

    private RetrofitAPI retrofitAPI;
    private void getRankProfile(int userId, MyViewHolder vHolder) {
        retrofitAPI = RetrofitClient.getApiService();

        Call<RankProfileResponse> call = retrofitAPI.getRankProfile(Token.getToken(), userId);
        call.enqueue(new Callback<RankProfileResponse>() {
            @Override
            public void onResponse(Call<RankProfileResponse> call, Response<RankProfileResponse> response) {
                if(response.isSuccessful()) {
                    RankData rankData = response.body().getRanks();
                    if(rankData != null) {
                            mData.get(vHolder.getAdapterPosition()).setDistance(rankData.getDistance());
                            mData.get(vHolder.getAdapterPosition()).setTime(rankData.getTime());
                            mData.get(vHolder.getAdapterPosition()).setAvgSpeed(rankData.getAvgSpeed());
                            mData.get(vHolder.getAdapterPosition()).setMaxSpeed(rankData.getMaxSpeed());
                    }



                    tv_profileRankName.setText(mData.get(vHolder.getAdapterPosition()).getNickname());
                    tv_profileRankScore.setText(scoreFormat.format(mData.get(vHolder.getAdapterPosition()).getScore()) + "점");
                    tv_profileRankDistance.setText(String.format("%.2f", mData.get(vHolder.getAdapterPosition()).getDistance()) + "km");
                    tv_profileRankTime.setText((mData.get(vHolder.getAdapterPosition()).getTime() / 60) + "시간"
                                                + (mData.get(vHolder.getAdapterPosition()).getTime() % 60) + "분");
                    tv_profileRankAvgSpd.setText(String.format("%.2f", mData.get(vHolder.getAdapterPosition()).getAvgSpeed()) + "km/h");
                    tv_profileRankMaxSpd.setText(String.format("%.2f", mData.get(vHolder.getAdapterPosition()).getMaxSpeed()) + "km/h");

                    /* image decode */
                    try {
                        String imgString = mData.get(vHolder.getAdapterPosition()).getImg().substring(22);
                        byte[] imageBytes = Base64.decode(imgString, Base64.DEFAULT);
                        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        img_profileRankImg.setImageBitmap(decodedImage);
                    } catch (Exception e) {
                        img_profileRankImg.setImageResource(R.drawable.img_user);
                    }

                    myDialog.show();
                } else {
                    Toast.makeText(mContext, "랭킹 프로필 획득 실패", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "랭킹 프로필 획득 실패");
                }
            }

            @Override
            public void onFailure(Call<RankProfileResponse> call, Throwable t) {
                Toast.makeText(mContext, "서버 통신 실패", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "서버 통신 실패");
                Log.d(TAG, t.getMessage());
            }
        });
    }
}
