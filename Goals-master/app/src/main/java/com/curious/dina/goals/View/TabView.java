package com.curious.dina.goals.View;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.curious.dina.goals.Adapters.MyAdapter;
import com.curious.dina.goals.Controller.Fragments.TimeFragment;
import com.curious.dina.goals.Model.GoalPlannerModel;
import com.curious.dina.goals.Model.GoalsApplication;
import com.curious.dina.goals.Model.GoalsModel;
import com.curious.dina.goals.Model.ListViewItem;
import com.curious.dina.goals.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

/**
 * One TabView handles data for all tabs, that is the Day
 * goal tab, week goal tab, etc.
 */
public class TabView implements Observer{
    public static int DAY = 0, WEEK = 1, MONTH = 2, LIFE = 3;
    ViewPager view;
    private boolean showCompletedGoals = false;

    public TabView(View v, GoalPlannerModel model, boolean showCompletedGoals){
        if(v.getClass().equals(ViewPager.class)){
            view = (ViewPager)v;
        }
        this.showCompletedGoals = showCompletedGoals;

        model.addObserver(this);

        TextView t = (TextView)view.findViewById(R.id.user_email);

    }

    public void update(Observable o, Object data){
        MyAdapter adapter = (MyAdapter)view.getAdapter();

        JSONObject json = (JSONObject) data;
        try{
            String tag;
            if(showCompletedGoals) tag = GoalPlannerModel.TAG_COMPLETED_GOALS;
            else tag = GoalPlannerModel.TAG_UNCOMPLETED_GOALS;
            if( ! json.getString("tag").equals(tag)){
                return;
            }
        }catch(JSONException e){
            e.printStackTrace();
        }

        ArrayList<ListViewItem> set;
        //Set day goals
        TimeFragment f = (TimeFragment)adapter.getItem(DAY);
        set = getListViewItems(json, DAY);
        f.updateData(set);

        //Set week goals
        f = (TimeFragment)adapter.getItem(WEEK);
        set = getListViewItems(json, WEEK);
        f.updateData(set);

        //Set month goals
        f = (TimeFragment)adapter.getItem(MONTH);
        set = getListViewItems(json, MONTH);
        f.updateData(set);

        //Set life goals
        f = (TimeFragment)adapter.getItem(LIFE);
        set = getListViewItems(json, LIFE);
        f.updateData(set);
    }

    private ArrayList<ListViewItem> getListViewItems(JSONObject data, int tag){
        final int LIFE_GOAL=0, MONTH_GOAL=1, WEEK_GOAL=2, DAY_GOAL=3;
        ArrayList<ListViewItem> list = new ArrayList<>();

        try {
            if (tag == DAY) {
                JSONArray days = data.getJSONArray("day");
                for(int i=0; i<days.length(); ++i){
                    JSONArray goals = days.getJSONObject(i).getJSONArray("goals");
                    for(int j=0; j<goals.length(); ++j){
                        String name = goals.getJSONObject(j).getString("name");
                        Calendar c = Calendar.getInstance();
                        int day = days.getJSONObject(i).getInt("dayOfMonth");
                        int month = days.getJSONObject(i).getInt("month") - 1;
                        int year = days.getJSONObject(i).getInt("year");
                        String date="";
                        if(c.get(Calendar.MONTH)==month && c.get(Calendar.YEAR)==year && c.get(Calendar.DAY_OF_MONTH)==day){
                            date = "Today";
                        }else if(c.get(Calendar.MONTH)==month && c.get(Calendar.YEAR)==year && c.get(Calendar.DAY_OF_MONTH)+1==day) {
                            date = "Tomorrow";
                        }else if(c.get(Calendar.MONTH)==month && c.get(Calendar.YEAR)==year && c.get(Calendar.DAY_OF_MONTH)-1==day) {
                            date = "Yesterday";
                        }
                        else {
                            c.set(Calendar.MONTH, (month));
                            String monthStr = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
                            date = "" + day + " " + monthStr + " " + year;
                        }
                        ListViewItem l = new ListViewItem(name, date, DAY_GOAL, day, 0, month, year, showCompletedGoals);
                        list.add(l);
                    }
                }
            }else if(tag == WEEK){
                JSONArray weeks = data.getJSONArray("week");
                for(int i=0; i<weeks.length(); ++i){
                    JSONArray goals = weeks.getJSONObject(i).getJSONArray("goals");
                    for(int j=0; j<goals.length(); ++j){
                        String name = goals.getJSONObject(j).getString("name");
                        int week = weeks.getJSONObject(i).getInt("week");
                        int year = weeks.getJSONObject(i).getInt("year");
                        String date = "Week " + week +" "+ year;
                        ListViewItem l = new ListViewItem(name, date, WEEK_GOAL, 0, week, 0, year, showCompletedGoals);
                        list.add(l);
                    }
                }
            }else if(tag == MONTH){
                JSONArray months = data.getJSONArray("month");
                for(int i=0; i<months.length(); ++i){
                    JSONArray goals = months.getJSONObject(i).getJSONArray("goals");
                    for(int j=0; j<goals.length(); ++j){
                        String name = goals.getJSONObject(j).getString("name");
                        Calendar c = Calendar.getInstance();
                        int month = months.getJSONObject(i).getInt("month") - 1;
                        c.set(Calendar.MONTH, (month));
                        String monthName = c.getDisplayName(Calendar.MONTH,Calendar.LONG,Locale.US);
                        int year = months.getJSONObject(i).getInt("year");
                        String date = monthName +" "+ year;
                        ListViewItem l = new ListViewItem(name, date, MONTH_GOAL,0, 0, month,year, showCompletedGoals);
                        list.add(l);
                    }
                }
            }else if(tag == LIFE){
                if(data.getJSONArray("life").length()==0){
                    return list;
                }
                JSONArray goals = data.getJSONArray("life").getJSONObject(0).getJSONArray("goals");
                for(int j=0; j<goals.length(); ++j){
                    String name = goals.getJSONObject(j).getString("name");

                    ListViewItem l = new ListViewItem(name, "", LIFE_GOAL, 0,0,0,0, showCompletedGoals);
                    list.add(l);
                }
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        return list;
    }
}
