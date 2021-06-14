package com.myriding.atapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.myriding.R;
import com.myriding.model.Home;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final static String TAG = "HomeRecyclerViewAdapter";

    DecimalFormat dataFormat = new DecimalFormat("#.##");

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private List<Home> mData;
    private Context mContext;

    public HomeRecyclerViewAdapter(Context context, List<Home> data) {
        this.mData = data;
        this.mContext = context;
    }

    public HomeRecyclerViewAdapter(List<Home> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            populateItemRows((ItemViewHolder) holder, position);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    private void showLoadingView(LoadingViewHolder holder, int position) {

    }

    private void populateItemRows(ItemViewHolder holder, int position) {
        Home item = mData.get(position);
        holder.setItem(item);
    }

    // GoogleMap mMap;
    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;
        private TextView tv_startPoint;
        private TextView tv_endPoint;
        private TextView tv_distance;
        private TextView tv_time;
        private TextView tv_avgSpeed;
        private TextView tv_maxSpeed;
        private ImageView img_map;
        // private MapView mapView;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = (TextView) itemView.findViewById(R.id.post_title);
            tv_startPoint = (TextView) itemView.findViewById(R.id.post_start_point);
            tv_endPoint = (TextView) itemView.findViewById(R.id.post_end_point);
            tv_distance = (TextView) itemView.findViewById(R.id.post_distance);
            tv_time = (TextView) itemView.findViewById(R.id.post_time);
            tv_avgSpeed = (TextView) itemView.findViewById(R.id.post_speed_avg);
            tv_maxSpeed = (TextView) itemView.findViewById(R.id.post_speed_max);
            img_map = (ImageView) itemView.findViewById(R.id.post_map_image);
            /*mapView = (MapView) itemView.findViewById(R.id.home_map);

            mapView.onCreate(null);
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    mMap = googleMap;
                    mMap.getUiSettings().setZoomControlsEnabled(false);
                    // LatLng seoul = new LatLng(37.52487, 126.92723);
                    // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(seoul, 16));
                }
            });*/
        }

        public void setItem(Home data) {
            tv_title.setText(data.getTitle());
            tv_startPoint.setText(data.getStartPoint());
            tv_endPoint.setText(data.getEndPoint());
            tv_distance.setText(dataFormat.format(data.getDistance()) + "km");
            tv_time.setText(data.getTime() + "");
            tv_avgSpeed.setText(dataFormat.format(data.getAvgSpeed()) + "km/h");
            tv_maxSpeed.setText(dataFormat.format(data.getMaxSpeed()) + "km/h");
            img_map.setImageResource(R.drawable.img_user);
            // img_map.setImageBitmap();
            /*if(mMap != null) {
                Log.d(TAG, "IN");
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.color(Color.RED);
                polylineOptions.width(10);
                polylineOptions.addAll(data.getArrayPoints());

                int center = (int) (data.getArrayPoints().size() / 2);
                LatLng latLng = new LatLng(data.getArrayPoints().get(center).latitude, data.getArrayPoints().get(center).longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                mMap.addPolyline(polylineOptions);
            }*/
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
