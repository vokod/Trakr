package com.awolity.trakr.view.list;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import com.awolity.trakr.view.MapUtils;
import com.awolity.trakr.viewmodel.TrackViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.Iterator;
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
        deleteInvalidTracks(newItems);
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

    private void deleteInvalidTracks(List<TrackEntity> trackEntityList) {
        Iterator<TrackEntity> iterator = trackEntityList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getNumOfTrackPoints() <= 2) {
                iterator.remove();
            }
        }
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
        private TrackWithPoints trackWithPoints = null;

        TrackItemViewHolder(View itemView) {
            super(itemView);
            MyLog.d(LOG_TAG, "TrackItemViewHolder "+ TrackItemViewHolder.this.hashCode());
            clickOverlay = itemView.findViewById(R.id.fl_click_overlay);
            titleTv = itemView.findViewById(R.id.tv_title_speed);
            dateTv = itemView.findViewById(R.id.tv_date);
            initialIv = itemView.findViewById(R.id.iv_initial_speed);
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
                    MyLog.d(LOG_TAG, "onMapReady: " + TrackItemViewHolder.this.hashCode());
                    TrackItemViewHolder.this.googleMap = googleMap;
                }
            });
        }

        private boolean isMapLayedOut;

        void bind(TrackEntity track) {
             MyLog.d(LOG_TAG, "bind "+ TrackItemViewHolder.this.hashCode());
            this.trackItem = track;
            titleTv.setText(trackItem.getTitle());
            dateTv.setText(DateUtils.getRelativeTimeSpanString(trackWithPoints.getStartTime()).toString());

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
                    if (trackWithPoints != null && trackWithPoints.getTrackPoints() != null) {
                        MyLog.d(LOG_TAG, "onChanged "+ TrackItemViewHolder.this.hashCode());
                        // MyLog.d(LOG_TAG, "getTrackWithPoints() - onChanged - trackPoints != null");
                        TrackItemViewHolder.this.trackWithPoints = trackWithPoints;
                        if (isMapLayedOut) {
                            MyLog.d(LOG_TAG, "onChanged - isMapLayedout "+ TrackItemViewHolder.this.hashCode());
                            MapUtils.setupTrackPolyLine(activity, googleMap, trackWithPoints, true);
                            mapView.onResume();
                        }
                    }
                }
            });

            if (mapView.getViewTreeObserver().isAlive()) {
                mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        MyLog.d(LOG_TAG, "onGlobalLayout "+ TrackItemViewHolder.this.hashCode());
                        isMapLayedOut = true;
                        if (trackWithPoints != null) {
                            MyLog.d(LOG_TAG, "onGlobalLayout trackWithPoints OK"+ TrackItemViewHolder.this.hashCode());
                            MapUtils.setupTrackPolyLine(activity, googleMap, trackWithPoints, true);
                            mapView.onResume();
                        }
                    }
                });
            }

            clickOverlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onTrackItemClicked(trackItem.getTrackId());
                }
            });
        }
    }

    public interface TrackItemCallback {
        void onTrackItemClicked(long trackId);
    }
}
