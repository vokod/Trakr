package com.awolity.trakr.view.detail;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.awolity.trakr.R;

public class EditTitleDialog extends DialogFragment {

    public interface EditTitleDialogListener {
        void onTitleEdited(String title);
    }

    private static final String ARG_OLD_TITLE = "arg_old_title";
    private EditTitleDialogListener listener;
    EditText titleEditText;

    public static EditTitleDialog newInstance(String oldTitle) {
        EditTitleDialog fragment = new EditTitleDialog();
        Bundle args = new Bundle();
        args.putString(ARG_OLD_TITLE, oldTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.activity_track_detail_fragment_data_dialog_edit_title, null);
        titleEditText = view.findViewById(R.id.et_title);
        String oldTitle = getArguments().getString(ARG_OLD_TITLE);
        titleEditText.setText(oldTitle);
        // TODO: extract
        builder.setTitle("Edit title")
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }


    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean wantToCloseDialog = false;
                    String enteredText = titleEditText.getText().toString();
                    if (!TextUtils.isEmpty(enteredText)) {
                        listener.onTitleEdited(enteredText);
                        wantToCloseDialog = true;
                    } else {
                        Toast.makeText(getContext(), "Invalid title", Toast.LENGTH_LONG).show();
                    }
                    if (wantToCloseDialog)
                        dismiss();
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (EditTitleDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement EditTitleDialogListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}

