package com.curious.dina.goals.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.curious.dina.goals.Model.GoalPlannerModel;
import com.curious.dina.goals.Model.GoalsApplication;
import com.curious.dina.goals.Model.GoalsModel;
import com.curious.dina.goals.R;
import com.curious.dina.goals.View.ProgressView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

/**
 * Controller that handles activity for progress view.
 */
public class ProgressNavigationActivity extends AppCompatActivity {
    protected String[] primaryChartSlices = new String[] {"Completed", "Remaining"};
    protected String[] secondaryChartSlices = new String[] {"Daily", "Weekly", "Monthly", "Life"};

    private boolean isPrimary = true;
    private GoalsModel model;
    private ProgressView view;
    private PieChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isPrimary = this.getIntent().getBooleanExtra("isPrimary", true);

        setContentView(R.layout.activity_progress_navigation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new MenuActionHandler(((GoalsApplication)this.getApplication()).getModel(), this,R.id.drawer_layout));

        model = ((GoalsApplication) this.getApplication()).getModel();
        chart = (PieChart) findViewById(R.id.chart1);
        if(isPrimary) {
            view = new ProgressView(findViewById(R.id.drawer_layout), model, primaryChartSlices, chart, "");
        }else {
            String tag;
            if(this.getIntent().getBooleanExtra("completed",true)){
                tag = GoalPlannerModel.TAG_NUM_COMPLETED_GOALS;
            }else {
                tag = GoalPlannerModel.TAG_NUM_UNCOMPLETED_GOALS;
            }
            view = new ProgressView(findViewById(R.id.drawer_layout), model, secondaryChartSlices, chart, tag);
        }

        model.getGoalPlannerModel().fetchGoalPlannerData();

        setListeners();
    }

    /**
     * Sets the listeners for when clicking on the pie chart
     */
    public void setListeners(){
        // add a selection listener
        if(isPrimary){
            chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry entry, int i, Highlight highlight) {
                    onValueSelectedForPrimary(entry);
                }

                @Override
                public void onNothingSelected() {
                }
            });
        }else{
            chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry entry, int i, Highlight highlight) {
                    onValueSelectedForSecondary(entry);
                }
                @Override
                public void onNothingSelected() {}
            });
        }

    }

    private void onValueSelectedForPrimary(Entry entry){
        if (entry == null)
            return;
        int selection = entry.getXIndex();
        String selectedString = (String)entry.getData();

        if (selectedString.equals(primaryChartSlices[0])) {
            Intent intent = new Intent(ProgressNavigationActivity.this, ProgressNavigationActivity.class);
            intent.putExtra("isPrimary",false);
            intent.putExtra("completed", true);

            startActivity(intent);
            //view = new ProgressView(findViewById(R.id.drawer_layout), model, secondaryChartSlices, chart, GoalPlannerModel.TAG_NUM_COMPLETED_GOALS);
        } else if (selectedString.equals(primaryChartSlices[1])) {
            Intent intent = new Intent(ProgressNavigationActivity.this, ProgressNavigationActivity.class);
            intent.putExtra("isPrimary",false);
            intent.putExtra("completed", false);

            startActivity(intent);
            //view = new ProgressView(findViewById(R.id.drawer_layout), model, secondaryChartSlices, chart, GoalPlannerModel.TAG_NUM_UNCOMPLETED_GOALS);
        }
    }

    private void onValueSelectedForSecondary(Entry entry){
        if (entry == null)
            return;
        int selection = 0;
        String selectionString = (String)entry.getData();
        for (int i=0; i<secondaryChartSlices.length; ++i){
            if(selectionString.equals(secondaryChartSlices[i]))
                selection =i;
        }




        Intent i = new Intent(this, GoalPlannerTabsActivity.class);
        i.putExtra("tag", selection);
        i.putExtra("completed",this.getIntent().getBooleanExtra("completed",false));
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}
