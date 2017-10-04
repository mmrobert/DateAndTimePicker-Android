package com.homearound.www.homearound;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by boqiancheng on 2016-10-31.
 */

public class DialogFragmentPhotoPermission extends DialogFragment {

    private static final String ATITLE = "aTitle";
    //   private static final String AMESSAGE = "aMessage";

    private String mTitle;
    //   private String mMessage;

    public interface OnPhotoPermissionListener {
        void onPhotoPermission();
    }

    private DialogFragmentPhotoPermission.OnPhotoPermissionListener listener;

    public DialogFragmentPhotoPermission() {
        // require empty constructor
    }

    public static DialogFragmentPhotoPermission newInstance(String param1) {
        DialogFragmentPhotoPermission mDialog = new DialogFragmentPhotoPermission();
        Bundle argts = new Bundle();
        argts.putString(ATITLE, param1);
        //  argts.putString(AMESSAGE, param2);
        mDialog.setArguments(argts);
        return mDialog;
    }

    public void setOnPhotoPermissionListener(DialogFragmentPhotoPermission.OnPhotoPermissionListener listener) {
        this.listener = listener;
    }
/*
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof DialogFragmentPhotoPermission.OnPhotoPermissionListener) {
            listener = (DialogFragmentPhotoPermission.OnPhotoPermissionListener)activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + "must implement DialogFragmentPhotoPermission.OnPhotoPermissionListener");
        }
    }
*/
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final int PHOTO_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 123;
        //  return super.onCreateDialog(savedInstanceState);
        mTitle = getArguments().getString(ATITLE);
        //   mMessage = getArguments().getString(AMESSAGE);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());

        mBuilder.setTitle(mTitle);
        //   mBuilder.setMessage(mMessage);

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onPhotoPermission();
                dismiss();
            }
        });

        Dialog mDialog = mBuilder.create();
        return mDialog;
    }
}
