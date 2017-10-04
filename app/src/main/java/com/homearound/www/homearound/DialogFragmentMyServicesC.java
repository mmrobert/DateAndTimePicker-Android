package com.homearound.www.homearound;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.homearound.www.homearound.db.CustomerService;

/**
 * Created by boqiancheng on 2016-10-20.
 */

public class DialogFragmentMyServicesC extends DialogFragment {

    private static final String ATITLE = "aTitle";
    //   private static final String AMESSAGE = "aMessage";

    private String mTitle;
    //   private String mMessage;

    public interface OnDeleteServiceListener {
        void onDeleteService();
    }

    private DialogFragmentMyServicesC.OnDeleteServiceListener listener;

    public DialogFragmentMyServicesC() {
        // require empty constructor
    }

    public static DialogFragmentMyServicesC newInstance(String param1) {
        DialogFragmentMyServicesC mDialog = new DialogFragmentMyServicesC();
        Bundle argts = new Bundle();
        argts.putString(ATITLE, param1);
        //  argts.putString(AMESSAGE, param2);
        mDialog.setArguments(argts);
        return mDialog;
    }

    public void setOnDeleteServiceListener(DialogFragmentMyServicesC.OnDeleteServiceListener listener) {
        this.listener = listener;
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

        mBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onDeleteService();
                dismiss();
            }
        });

        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        Dialog mDialog = mBuilder.create();
        return mDialog;
    }
}
