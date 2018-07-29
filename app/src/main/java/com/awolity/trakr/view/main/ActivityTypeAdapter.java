package com.awolity.trakr.view.main;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.awolity.trakr.R;
import com.awolity.trakr.activitytype.ActivityType;
import com.awolity.trakr.utils.MyLog;

import java.util.ArrayList;
import java.util.List;

public class ActivityTypeAdapter
        extends RecyclerView.Adapter<ActivityTypeAdapter.ActivityTypeItemViewHolder> {

    private static final String TAG = ActivityTypeAdapter.class.getSimpleName();
    private final List<ActivityType> items = new ArrayList<>();
    private final LayoutInflater layoutInflater;
    private final ActivityTypeItemCallback callback;

    public ActivityTypeAdapter(LayoutInflater layoutInflater, ActivityTypeItemCallback callback) {
        this.layoutInflater = layoutInflater;
        this.callback = callback;

    }

    public void updateItems(final List<ActivityType> newItems) {
        MyLog.d(TAG, "updateItems");
        final List<ActivityType> oldItems = new ArrayList<>(this.items);
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

    @Override
    public ActivityTypeItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyLog.d(TAG, "onCreateViewHolder");
        View v = layoutInflater.inflate(R.layout.activity_main_dialog_activity_type_list_item, parent, false);
        return new ActivityTypeItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ActivityTypeItemViewHolder holder, int position) {
        MyLog.d(TAG, "onBindViewHolder");
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ActivityTypeItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleTv;
        private final ImageView iconImageView;
        private final FrameLayout clickOverlay;

        ActivityTypeItemViewHolder(View itemView) {
            super(itemView);
            MyLog.d(TAG, "TrackItemViewHolder " + ActivityTypeItemViewHolder.this.hashCode());
            clickOverlay = itemView.findViewById(R.id.fl_click_overlay2);
            titleTv = itemView.findViewById(R.id.tv_title);
            iconImageView = itemView.findViewById(R.id.iv_icon_duration);
        }

        void bind(final ActivityType activityType) {
            MyLog.d(TAG, "bind " + ActivityTypeItemViewHolder.this.hashCode());
            titleTv.setText(activityType.getTitle());

            iconImageView.setImageResource(activityType.getIconResource());

            clickOverlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onActivityTypeItemClicked(activityType);
                }
            });
        }
    }

    public interface ActivityTypeItemCallback {
        void onActivityTypeItemClicked(ActivityType activityType);
    }
}

