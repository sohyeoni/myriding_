package com.myriding.http;

import com.myriding.model.CourseResponse;
import com.myriding.model.Login;
import com.myriding.model.RankProfileResponse;
import com.myriding.model.Register;
import com.myriding.model.LoginResponse;
import com.myriding.model.RankResponse;
import com.myriding.model.RegisterResponse;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitAPI {
    @POST("auth/signup")
    Call<RegisterResponse> insertRegister(@Body Register register);

    @POST("auth/login")
    Call<LoginResponse> login(@Body Login login);

    @GET("rank")
    Call<RankResponse> getRank(@Header("Authorization") String authToken);

    @GET("rank/{user_id}")
    Call<RankProfileResponse> getRankProfile(
            @Header("Authorization") String authToken,
            @Path("user_id") int id
    );

    @GET("route/popularity")
    Call<CourseResponse> getPopularCourse(@Header("Authorization") String authToken);

    @GET("route/mylistlatest")
    Call<CourseResponse> getMyCourse(@Header("Authorization") String authToken);

    @GET("route/mylistall")
    Call<CourseResponse> getMyCourseAll(@Header("Authorization") String authToken);

    @GET("route/search")
    Call<CourseResponse> getSearchCourse(
            @Header("Authorization") String authToken,
            @Query("word") String word,
            @Query("count") int sortValue
    );

    @GET("route/{post_id}")
    Call<CourseResponse> getDetailCourse(
            @Header("Authorization") String authToke,
            @Path("post_id") int id
    );

    @POST("routelike/likeup")
    Call<JSONObject> updateLikeCount(
            @Header("Authorization") String authToken,
            @Field("route_like_obj") int route_id
    );


}
