package com.curious.dina.goals.Controller;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.curious.dina.goals.Controller.Fragments.DialogFragmentAdd;
import com.curious.dina.goals.Controller.Fragments.DialogFragmentDatePicker;
import com.curious.dina.goals.Controller.Fragments.DialogFragmentDelete;
import com.curious.dina.goals.Controller.Fragments.DialogFragmentDropDown;
import com.curious.dina.goals.Controller.Fragments.DialogFragmentNumberPicker;
import com.curious.dina.goals.Model.GoalPlannerModel;
import com.curious.dina.goals.Model.GoalsApplication;
import com.curious.dina.goals.Adapters.MyAdapter;
import com.curious.dina.goals.Model.GoalsModel;
import com.curious.dina.goals.Model.ListViewItem;
import com.curious.dina.goals.R;
import com.curious.dina.goals.View.GoalPlannerTabsView;
import com.curious.dina.goals.View.TabView;

/**
 * Controller for goal planner view.
 */
public class GoalPlannerTabsActivity extends AppCompatActivity
        implements  DialogFragmentAdd.DialogListener,
                    DialogFragmentDelete.DialogListener,
                    DialogFragmentDropDown.DialogListener,
                    DialogFragmentNumberPicker.DialogListener{

    TabLayout tabLayout;
    public ViewPager viewPager;
    Window window;

    GoalPlannerModel model;
    public GoalPlannerTabsView view;
    private ListViewItem goalToRemove;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_planner_tabs);

        //-------------NAVIGATION BAR------------------

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_goal_planner);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_goal_planner);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_goal_planner);


        navigationView.setNavigationItemSelectedListener(new MenuActionHandler(((GoalsApplication)this.getApplication()).getModel(),this, R.id.drawer_layout_goal_planner));

        //---------------------------------------------

        model = ((GoalsApplication) this.getApplication()).getModel().getGoalPlannerModel();
        view = new GoalPlannerTabsView(findViewById(R.id.activity_goal_planner_tabs),model);

        boolean showCompletedGoals = this.getIntent().getBooleanExtra("completed",false);

        model.fetchGoalPlannerData();

        window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));

        TabView tabview = new TabView(viewPager,model,showCompletedGoals);





        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        //Set starting tab
        int tab = this.getIntent().getIntExtra("tag", 0);
        tabLayout.getTabAt(tab).select();
        view.changeBackgroundColor(tab, window);


        view.changeBackgroundColor(tabLayout.getSelectedTabPosition(), window);

        // Listeners
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                view.changeBackgroundColor(tabLayout.getSelectedTabPosition(), window);
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(!showCompletedGoals) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAddDialog();
                }
            });
        } else {
            fab.hide();
        }
    }

    //------------DIALOGS---------------------------------------
    public void showAddDialog(){
        DialogFragmentAdd newFragment = new DialogFragmentAdd();
        newFragment.show(getFragmentManager(), "ADD");
    }

    public void showDeleteDialog(ListViewItem goalToRemove){
        DialogFragmentDelete newFragment = new DialogFragmentDelete();
        newFragment.show(getFragmentManager(), "DELETE");
        this.goalToRemove = goalToRemove; // HACK
    }

    public DialogFragmentDatePicker showDatePickerDialog() {
        DialogFragmentDatePicker newFragment = new DialogFragmentDatePicker();
        newFragment.show(getFragmentManager(), "DATE_PICKER");
        return newFragment;
    }

    public DialogFragmentDropDown showDropDownDialog(){
        DialogFragmentDropDown newFragment = new DialogFragmentDropDown();
        newFragment.show(getFragmentManager(), "DROP_DOWN");
        return newFragment;
    }

    public DialogFragmentNumberPicker showNumberPickerDialog(){
        DialogFragmentNumberPicker newFragment = new DialogFragmentNumberPicker();
        newFragment.show(getFragmentManager(), "NUMBER_PICKER");
        return newFragment;
    }
    //-----------------------------------------------------------

    /**
     * Deals with adding a new goal.
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if(("ADD").equals(dialog.getTag())){
            // get goal input
            EditText editText = (EditText) dialog.getDialog().findViewById(R.id.input_goal);
            String goal = editText.getText().toString();

            // check selected radio button
            RadioGroup radioGroup = (RadioGroup) dialog.getDialog().findViewById(R.id.radio_group);
            int index = radioGroup.indexOfChild(dialog.getDialog().findViewById(radioGroup.getCheckedRadioButtonId()));

            if(!goal.equals("")){
                if(index == TabView.DAY){
                    showDatePickerDialog().setGoal(goal);
                } else if(index == TabView.WEEK){
                    showNumberPickerDialog().setGoal(goal);
                } else if(index == TabView.MONTH){
                    showDropDownDialog().setGoal(goal);
                } else if(index == TabView.LIFE){
                    model.addNewGoal(goal, GoalPlannerModel.LIFE, 0, 0, 0, 0);
                    tabLayout.getTabAt(index).select();
                } else {
                    Toast.makeText(this, "Please select a category to enter your goal into.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter a goal.", Toast.LENGTH_SHORT).show();
            }
        } else if(("DELETE".equals(dialog.getTag()))){
            model.removeGoal(goalToRemove.name, goalToRemove.goalType,
                    goalToRemove.year,goalToRemove.month, goalToRemove.week, goalToRemove.day);
        }
    }

    public void setChosenTabToIndex(int index){
        tabLayout.getTabAt(index).select();
    }

    /**
     * Do nothing if user selects cancel.
     */
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) { }

    public GoalPlannerModel getModel(){
        return model;
    }

    /**
     * Calls model to set goal as completed.
     */
    public void changeGoalStatus(ListViewItem item){
        if(item.isCompleted())
            model.setGoalAsUncompleted(item.name, item.goalType, item.year,
                    item.month, item.week, item.day);
        else
            model.setGoalAsCompleted(item.name, item.goalType, item.year,
                    item.month, item.week, item.day);
    }


}
