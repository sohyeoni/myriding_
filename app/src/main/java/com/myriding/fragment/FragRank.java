package com.myriding.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myriding.R;
import com.myriding.atapter.RankRecyclerViewAdapter;
import com.myriding.http.RetrofitAPI;
import com.myriding.http.RetrofitClient;
import com.myriding.model.Rank;
import com.myriding.model.RankData;
import com.myriding.model.RankResponse;
import com.myriding.model.Token;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragRank extends Fragment {
    final static String TAG = "FragRank";

    private View view;

    private RecyclerView myrecyclerview;
    private RankRecyclerViewAdapter recyclerAdapter;
    private List<Rank> lstRank;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_rank, container, false);

        myrecyclerview = (RecyclerView) view.findViewById(R.id.rank_recyclerview);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lstRank = new ArrayList<>();
        getRank();
    }

    private RetrofitAPI retrofitAPI;
    private void getRank() {
        retrofitAPI = RetrofitClient.getApiService();

        Call<RankResponse> call = retrofitAPI.getRank(Token.getToken());
        call.enqueue(new Callback<RankResponse>() {
            @Override
            public void onResponse(Call<RankResponse> call, Response<RankResponse> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getContext(), "랭킹 획득 성공", Toast.LENGTH_SHORT).show();
                    List<RankData> rankData = response.body().getRanks();

                    if(rankData != null) setRankList(rankData);

                    recyclerAdapter = new RankRecyclerViewAdapter(getContext(), lstRank);
                    myrecyclerview.setAdapter(recyclerAdapter);
                } else {
                    Toast.makeText(getContext(), "랭킹 획득 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RankResponse> call, Throwable t) {
                Toast.makeText(getContext(), "서버 통신 실패", Toast.LENGTH_SHORT).show();
                Log.d(TAG, t.getMessage());
            }
        });
    }

    /* 리사이클러뷰에 표시를 위해 획득한 랭킹 정보를 리스트에 저장 */
    private void setRankList(List<RankData> ranks) {
        int rankNum = 1;
        for(RankData rank : ranks) {
            lstRank.add(
                    new Rank(
                            rankNum,
                            rank.getPicture(),
                            rank.getId(),
                            rank.getNickname(),
                            rank.getScore()
                    )
            );

            rankNum++;
        }
    }
}
