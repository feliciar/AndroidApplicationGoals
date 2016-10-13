package com.curious.dina.goals.Controller.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.curious.dina.goals.Controller.GoalPlannerTabsActivity;
import com.curious.dina.goals.Model.GoalPlannerModel;
import com.curious.dina.goals.R;
import com.curious.dina.goals.View.TabView;

import java.util.Calendar;

/**
 * Displays drop-down menu after trying to add a monthly goal.
 */
public class DialogFragmentDropDown extends DialogFragment {
    private int selectedMonth;
    private String goal;

    public interface DialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }
    DialogListener mListener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_fragment_drop_down, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(DialogFragmentDropDown.this);
                        ((GoalPlannerTabsActivity)getActivity()).getModel().addNewGoal(goal, GoalPlannerModel.MONTH, 0, selectedMonth, 0, 0);
                        ((GoalPlannerTabsActivity)getActivity()).setChosenTabToIndex(TabView.MONTH);

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(DialogFragmentDropDown.this);
                    }
                });

        Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner_drop_down);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.spinner_drop_down_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Calendar c = Calendar.getInstance();
        spinner.setSelection(c.get(Calendar.MONTH));
        spinner.getBackground().setColorFilter(ContextCompat.getColor(getActivity(),R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMonth = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

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

    public int getSelectedMonth(){
        return selectedMonth;
    }

    public void setGoal(String goal){
        this.goal=goal;
    }
}

