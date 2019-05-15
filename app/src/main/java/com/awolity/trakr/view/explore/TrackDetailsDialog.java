package com.awolity.trakr.view.explore;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.awolity.trakr.R;
import com.awolity.trakr.model.TrackData;
import com.awolity.trakr.utils.Constants;
import com.awolity.trakr.utils.StringUtils;
import com.awolity.trakr.utils.Utility;
import com.awolity.trakrviews.ListPropertyViewIcon;

import java.util.Locale;

public class TrackDetailsDialog extends DialogFragment {

    public interface TrackDetailsDialogListener {
        void onViewClicked(long id);
    }

    private TrackDetailsDialogListener listener;
    private TrackData trackData;
    private int unit = Constants.UNIT_METRIC;

    public void setTrackData(TrackData trackData) {
        this.trackData = trackData;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (trackData == null) {
            throw new IllegalStateException(" TrackEntity is null");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        @SuppressWarnings("ConstantConditions") final LayoutInflater inflater = getActivity().getLayoutInflater();

        @SuppressLint("InflateParams") final View dialog = inflater.inflate(R.layout.activity_explore_dialog_track_data, null);

        ListPropertyViewIcon durationView = dialog.findViewById(R.id.lpv_duration);
        ListPropertyViewIcon distanceView = dialog.findViewById(R.id.lpv_distance);
        ListPropertyViewIcon elevationView = dialog.findViewById(R.id.lpv_ascent);
        TextView titleTv = dialog.findViewById(R.id.tv_title);
        TextView dateTv = dialog.findViewById(R.id.tv_date);
        ImageView iconIv = dialog.findViewById(R.id.iv_icon);

        durationView.setup(getActivity().getString(R.string.elapsed_time_view_title),
                getActivity().getString(R.string.elapsed_time_view_unit),
                getActivity().getString(R.string.elapsed_time_view_default_value),
                R.drawable.ic_duration);
        if (unit == Constants.UNIT_IMPERIAL) {
            distanceView.setup(getActivity().getString(R.string.distance_view_title),
                    getActivity().getString(R.string.distance_view_unit_imperial),
                    getActivity().getString(R.string.distance_view_default_value),
                    R.drawable.ic_distance);
            elevationView.setup(getActivity().getString(R.string.ascent_view_title),
                    getActivity().getString(R.string.ascent_view_unit_imperial),
                    getActivity().getString(R.string.ascent_view_default_value),
                    R.drawable.ic_ascent);
        } else {
            distanceView.setup(getActivity().getString(R.string.distance_view_title),
                    getActivity().getString(R.string.distance_view_unit),
                    getActivity().getString(R.string.distance_view_default_value),
                    R.drawable.ic_distance);
            elevationView.setup(getActivity().getString(R.string.ascent_view_title),
                    getActivity().getString(R.string.ascent_view_unit),
                    getActivity().getString(R.string.ascent_view_default_value),
                    R.drawable.ic_ascent);
        }

        titleTv.setText(trackData.getTitle());
        dateTv.setText(DateUtils.getRelativeTimeSpanString(
                trackData.getStartTime()).toString());

        String firstLetter = "";
        if (trackData.getTitle() != null && !trackData.getTitle().isEmpty()) {
            firstLetter = trackData.getTitle().substring(0, 1);
        }
        iconIv.setImageDrawable(
                Utility.getInitial(firstLetter,
                        String.valueOf(trackData.getStartTime()),
                        iconIv.getLayoutParams().width));

        distanceView.setValue(StringUtils.getDistanceAsThreeCharactersString(
                trackData.getDistance()));
        elevationView.setValue(String.format(Locale.getDefault(),
                "%.0f", trackData.getAscent()));
        durationView.setValue(StringUtils.getElapsedTimeAsString(
                trackData.getElapsedTime()));

        builder.setView(dialog)
                .setPositiveButton(R.string.activity_explore_dialog_track_details_view, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onViewClicked(trackData.getTrackId());
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        try {
            listener = (TrackDetailsDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TrackDetailsDialogListener");
        }


    }

}
