package com.example.rush_b.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.rush_b.MainViewActivity;
import com.example.rush_b.fragment.CommunicationFragment;
import com.example.rush_b.fragment.HomeFragment;
import com.example.rush_b.fragment.StatisticFragment;

/**
 * Created by martinock on 24/02/17.
 */

public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case MainViewActivity.TAB_HOME:
                return new HomeFragment();
            case MainViewActivity.TAB_COMMUNICATION:
                return new CommunicationFragment();
            case MainViewActivity.TAB_STATISTIC:
                return new StatisticFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case MainViewActivity.TAB_HOME:
                return "HOME";
            case MainViewActivity.TAB_COMMUNICATION:
                return "COMMUNICATE";
            case MainViewActivity.TAB_STATISTIC:
                return "STATISTIC";
            default:
                return null;
        }
    }
}
