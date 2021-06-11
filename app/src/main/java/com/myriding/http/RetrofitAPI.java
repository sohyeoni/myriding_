package com.myriding.http;

import com.myriding.model.CourseDetailResponse;
import com.myriding.model.CourseResponse;
import com.myriding.model.Home;
import com.myriding.model.HomeResponse;
import com.myriding.model.Login;
import com.myriding.model.ProfileResponse;
import com.myriding.model.RankProfileResponse;
import com.myriding.model.Register;
import com.myriding.model.LoginResponse;
import com.myriding.model.RankResponse;
import com.myriding.model.RegisterResponse;
import com.myriding.model.RouteLikeResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitAPI {
    @POST("auth/signup")
    Call<RegisterResponse> insertRegister(@Body Register register);

    @POST("auth/login")
    Call<LoginResponse> login(@Body Login login);

    @GET("auth/profilemobile")
    Call<ProfileResponse> getProfile(
            @Header("Authorization") String authToke
    );

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

    @GET("route/mylist/{post_id}")
    Call<CourseDetailResponse> getDetailCourse(
            @Header("Authorization") String authToke,
            @Path("post_id") int id
    );

    @GET("route/search")
    Call<CourseResponse> getSearchCourse(
            @Header("Authorization") String authToken,
            @Query("word") String word,
            @Query("count") int sortValue
    );

    @FormUrlEncoded
    @POST("routelike/likeup")
    Call<RouteLikeResponse> updateLike(
            @Header("Authorization") String authToken,
            @Field("route_like_obj") int route_id
    );

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "routelike/likedown", hasBody = true)
    Call<RouteLikeResponse> deleteLike(
            @Header("Authorization") String authToken,
            @Field("route_like_obj") int route_id
    );

    @FormUrlEncoded
    @POST("record")
    Call<JSONObject> setMyRecord(
            @Header("Authorization") String authToken,
            @Field("rec_title") String rec_title,
            @Field("rec_distance") double rec_distance,
            @Field("rec_time") int rec_time,
            @Field("rec_start_point_address") String rec_start_point_address,
            @Field("rec_end_point_address") String rec_end_point_address,
            @Field("rec_avg_speed") double rec_avg_speed,
            @Field("rec_max_speed") double rec_max_speed,
            @Field("records") JSONArray record
    );

    @GET("record/home")
    Call<HomeResponse> getOnedayRecord(
            @Header("Authorization") String authToken,
            @Query("year") int year,
            @Query("month") int month,
            @Query("day") int day
    );

    @GET("record/home")
    Call<HomeResponse> getRecordRoute(
            @Header("Authorization") String authToken,
            @Query("year") int year,
            @Query("month") int month,
            @Query("day") int day,
            @Query("record_id") int record_id
    );

    @Multipart
    @POST("auth/update/image") Call<JSONObject> uploadProfileImage(
            @Header("Authorization") String authToken,
            @Part ArrayList<MultipartBody.Part> files
    );
}
