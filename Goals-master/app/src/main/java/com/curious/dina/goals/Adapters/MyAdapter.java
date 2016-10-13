package com.curious.dina.goals.Adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import com.curious.dina.goals.Controller.Fragments.TimeFragment;
import com.curious.dina.goals.R;

public class MyAdapter extends FragmentPagerAdapter {
    private String fragments [] = {"Daily", "Weekly", "Monthly", "Life"};
    private Fragment fragObjects [] = {null,null,null,null};

    public MyAdapter(android.support.v4.app.FragmentManager supportFragmentManager){
        super(supportFragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        if(fragObjects[position]!=null)
            return fragObjects[position];

        fragObjects[position] = new TimeFragment();

        int layoutID;
        switch (position){
            case 0:
                layoutID = R.layout.fragment_daily;
                break;
            case 1:
                layoutID = R.layout.fragment_weekly;
                break;
            case 2:
                layoutID = R.layout.fragment_monthly;
                break;
            case 3:
                layoutID = R.layout.fragment_life;
                break;
            default:
                return null;
        }
        Bundle b = new Bundle();
        b.putInt("layoutID", layoutID);
        fragObjects[position].setArguments(b);
        return fragObjects[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position){
        return fragments[position];
    }
}