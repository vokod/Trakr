package com.awolity.trakr.view.list;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
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
import com.awolity.trakr.utils.MyLog;
import com.awolity.trakr.utils.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class TrackListAdapterNoMap
        extends RecyclerView.Adapter<TrackListAdapterNoMap.TrackItemViewHolder> {

    private static final String LOG_TAG = TrackListAdapterNoMap.class.getSimpleName();
    private final List<TrackEntity> items = new ArrayList<>();
    private final LayoutInflater layoutInflater;
    private final TrackItemCallback callback;

    public TrackListAdapterNoMap(LayoutInflater inflater, TrackItemCallback callback) {
        layoutInflater = inflater;
        this.callback = callback;
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
        View v = layoutInflater.inflate(R.layout.activity_track_list_item_track_list_no_map, parent, false);
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

        TrackItemViewHolder(View itemView) {
            super(itemView);
            MyLog.d(LOG_TAG, "TrackItemViewHolder "+ TrackItemViewHolder.this.hashCode());
            clickOverlay = itemView.findViewById(R.id.fl_click_overlay);
            titleTv = itemView.findViewById(R.id.tv_title);
            dateTv = itemView.findViewById(R.id.tv_date);
            initialIv = itemView.findViewById(R.id.iv_initial);
            distanceView = itemView.findViewById(R.id.spv_distance);
            durationView = itemView.findViewById(R.id.spv_duration);
            elevationView = itemView.findViewById(R.id.spv_elevation);
            }

        void bind(TrackEntity track) {
             MyLog.d(LOG_TAG, "bind "+ TrackItemViewHolder.this.hashCode());
            this.trackItem = track;
            titleTv.setText(trackItem.getTitle());
            // TODO: ezt szebben formázni
            dateTv.setText(DateUtils.getRelativeTimeSpanString(track.getStartTime()).toString());

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
