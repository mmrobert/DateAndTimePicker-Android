package com.homearound.www.homearound;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class DialogFragmentMyJobsC extends DialogFragment {

    private static final String ATITLE = "aTitle";
    private static final String AACTION = "aAction";

    private String mTitle;
    private String mAction;

    private OnFragmentOkActionListener listener;

    public interface OnFragmentOkActionListener {
        public void onUpdateJobStatus();
        public void onDeleteJob();
    }

    public DialogFragmentMyJobsC() {
        // Required empty public constructor
    }

    public static DialogFragmentMyJobsC newInstance(String param1, String param2) {
        DialogFragmentMyJobsC myDialogFragment1 = new DialogFragmentMyJobsC();
        Bundle argts = new Bundle();
        argts.putString(ATITLE, param1);
        argts.putString(AACTION, param2);
        myDialogFragment1.setArguments(argts);
        return myDialogFragment1;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentOkActionListener) {
            listener = (OnFragmentOkActionListener)activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + "must implement DialogFragmentMyJobsC.OnFragmentOkActionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.dialog_fragment_my_jobs_c, container, false);

        mTitle = getArguments().getString(ATITLE);
        mAction = getArguments().getString(AACTION);

        getDialog().setTitle(mTitle);

        Button cancelBtn = (Button)rootView.findViewById(R.id.dialog_button_cancel_my_jobs_c);
        Button okBtn = (Button)rootView.findViewById(R.id.dialog_button_ok_my_jobs_c);

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
