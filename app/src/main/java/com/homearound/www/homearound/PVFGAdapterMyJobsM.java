package com.homearound.www.homearound;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by boqiancheng on 2016-11-30.
 */

public class PVFGAdapterMyJobsM extends FragmentPagerAdapter {

    private ArrayList<Fragment> fgList;
    private ArrayList<String> titleList;

    public PVFGAdapterMyJobsM(FragmentManager fm) {
        super(fm);
        this.fgList = new ArrayList<Fragment>();
        this.titleList = new ArrayList<String>();
    }

    public void addPVFGTitle(String mTitle) {
        this.titleList.add(mTitle);
    }

    public void addPVFGFragment(Fragment mFragment) {
        this.fgList.add(mFragment);
    }

    @Override
    public int getCount() {
        return fgList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fgList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
       // return super.getPageTitle(position);
        return titleList.get(position);
    }
}
