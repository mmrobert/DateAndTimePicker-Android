package com.homearound.www.homearound;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by boqiancheng on 2016-12-02.
 */

public class DialogFragmentMyJobsM extends DialogFragment {

    private static final String ATITLE = "aTitle";
    private static final String AACTION = "aAction";

    private String mTitle;
    private String mAction;

    public interface OnFragmentOkActionListener {
        void onUpdateJobStatus();
        void onDeleteJob();
    }

    private DialogFragmentMyJobsM.OnFragmentOkActionListener listener;

    public DialogFragmentMyJobsM() {
      //  super();
        // Required empty public constructor
    }

    public static DialogFragmentMyJobsM newInstance(String param1, String param2) {
        DialogFragmentMyJobsM myDialogFragment1 = new DialogFragmentMyJobsM();
        Bundle argts = new Bundle();
        argts.putString(ATITLE, param1);
        argts.putString(AACTION, param2);
        myDialogFragment1.setArguments(argts);
        return myDialogFragment1;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof DialogFragmentMyJobsM.OnFragmentOkActionListener) {
            listener = (DialogFragmentMyJobsM.OnFragmentOkActionListener)activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + "must implement DialogFragmentMyJobsM.OnFragmentOkActionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
      //  return super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.dialog_fragment_my_jobs_m, container, false);

        mTitle = getArguments().getString(ATITLE);
        mAction = getArguments().getString(AACTION);

        getDialog().setTitle(mTitle);

        Button cancelBtn = (Button)rootView.findViewById(R.id.dialog_button_cancel_my_jobs_m);
        Button okBtn = (Button)rootView.findViewById(R.id.dialog_button_ok_my_jobs_m);

        okBtn.setText(mAction);
        //  okBtn.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTitle.equals("Congratulation!")) {
                    listener.onUpdateJobStatus();
                } else {
                    listener.onDeleteJob();
                }
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
