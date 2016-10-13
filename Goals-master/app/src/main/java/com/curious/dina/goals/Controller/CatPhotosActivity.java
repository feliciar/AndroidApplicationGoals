package com.curious.dina.goals.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.curious.dina.goals.Model.GoalsApplication;
import com.curious.dina.goals.Model.GoalsModel;
import com.curious.dina.goals.R;
import com.curious.dina.goals.View.CatPhotosView;

import org.w3c.dom.Text;

/**
 * Controller that handles activity for displaying cat photos.
 */
public class CatPhotosActivity extends AppCompatActivity  {
    private GoalsModel model;
    private CatPhotosView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cats);

        //-------------NAVIGATION BAR------------------

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_cats);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_cats);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_cats);
         navigationView.setNavigationItemSelectedListener(new MenuActionHandler(((GoalsApplication)this.getApplication()).getModel(), this, R.id.activity_cats));

        //---------------------------------------------

        model = ((GoalsApplication) this.getApplication()).getModel();
        view = new CatPhotosView(findViewById(R.id.activity_cats), model);
        model.getRewardsModel().fetchRewards();

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //------------SWIPE REFRESH--------------------

        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe_cats);
        swipeView.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorWeeklyTab));
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeView.setRefreshing(false);
                        CatPhotosActivity.this.model.getRewardsModel().fetchRewards();
                    }
                }, 3000);
            }
        });
        //---------------------------------------------

    }



}
