package com.curious.dina.goals.View;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.curious.dina.goals.Model.GoalPlannerModel;
import com.curious.dina.goals.R;

import java.util.Observable;
import java.util.Observer;

public class GoalPlannerTabsView {
    View view;
    GoalPlannerModel model;

    TabLayout tl;
    Toolbar tb;
    FloatingActionButton fab;
    TextView goalTitle, goalSubtitle;

    public GoalPlannerTabsView(View view, GoalPlannerModel model){
        this.view = view;
        this.model = model;

        tl = (TabLayout) view.findViewById(R.id.tabLayout);
        tb = (Toolbar) view.findViewById(R.id.toolbar_goal_planner);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        goalTitle = (TextView) view.findViewById(R.id.title);
        goalSubtitle = (TextView) view.findViewById(R.id.subtitle);

        // default stuff
        tl.setSelectedTabIndicatorColor(Color.WHITE);
        tl.setTabTextColors(Color.WHITE, Color.WHITE);
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat
                .getColor(view.getContext(), R.color.colorPrimary)));
    }

    public void changeBackgroundColor(int tabNr, Window window) {
        if (tabNr == 0) {
            changeColors(window, R.color.colorPrimary, R.color.colorPrimaryDark);
        } else if (tabNr == 1) {
            changeColors(window, R.color.colorWeeklyTab, R.color.colorWeeklyTabDark);
        } else if (tabNr == 2) {
            changeColors(window, R.color.colorMonthlyTab, R.color.colorMonthlyTabDark);
        } else if (tabNr == 3) {
            changeColors(window, R.color.colorLifeTab, R.color.colorLifeTabDark);
        }
    }

    private void changeColors(Window window, int colorID, int colorDarkID){
        int color = ContextCompat.getColor(view.getContext(), colorID);
        int colorDark = ContextCompat.getColor(view.getContext(), colorDarkID);
        window.setStatusBarColor(colorDark);
        tl.setBackgroundColor(color);
        tb.setBackgroundColor(color);
        fab.setBackgroundTintList(ColorStateList.valueOf(color));
    }
}
