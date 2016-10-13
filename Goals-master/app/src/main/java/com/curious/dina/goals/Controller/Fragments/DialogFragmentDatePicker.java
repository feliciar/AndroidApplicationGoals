package com.curious.dina.goals.Controller.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import com.curious.dina.goals.Controller.GoalPlannerTabsActivity;
import com.curious.dina.goals.Model.GoalPlannerModel;
import com.curious.dina.goals.View.TabView;

import java.util.Calendar;

/**
 * Displays calendar after trying to add a daily goal.
 */
public class DialogFragmentDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    String goal;

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        ((GoalPlannerTabsActivity) getActivity()).getModel()
                .addNewGoal(goal, GoalPlannerModel.DAY, year, monthOfYear, 0, dayOfMonth);
        ((GoalPlannerTabsActivity) getActivity()).setChosenTabToIndex(TabView.DAY);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void setGoal(String goal){
        this.goal = goal;
    }

}

