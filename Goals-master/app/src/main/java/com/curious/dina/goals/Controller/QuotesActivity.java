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
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.curious.dina.goals.Model.GoalsApplication;
import com.curious.dina.goals.Model.GoalsModel;
import com.curious.dina.goals.R;
import com.curious.dina.goals.View.QuotesView;

/**
 * Controller that handles activity for displaying quote of the day.
 */
public class QuotesActivity extends AppCompatActivity {
    private GoalsModel model;
    private QuotesView view;
    private TextView quotesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);

        //-------------NAVIGATION BAR------------------

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_quote);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_quotes);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_quotes);
        navigationView.setNavigationItemSelectedListener(new MenuActionHandler(((GoalsApplication)this.getApplication()).getModel(), this, R.id.activity_quotes));

        //---------------------------------------------

        model = ((GoalsApplication) this.getApplication()).getModel();
        view = new QuotesView(findViewById(R.id.activity_quotes),model);


        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        quotesTextView = (TextView)findViewById(R.id.quote_text_view);

        model.getRewardsModel().fetchQuoteOfTheDay();
    }


}
