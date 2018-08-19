package com.awolity.trakr.view.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackWithPoints;
import com.awolity.trakr.view.MapUtils;
import com.awolity.trakrutils.StringUtils;
import com.awolity.trakrutils.Utility;
import com.awolity.trakrviews.PrimaryPropertyViewIcon;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class TrackListAdapter
        extends RecyclerView.Adapter<TrackListAdapter.TrackItemViewHolder> {

    private static final String TAG = TrackListAdapter.class.getSimpleName();
    private final List<TrackWithPoints> items = new ArrayList<>();
    private final LayoutInflater layoutInflater;
    private final TrackItemCallback callback;
    private final Context context;

    TrackListAdapter(Context context, LayoutInflater layoutInflater, TrackItemCallback callback) {
        this.layoutInflater = layoutInflater;
        this.callback = callback;
        this.context = context;
    }

    public void updateItems(@NonNull final List<TrackWithPoints> newItems) {
        // MyLog.d(TAG, "updateItems");
        // deleteInvalidTracks(newItems);
        final List<TrackWithPoints> oldItems = new ArrayList<>(this.items);
        this.items.clear();
        this.items.addAll(newItems);
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

    private void deleteInvalidTracks(List<TrackWithPoints> trackWithPointsList) {
        Iterator<TrackWithPoints> iterator = trackWithPointsList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getNumOfTrackPoints() < 2) {
                iterator.remove();
            }
        }
    }

    @Override
    public TrackItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // MyLog.d(TAG, "onCreateViewHolder");
        View v = layoutInflater.inflate(R.layout.activity_track_list_item_track_list, parent, false);
        return new TrackItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TrackItemViewHolder holder, int position) {
        // MyLog.d(TAG, "onBindViewHolder");
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class TrackItemViewHolder extends RecyclerView.ViewHolder {

        private TrackWithPoints trackWithPoints;
        private final TextView titleTv;
        private final TextView dateTv;
        private final ImageView initialIv;
        private final PrimaryPropertyViewIcon distanceView;
        private final PrimaryPropertyViewIcon durationView;
        private final PrimaryPropertyViewIcon elevationView;
        private final FrameLayout clickOverlay;
        private final MapView mapView;
        private Polyline polyline;

        TrackItemViewHolder(View itemView) {
            super(itemView);
            clickOverlay = itemView.findViewById(R.id.fl_click_overlay);
            titleTv = itemView.findViewById(R.id.tv_title);
            dateTv = itemView.findViewById(R.id.tv_date);
            initialIv = itemView.findViewById(R.id.iv_icon);
            distanceView = itemView.findViewById(R.id.spv_distance);
            durationView = itemView.findViewById(R.id.spv_duration);
            elevationView = itemView.findViewById(R.id.spv_ascent);
            mapView = itemView.findViewById(R.id.mapView);

            durationView.setup(context.getString(R.string.elapsed_time_view_title),
                    context.getString(R.string.elapsed_time_view_unit),
                    context.getString(R.string.elapsed_time_view_default_value),
                    R.drawable.ic_duration);
            distanceView.setup(context.getString(R.string.distance_view_title),
                    context.getString(R.string.distance_view_unit),
                    context.getString(R.string.distance_view_default_value),
                    R.drawable.ic_distance);
            elevationView.setup(context.getString(R.string.ascent_view_title),
                    context.getString(R.string.ascent_view_unit),
                    context.getString(R.string.ascent_view_default_value),
                    R.drawable.ic_ascent);

            mapView.onCreate(null);
            mapView.setClickable(false);
        }

        void bind(final TrackWithPoints trackWithPoints) {
            // MyLog.d(TAG, "bind " + TrackItemViewHolder.this.hashCode());
            this.trackWithPoints = trackWithPoints;
            titleTv.setText(this.trackWithPoints.getTitle());
            dateTv.setText(DateUtils.getRelativeTimeSpanString(
                    this.trackWithPoints.getStartTime()).toString());

            String firstLetter = "";
            if (trackWithPoints.getTitle() != null && !trackWithPoints.getTitle().isEmpty()) {
                firstLetter = trackWithPoints.getTitle().substring(0, 1);
            }
            initialIv.setImageDrawable(
                    Utility.getInitial(firstLetter,
                            String.valueOf(trackWithPoints.getStartTime()),
                            initialIv.getLayoutParams().width));

            distanceView.setValue(StringUtils.getDistanceAsThreeCharactersString(
                    trackWithPoints.getDistance()));
            elevationView.setValue(String.format(Locale.getDefault(),
                    "%.0f", trackWithPoints.getAscent()));
            durationView.setValue(StringUtils.getElapsedTimeAsString(
                    trackWithPoints.getElapsedTime()));

            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final GoogleMap googleMap) {
                    if (polyline != null) {
                        polyline.remove();
                    }
                    polyline = MapUtils.setupTrackPolyLine(context, googleMap,
                            trackWithPoints, true);
                    mapView.onResume();
                }
            });

            clickOverlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onTrackItemClicked(trackWithPoints.getTrackId(), itemView);
                }
            });
        }
    }

    public interface TrackItemCallback {
        void onTrackItemClicked(long trackId, View itemView);
    }
}
