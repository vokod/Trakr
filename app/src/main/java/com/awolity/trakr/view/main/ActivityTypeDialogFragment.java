package com.awolity.trakr.view.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.awolity.trakr.R;
import com.awolity.trakr.activitytype.ActivityType;
import com.awolity.trakr.activitytype.ActivityTypeManager;

import java.util.List;

public class ActivityTypeDialogFragment extends DialogFragment
        implements ActivityTypeAdapter.ActivityTypeItemCallback {

    private RecyclerView rv;
    private ActivityTypeAdapter adapter;
    private ActivityTypeDialogListener listener;


    public interface ActivityTypeDialogListener {
        void onActivityTypeSelected(ActivityType activityType);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.activity_main_dialog_activity_type, null);

        rv = view.findViewById(R.id.rv_activity_type);
        rv.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        adapter = new ActivityTypeAdapter(getActivity().getLayoutInflater(), this);
        rv.setAdapter(adapter);
        List<ActivityType> activityTypeList = ActivityTypeManager.getInstance().getActivityTypes();
        adapter.updateItems(activityTypeList);

        builder.setTitle("Select activity type")
                .setView(view);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (ActivityTypeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AddPartnerDialogListener");
        }
    }

    @Override
    public void onActivityTypeItemClicked(ActivityType activityType) {
        listener.onActivityTypeSelected(activityType);
    }
}