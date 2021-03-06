package com.awolity.trakr.view.list;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.awolity.trakr.R;
import com.awolity.trakr.view.model.TrackDataWithMapPoints;
import com.awolity.trakr.view.MapUtils;
import com.awolity.trakr.utils.Constants;
import com.awolity.trakr.utils.StringUtils;
import com.awolity.trakr.utils.Utility;
import com.awolity.trakrviews.PrimaryPropertyViewIcon;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrackListAdapter
        extends RecyclerView.Adapter<TrackListAdapter.TrackItemViewHolder> {

    private final List<TrackDataWithMapPoints> items = new ArrayList<>();
    private final LayoutInflater layoutInflater;
    private final TrackItemCallback callback;
    private final Context context;
    private int unit = Constants.UNIT_METRIC;

    TrackListAdapter(Context context, LayoutInflater layoutInflater, TrackItemCallback callback) {
        this.layoutInflater = layoutInflater;
        this.callback = callback;
        this.context = context;
    }

    void updateItems(@NonNull final List<TrackDataWithMapPoints> newItems) {
        // MyLog.d(TAG, "updateItems");
        final List<TrackDataWithMapPoints> oldItems = new ArrayList<>(this.items);
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
                return oldItems.get(oldItemPosition).equals(newItems.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldItems.get(oldItemPosition).equals(newItems.get(newItemPosition));
            }
        }).dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public TrackItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // MyLog.d(TAG, "onCreateViewHolder");
        View v = layoutInflater.inflate(R.layout.activity_track_list_item_track_list, parent, false);
        return new TrackItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackItemViewHolder holder, int position) {
        // MyLog.d(TAG, "onBindViewHolder");
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    class TrackItemViewHolder extends RecyclerView.ViewHolder {

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
            distanceView = itemView.findViewById(R.id.ppv_distance);
            durationView = itemView.findViewById(R.id.ppv_duration);
            elevationView = itemView.findViewById(R.id.ppv_ascent);
            mapView = itemView.findViewById(R.id.mapView);

            resetWidgets();

            mapView.onCreate(null);
            mapView.setClickable(false);
        }

        void bind(final TrackDataWithMapPoints trackDataWithMapPoints) {
            titleTv.setText(trackDataWithMapPoints.getTrackData().getTitle());
            dateTv.setText(DateUtils.getRelativeTimeSpanString(
                    trackDataWithMapPoints.getTrackData().getStartTime()).toString());

            String firstLetter = "";
            if (trackDataWithMapPoints.getTrackData().getTitle()
                    != null && !trackDataWithMapPoints.getTrackData().getTitle().isEmpty()) {
                firstLetter = trackDataWithMapPoints.getTrackData().getTitle().substring(0, 1);
            }
            initialIv.setImageDrawable(
                    Utility.getInitial(firstLetter,
                            String.valueOf(trackDataWithMapPoints.getTrackData().getStartTime()),
                            initialIv.getLayoutParams().width));

            distanceView.setValue(StringUtils.getDistanceAsThreeCharactersString(
                    trackDataWithMapPoints.getTrackData().getDistance()));
            elevationView.setValue(String.format(Locale.getDefault(),
                    "%.0f", trackDataWithMapPoints.getTrackData().getAscent()));
            durationView.setValue(StringUtils.getElapsedTimeAsString(
                    trackDataWithMapPoints.getTrackData().getElapsedTime()));

            mapView.getMapAsync(googleMap -> {
                googleMap.getUiSettings().setMapToolbarEnabled(false);
                if (polyline != null) {
                    polyline.remove();
                }
                polyline = MapUtils.setupTrackPolyLine(context, googleMap,
                        trackDataWithMapPoints, true);
                mapView.onResume();
            });

            clickOverlay.setOnClickListener(v -> callback.onTrackItemClicked(trackDataWithMapPoints.getTrackData().getTrackId(),
                    itemView));
        }

        private void resetWidgets() {
            durationView.setup(context.getString(R.string.elapsed_time_view_title),
                    context.getString(R.string.elapsed_time_view_unit),
                    context.getString(R.string.elapsed_time_view_default_value),
                    R.drawable.ic_duration);

            if (unit == Constants.UNIT_IMPERIAL) {
                distanceView.setup(context.getString(R.string.distance_view_title),
                        context.getString(R.string.distance_view_unit_imperial),
                        context.getString(R.string.distance_view_default_value),
                        R.drawable.ic_distance);
                elevationView.setup(context.getString(R.string.ascent_view_title),
                        context.getString(R.string.ascent_view_unit_imperial),
                        context.getString(R.string.ascent_view_default_value),
                        R.drawable.ic_ascent);
            } else {
                distanceView.setup(context.getString(R.string.distance_view_title),
                        context.getString(R.string.distance_view_unit),
                        context.getString(R.string.distance_view_default_value),
                        R.drawable.ic_distance);
                elevationView.setup(context.getString(R.string.ascent_view_title),
                        context.getString(R.string.ascent_view_unit),
                        context.getString(R.string.ascent_view_default_value),
                        R.drawable.ic_ascent);
            }
        }
    }

    public interface TrackItemCallback {
        void onTrackItemClicked(long trackId, View itemView);
    }
}
