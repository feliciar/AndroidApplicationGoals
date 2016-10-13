package com.curious.dina.goals.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.curious.dina.goals.Model.GoalsModel;
import com.curious.dina.goals.R;

/**
 * Created by Felicia Rosell on 2016-04-24.
 */
public class MenuActionHandler implements NavigationView.OnNavigationItemSelectedListener {

    Activity activity;
    int drawerID;
    GoalsModel model;
    public MenuActionHandler(GoalsModel model, Activity activity, int drawerID){
        this.activity=activity;
        this.drawerID=drawerID;
        this.model=model;

    }
    /*
    public void init(){
        DrawerLayout drawer = (DrawerLayout) activity.findViewById(drawerID);
        //drawer.closeDrawer(GravityCompat.START);
        TextView t = (TextView)drawer.findViewById(R.id.user_email);
        if(t!=null) {
            Log.d("DBG_user_email", "User email textview was not null! Setting text");
            t.setText(model.getUserEmail());
        }
        else
            Log.d("DBG_user_email","User email textview was  null");
    }
    */



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(activity, ProgressNavigationActivity.class);
            activity.startActivity(intent);
        }else if(id == R.id.nav_goal_planner) {
            Intent intent = new Intent(activity, GoalPlannerTabsActivity.class);
            activity.startActivity(intent);
        } else if (id == R.id.nav_completed){
            Intent intent = new Intent(activity, GoalPlannerTabsActivity.class);
            intent.putExtra("completed", true);
            activity.startActivity(intent);
        } else if (id == R.id.nav_quotes){
            Intent intent = new Intent(activity, QuotesActivity.class);
            activity.startActivity(intent);
        } else if (id == R.id.nav_cats){
            Intent intent = new Intent(activity, CatPhotosActivity.class);
            activity.startActivity(intent);
        }else if(id == R.id.nav_logout){
            model.logout();
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(drawerID);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
