package com.curious.dina.goals.Controller.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.NumberPicker;

import com.curious.dina.goals.Controller.GoalPlannerTabsActivity;
import com.curious.dina.goals.Model.GoalPlannerModel;
import com.curious.dina.goals.R;
import com.curious.dina.goals.View.TabView;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Displays number-picker after trying to add a weekly goal.
 */
public class DialogFragmentNumberPicker extends DialogFragment {
    private int selectedWeek;
    private String goal;

    public interface DialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    public void setGoal(String goal){
        this.goal=goal;
    }

    DialogListener mListener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_fragment_numberpicker, null);
        builder.setView(dialogView)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(DialogFragmentNumberPicker.this);
                        //The new goal is created when the "Add"-button is clicked
                        ((GoalPlannerTabsActivity) getActivity()).getModel()
                                .addNewGoal(goal, GoalPlannerModel.WEEK, 0, 0, selectedWeek, 0);
                        ((GoalPlannerTabsActivity) getActivity()).setChosenTabToIndex(TabView.WEEK);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(DialogFragmentNumberPicker.this);
                    }
                });

        NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(52);
        numberPicker.setWrapSelectorWheel(true);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                selectedWeek = newVal;
                Log.i("DBG_addgoal", "In DialogFragmentNumberPicker. A week was picked: " + selectedWeek);
            }
        });
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Stockholm"), Locale.forLanguageTag("sv"));
        numberPicker.setValue(c.get(Calendar.WEEK_OF_YEAR));
        selectedWeek = c.get(Calendar.WEEK_OF_YEAR);

        return builder.create();
    }

    // Override the Fragment.onAttach() method to instantiate the DialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the DialogListener so we can send events to the host
            mListener = (DialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement DialogListener");
        }
    }

    public int getSelectedWeek(){
        return selectedWeek;
    }
}

