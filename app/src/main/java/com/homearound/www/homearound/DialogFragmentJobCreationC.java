package com.homearound.www.homearound;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by boqiancheng on 2016-10-17.
 */

public class DialogFragmentJobCreationC extends DialogFragment {

    public DialogFragmentJobCreationC() {
        // reqiure empty constructor
    }

    private DialogFragmentJobCreationC.OnFragmentJobCreationListener listener;

    public interface OnFragmentJobCreationListener {
        public void onSaveJobC();
        public void onSaveAndPostJobC();
    }

    public static DialogFragmentJobCreationC newInstance() {
        DialogFragmentJobCreationC mDialog = new DialogFragmentJobCreationC();
     //   Bundle argts = new Bundle();
     //   argts.putString(ATITLE, param1);
     //   mDialog.setArguments(argts);
        return mDialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof DialogFragmentJobCreationC.OnFragmentJobCreationListener) {
            listener = (DialogFragmentJobCreationC.OnFragmentJobCreationListener)activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + "must implement DialogFragmentJobCreationC.OnFragmentJobCreationListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.dialog_fragment_job_creation_c, container, false);

      //  mTitle = getArguments().getString(ATITLE);

      //  getDialog().setTitle(mTitle);

        Button savePostBtn = (Button)rootView.findViewById(R.id.dialog_button_save_post_c);
        Button saveBtn = (Button)rootView.findViewById(R.id.dialog_button_save_only_c);
        Button cancelBtn = (Button)rootView.findViewById(R.id.dialog_button_cancel_job_creation_c);

        // okBtn.setText(mAction);
        //  okBtn.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

        savePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSaveAndPostJobC();
                dismiss();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSaveJobC();
                dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return rootView;
    }

}
