package com.awolity.trakr.view.explore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.renderscript.RSInvalidStateException;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.awolity.trakr.R;
import com.awolity.trakr.data.entity.TrackEntity;
import com.awolity.trakr.view.detail.TrackDetailActivity;
import com.awolity.trakrutils.StringUtils;
import com.awolity.trakrutils.Utility;
import com.awolity.trakrviews.PrimaryPropertyViewIcon;
import com.awolity.trakrviews.SecondaryPropertyViewIcon;

import java.util.Locale;

public class TrackDetailsDialog extends android.support.v4.app.DialogFragment {

    public interface TrackDetailsDialogListener {
        void onViewClicked(long id);
    }

    private TrackDetailsDialogListener listener;

    private TrackEntity trackEntity;

    public void setTrackEntity(TrackEntity trackEntity){
        this.trackEntity = trackEntity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if(trackEntity == null){
            throw new RSInvalidStateException(" TrackEntity is null");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialog = inflater.inflate(R.layout.activity_explore_dialog_track_data, null);

        SecondaryPropertyViewIcon durationView = dialog.findViewById(R.id.spv_duration);
        SecondaryPropertyViewIcon distanceView = dialog.findViewById(R.id.spv_distance);
        SecondaryPropertyViewIcon elevationView = dialog.findViewById(R.id.spv_ascent);
        TextView titleTv = dialog.findViewById(R.id.tv_title);
        TextView dateTv = dialog.findViewById(R.id.tv_date);
        ImageView iconIv = dialog.findViewById(R.id.iv_icon);

        durationView.setup(getActivity().getString(R.string.elapsed_time_view_title),
                getActivity().getString(R.string.elapsed_time_view_unit),
                getActivity().getString(R.string.elapsed_time_view_default_value),
                R.drawable.ic_duration);
        distanceView.setup(getActivity().getString(R.string.distance_view_title),
                getActivity().getString(R.string.distance_view_unit),
                getActivity().getString(R.string.distance_view_default_value),
                R.drawable.ic_distance);
        elevationView.setup(getActivity().getString(R.string.ascent_view_title),
                getActivity().getString(R.string.ascent_view_unit),
                getActivity().getString(R.string.ascent_view_default_value),
                R.drawable.ic_ascent);

        titleTv.setText(trackEntity.getTitle());
        dateTv.setText(DateUtils.getRelativeTimeSpanString(
                trackEntity.getStartTime()).toString());

        String firstLetter = "";
        if (trackEntity.getTitle() != null && !trackEntity.getTitle().isEmpty()) {
            firstLetter = trackEntity.getTitle().substring(0, 1);
        }
        iconIv.setImageDrawable(
                Utility.getInitial(firstLetter,
                        String.valueOf(trackEntity.getStartTime()),
                        iconIv.getLayoutParams().width));

        distanceView.setValue(StringUtils.getDistanceAsThreeCharactersString(
                trackEntity.getDistance()));
        elevationView.setValue(String.format(Locale.getDefault(),
                "%.0f", trackEntity.getAscent()));
        durationView.setValue(StringUtils.getElapsedTimeAsString(
                trackEntity.getElapsedTime()));

        builder/*.setTitle("Track details")*/
                .setView(dialog)
                .setPositiveButton("View", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onViewClicked(trackEntity.getTrackId());
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
             listener = (TrackDetailsDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement TrackDetailsDialogListener");
        }


    }

}
