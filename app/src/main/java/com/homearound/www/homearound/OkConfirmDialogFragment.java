package com.homearound.www.homearound;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by boqiancheng on 2016-10-17.
 */

public class OkConfirmDialogFragment extends DialogFragment {

    private static final String ATITLE = "aTitle";
 //   private static final String AMESSAGE = "aMessage";

    private String mTitle;
 //   private String mMessage;

    public OkConfirmDialogFragment() {
        // require empty constructor
    }

    public static OkConfirmDialogFragment newInstance(String param1) {
        OkConfirmDialogFragment okDialog = new OkConfirmDialogFragment();
        Bundle argts = new Bundle();
        argts.putString(ATITLE, param1);
      //  argts.putString(AMESSAGE, param2);
        okDialog.setArguments(argts);
        return okDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      //  return super.onCreateDialog(savedInstanceState);
        mTitle = getArguments().getString(ATITLE);
     //   mMessage = getArguments().getString(AMESSAGE);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());

        mBuilder.setTitle(mTitle);
     //   mBuilder.setMessage(mMessage);

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        Dialog mDialog = mBuilder.create();
        return mDialog;
    }
}
