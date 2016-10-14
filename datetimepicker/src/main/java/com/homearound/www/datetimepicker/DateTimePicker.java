package com.homearound.www.datetimepicker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TabHost;
import android.widget.TimePicker;

import java.util.Date;

/**
 * Created by boqiancheng on 2016-10-12.
 */
public class DateTimePicker extends DialogFragment {

    public static final String TAG_FRAG_DATE_TIME = "fragDateTime";

    private static final String KEY_DIALOG_TITLE = "dialogTitle";
    private static final String KEY_INIT_DATE = "initDate";
    private static final String TAG_DATE = "date";
    private static final String TAG_TIME = "time";

    private Context mContext;
    private ButtonClickListener mButtonClickListener;
    private OnDateTimeSetListener mOnDateTimeSetListener;
    private Bundle mArgument;
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;

    public DateTimePicker() {

    }

    public static DateTimePicker newInstance(CharSequence dialogTitle, Date initDate) {
        DateTimePicker mDateTimePicker = new DateTimePicker();
        Bundle mBundle = new Bundle();
        mBundle.putCharSequence(KEY_DIALOG_TITLE, dialogTitle);
        mBundle.putSerializable(KEY_INIT_DATE, initDate);
        mDateTimePicker.setArguments(mBundle);
        return mDateTimePicker;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mButtonClickListener = new ButtonClickListener();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mArgument = getArguments();
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setTitle(mArgument.getCharSequence(KEY_DIALOG_TITLE));
        mBuilder.setNegativeButton(android.R.string.no, mButtonClickListener);
        mBuilder.setPositiveButton(android.R.string.yes, mButtonClickListener);

        AlertDialog mDialog = mBuilder.create();
        mDialog.setView(createDateTimeView(mDialog.getLayoutInflater()));

        return mDialog;

     //   return super.onCreateDialog(savedInstanceState);
    }

    private View createDateTimeView(LayoutInflater layoutInflater) {
        View mView = layoutInflater.inflate(R.layout.date_time_picker, null);
        TabHost mTabHost = (TabHost) mView.findViewById(R.id.tab_host);
        mTabHost.setup();

        TabHost.TabSpec mDateTab = mTabHost.newTabSpec(TAG_DATE);
        mDateTab.setIndicator(getString(R.string.tab_date));
        mDateTab.setContent(R.id.date_content);
        mTabHost.addTab(mDateTab);

        TabHost.TabSpec mTimeTab = mTabHost.newTabSpec(TAG_TIME);
        mTimeTab.setIndicator(getString(R.string.tab_time));
        mTimeTab.setContent(R.id.time_content);
        mTabHost.addTab(mTimeTab);

        DateTime mDateTime = new DateTime((Date)mArgument.getSerializable(KEY_INIT_DATE));

        mDatePicker = (DatePicker)mView.findViewById(R.id.date_picker);
        mTimePicker = (TimePicker)mView.findViewById(R.id.time_picker);
        mDatePicker.init(mDateTime.getYear(), mDateTime.getMonthOfYear(), mDateTime.getDayOfMonth(), null);
        if (Build.VERSION.SDK_INT >= 23) {
            mTimePicker.setHour(mDateTime.getHourOfDay());
        } else {
            mTimePicker.setCurrentHour(mDateTime.getHourOfDay());
        }
        if (Build.VERSION.SDK_INT >= 23) {
            mTimePicker.setMinute(mDateTime.getMinuteOfHour());
        } else {
            mTimePicker.setCurrentMinute(mDateTime.getMinuteOfHour());
        }

        return mView;
    }

    public void setOnDateTimeSetListener(OnDateTimeSetListener onDateTimeSetListener) {
        mOnDateTimeSetListener = onDateTimeSetListener;
    }

    private class ButtonClickListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (DialogInterface.BUTTON_POSITIVE == which) {
                if (Build.VERSION.SDK_INT >= 23) {
                    DateTime mDateTime = new DateTime(mDatePicker.getYear(), mDatePicker.getMonth(),
                            mDatePicker.getDayOfMonth(), mTimePicker.getHour(), mTimePicker.getMinute());
                    mOnDateTimeSetListener.dateTimeSet(mDateTime.getDate());
                } else {
                    DateTime mDateTime = new DateTime(mDatePicker.getYear(), mDatePicker.getMonth(),
                            mDatePicker.getDayOfMonth(), mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute());
                    mOnDateTimeSetListener.dateTimeSet(mDateTime.getDate());
                }
            }
        }
    }

    public interface OnDateTimeSetListener {
        public void dateTimeSet(Date date);
    }
}
