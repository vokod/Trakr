package com.awolity.trakr.view.list;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.awolity.trakr.R;
import com.awolity.trakr.customviews.SecondaryPropertyView;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.utils.MyLog;
import com.awolity.trakr.utils.StringUtils;
import com.awolity.trakr.viewmodel.TrackViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrackListAdapter
        extends RecyclerView.Adapter<TrackListAdapter.TrackItemViewHolder> {

    private static final String LOG_TAG = TrackListAdapter.class.getSimpleName();
    private final List<TrackEntity> items = new ArrayList<>();
    private final LayoutInflater layoutInflater;
    private final TrackItemCallback callback;
    private final AppCompatActivity activity;


    public TrackListAdapter(AppCompatActivity activity) {
        this.activity = activity;
        layoutInflater = activity.getLayoutInflater();
        this.callback = (TrackItemCallback) activity;
    }

    private void add(TrackEntity item) {
        items.add(item);
        notifyItemChanged(items.size() + 1);
    }

    public void add(List<TrackEntity> items) {
        for (TrackEntity item : items) {
            add(item);
        }
    }

    public void updateItems(final List<TrackEntity> newItems) {
        // MyLog.d(LOG_TAG, "updateItems");
        final List<TrackEntity> oldItems = new ArrayList<>(this.items);
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldItems.size();
            }

            @Override
            public int getNewListSize() {
                return items.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                //noinspection ConstantConditions
                return oldItems.get(oldItemPosition).equals(newItems.get(newItemPosition));
            }

            @SuppressWarnings("ConstantConditions")
            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldItems.get(oldItemPosition).equals(newItems.get(newItemPosition));
            }
        }).dispatchUpdatesTo(this);
    }

    private void remove(int id) {
        items.remove(id);
        notifyItemRemoved(id);
    }

    @Override
    public TrackItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // MyLog.d(LOG_TAG, "onCreateViewHolder");
        View v = layoutInflater.inflate(R.layout.activity_track_list_item_track_list, parent, false);
        return new TrackItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TrackItemViewHolder holder, int position) {
        // MyLog.d(LOG_TAG, "onBindViewHolder");
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class TrackItemViewHolder extends RecyclerView.ViewHolder {

        private TrackEntity trackItem;
        private TextView titleTv, dateTv;
        private ImageView initialIv;
        private SecondaryPropertyView distanceView, durationView, elevationView;
        private FrameLayout clickOverlay;
        private MapView mapView;
        private TrackViewModel viewModel;
        private GoogleMap googleMap;

        TrackItemViewHolder(View itemView) {
            super(itemView);
            // MyLog.d(LOG_TAG, "TrackItemViewHolder");
            clickOverlay = itemView.findViewById(R.id.fl_click_overlay);
            titleTv = itemView.findViewById(R.id.tv_title);
            dateTv = itemView.findViewById(R.id.tv_date);
            initialIv = itemView.findViewById(R.id.iv_initial);
            distanceView = itemView.findViewById(R.id.spv_distance);
            durationView = itemView.findViewById(R.id.spv_duration);
            elevationView = itemView.findViewById(R.id.spv_elevation);
            mapView = itemView.findViewById(R.id.mapView);

            viewModel = ViewModelProviders.of(activity).get(TrackViewModel.class);

            mapView.onCreate(null);
            mapView.setClickable(false);
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final GoogleMap googleMap) {
                    MyLog.d(LOG_TAG, "onMapReady: " + googleMap.hashCode());
                    TrackItemViewHolder.this.googleMap = googleMap;
                }
            });
        }

        void bind(TrackEntity track) {
            // MyLog.d(LOG_TAG, "bind");
            this.trackItem = track;
            titleTv.setText(trackItem.getTitle());
            // TODO: ezt szebben formázni
            dateTv.setText(StringUtils.getStartTimeAsString(trackItem.getStartTime()));

            ColorGenerator generator = ColorGenerator.MATERIAL;
            String firstLetter = "";
            if (trackItem.getTitle().length() > 0) {
                firstLetter = trackItem.getTitle().substring(0, 1);
            }
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(firstLetter, generator.getColor(trackItem.getTitle()));
            initialIv.setImageDrawable(drawable);

            distanceView.setLabel("Distance");
            distanceView.setUnit("km");
            distanceView.setValue(StringUtils.getDistanceAsThreeCharactersString(trackItem.getDistance()));

            elevationView.setLabel("Elevation");
            elevationView.setUnit("m");
            elevationView.setValue(String.format(Locale.getDefault(), "%.0f", trackItem.getAscent()));

            durationView.setLabel("Duration");
            durationView.setUnit("s");
            durationView.setValue(StringUtils.getElapsedTimeAsString(trackItem.getElapsedTime()));

            viewModel.init(track.getTrackId());
            viewModel.getTrackWithPoints().observe(activity, new Observer<TrackWithPoints>() {
                @Override
                public void onChanged(@Nullable final TrackWithPoints trackWithPoints) {
                    // MyLog.d(LOG_TAG, "getTrackWithPoints() - onChanged");
                    if (trackWithPoints != null && trackWithPoints.getTrackPoints() != null) {
                        // MyLog.d(LOG_TAG, "getTrackWithPoints() - onChanged - trackPoints != null");
                        if(googleMap!=null) {
                            setupPolyLine(activity, googleMap, trackWithPoints);
                            mapView.onResume();
                        }
                    }
                }
            });

            clickOverlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onTrackItemClicked(trackItem.getTrackId());
                }
            });
        }
    }

    private static void setupPolyLine(Context context, GoogleMap googleMap, TrackWithPoints trackWithPoints) {
        // MyLog.d(LOG_TAG, "setupPolyLine");
        PolylineOptions polylineOptions = new PolylineOptions()
                .geodesic(true)
                .color(ContextCompat.getColor(context, R.color.colorPrimary))
                .width(context.getResources().getInteger(R.integer.polyline_width))
                .zIndex(30)
                .visible(true);

        if (googleMap != null) {
            googleMap.addPolyline(polylineOptions.addAll(trackWithPoints.getPointsLatLng()));
            moveCamera(googleMap, trackWithPoints);
        }
    }

    private static void moveCamera(GoogleMap googleMap, TrackWithPoints trackWithPoints) {
        // MyLog.d(LOG_TAG, "moveCamera");
        LatLngBounds bounds = new LatLngBounds(
                new LatLng(trackWithPoints.getSouthestPoint(), trackWithPoints.getWesternPoint()),
                new LatLng(trackWithPoints.getNorthestPoint(), trackWithPoints.getEasternPoint()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    public interface TrackItemCallback {
        void onTrackItemClicked(long trackId);
    }
}
