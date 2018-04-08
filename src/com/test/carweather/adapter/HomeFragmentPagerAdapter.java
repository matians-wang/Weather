package com.test.carweather.adapter;

import java.util.List;

import com.test.carweather.view.HomeFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

/**
 * Created by b913 on 2017/3/13.
 */

public class HomeFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private List<HomeFragment> fragments = null;

    public HomeFragmentPagerAdapter(FragmentManager fm, List<HomeFragment> fragments) {
        super(fm);
        this.fragments = fragments;
        try {
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateData(List<HomeFragment> fragments) {
        this.fragments = fragments;
        try {
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
