package com.homearound.www.datetimepicker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.Date;

/**
 * Created by boqiancheng on 2016-10-12.
 */
public class SimpleDateTimePicker {

    private CharSequence mDialogTitle;
    private Date mInitDate;
    private DateTimePicker.OnDateTimeSetListener mOnDateTimeSetListener;
    private FragmentManager mFragmentManager;

    private SimpleDateTimePicker(CharSequence dialogTitle, Date initDate,
                                 DateTimePicker.OnDateTimeSetListener onDateTimeSetListener,
                                 FragmentManager fragmentManager) {
        FragmentTransaction mFragmentTransaction = fragmentManager.beginTransaction();
        Fragment mDateTimeDialogFrag = fragmentManager.findFragmentByTag(DateTimePicker.TAG_FRAG_DATE_TIME);
        if (mDateTimeDialogFrag != null) {
            mFragmentTransaction.remove(mDateTimeDialogFrag);
        }
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.commit();

        mDialogTitle = dialogTitle;
        mInitDate = initDate;
        mOnDateTimeSetListener = onDateTimeSetListener;
        mFragmentManager = fragmentManager;
    }

    public static SimpleDateTimePicker make(CharSequence dialogTitle, Date initDate,
                                            DateTimePicker.OnDateTimeSetListener onDateTimeSetListener,
                                            FragmentManager fragmentManager) {
        return new SimpleDateTimePicker(dialogTitle, initDate, onDateTimeSetListener, fragmentManager);
    }

    public void show() {
        DateTimePicker mDateTimePicker = DateTimePicker.newInstance(mDialogTitle, mInitDate);
        mDateTimePicker.setOnDateTimeSetListener(mOnDateTimeSetListener);
        mDateTimePicker.show(mFragmentManager, DateTimePicker.TAG_FRAG_DATE_TIME);
    }
}
