package com.awolity.trakr.view;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.awolity.trakr.R;
import com.awolity.trakr.customviews.PrimaryPropertyView;
import com.awolity.trakr.customviews.SecondaryPropertyView;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.utils.StringUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrackListAdapter
        extends RecyclerView.Adapter<TrackListAdapter.TrackItemViewHolder> {

    private static final String TAG = TrackListAdapter.class.getSimpleName();
    private final List<TrackEntity> items = new ArrayList<>();
    private final LayoutInflater layoutInflater;

    public TrackListAdapter(LayoutInflater inflater) {
        layoutInflater = inflater;
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
        View v = layoutInflater.inflate(R.layout.activity_track_list_item_track_list, parent, false);
        return new TrackItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TrackItemViewHolder holder, int position) {
        holder.setItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class TrackItemViewHolder extends RecyclerView.ViewHolder {

        private TrackEntity trackItem;
        private TextView titleTv;
        private ImageView initialIv;
        private PrimaryPropertyView elapsedTimeView, distanceView;
        private SecondaryPropertyView ascentView, descentView, avgSpeedView, maxSpeedView;


        TrackItemViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.tv_title);
            initialIv = itemView.findViewById(R.id.iv_initial);
            elapsedTimeView = itemView.findViewById(R.id.ppv_elapsed_time);
            distanceView = itemView.findViewById(R.id.ppv_distance);
            ascentView = itemView.findViewById(R.id.spv_ascent);
            descentView = itemView.findViewById(R.id.spv_descent);
            avgSpeedView = itemView.findViewById(R.id.spv_avg_speed);
            maxSpeedView = itemView.findViewById(R.id.spv_max_speed);
        }

        void setItem(TrackEntity track) {
            this.trackItem = track;
            titleTv.setText(trackItem.getTitle());

            ColorGenerator generator = ColorGenerator.MATERIAL;
            String firstLetter = "";
            if (trackItem.getTitle().length() > 0) {
                firstLetter = trackItem.getTitle().substring(0, 1);
            }
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(firstLetter, generator.getColor(trackItem.getTitle()));
            initialIv.setImageDrawable(drawable);

            elapsedTimeView.setLabel("Elapsed time");
            elapsedTimeView.setUnit("s");
            elapsedTimeView.setValue(StringUtils.getElapsedTimeAsString(trackItem.getElapsedTime()));

            distanceView.setLabel("Distance");
            distanceView.setUnit("km");
            distanceView.setValue(StringUtils.getDistanceAsThreeCharactersString(trackItem.getDistance()));

            ascentView.setLabel("Ascent");
            ascentView.setUnit("m");
            ascentView.setValue(String.format(Locale.getDefault(), "%.0f", trackItem.getAscent()));

            descentView.setLabel("Descent");
            descentView.setUnit("m");
            descentView.setValue(String.format(Locale.getDefault(), "%.0f", trackItem.getDescent()));

            avgSpeedView.setLabel("Avg. speed");
            avgSpeedView.setUnit("km/h");
            avgSpeedView.setValue(StringUtils.getSpeedAsThreeCharactersString(trackItem.getAvgSpeed()));

            maxSpeedView.setLabel("Max. speed");
            maxSpeedView.setUnit("km/h");
            maxSpeedView.setValue(StringUtils.getSpeedAsThreeCharactersString(trackItem.getMaxSpeed()));
        }
    }
}
