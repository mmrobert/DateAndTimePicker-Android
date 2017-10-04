package com.homearound.www.homearound;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by boqiancheng on 2016-10-31.
 */

public class DialogFragmentPhoto extends DialogFragment {

    public DialogFragmentPhoto() {
        // reqiure empty constructor
    }

    private DialogFragmentPhoto.OnFragmentPhotoListener listener;

    public interface OnFragmentPhotoListener {
        public void onChooseFromGallery();
        public void onTakePhoto();
    }

    public static DialogFragmentPhoto newInstance() {
        DialogFragmentPhoto mDialog = new DialogFragmentPhoto();
        //   Bundle argts = new Bundle();
        //   argts.putString(ATITLE, param1);
        //   mDialog.setArguments(argts);
        return mDialog;
    }

    public void setOnFragmentPhotoListener(DialogFragmentPhoto.OnFragmentPhotoListener listener) {
        this.listener = listener;
    }
/*
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof DialogFragmentPhoto.OnFragmentPhotoListener) {
            listener = (DialogFragmentPhoto.OnFragmentPhotoListener)activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + "must implement DialogFragmentPhoto.OnFragmentPhotoListener");
        }
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.dialog_fragment_photo, container, false);

        //  mTitle = getArguments().getString(ATITLE);

        //  getDialog().setTitle(mTitle);

        Button chooseBtn = (Button)rootView.findViewById(R.id.dialog_button_choose_gallery);
        Button takeBtn = (Button)rootView.findViewById(R.id.dialog_button_take_photo);
        Button cancelBtn = (Button)rootView.findViewById(R.id.dialog_button_cancel_photo);

        // okBtn.setText(mAction);
        //  okBtn.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChooseFromGallery();
                dismiss();
            }
        });

        takeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTakePhoto();
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
