package com.curious.dina.goals.Controller.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

import com.curious.dina.goals.Controller.GoalPlannerTabsActivity;
import com.curious.dina.goals.R;

/**
 * Used for displaying dialog for adding new goals.
 */
public class DialogFragmentAdd extends DialogFragment {
    private View dialogView;

    public interface DialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }
    DialogListener mListener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_fragment_add, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(DialogFragmentAdd.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(DialogFragmentAdd.this);
                    }
                });

        //
        GoalPlannerTabsActivity activity = (GoalPlannerTabsActivity) getActivity();
        setPreselectedRadioButton(activity.viewPager.getCurrentItem());


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

    public void setPreselectedRadioButton(int tabNr){
        if (tabNr == 0) {
            ((RadioButton) dialogView.findViewById(R.id.radioButton_daily)).setChecked(true);
        } else if (tabNr == 1) {
            ((RadioButton) dialogView.findViewById(R.id.radioButton_weekly)).setChecked(true);
        } else if (tabNr == 2) {
            ((RadioButton) dialogView.findViewById(R.id.radioButton_monthly)).setChecked(true);
        } else if (tabNr == 3) {
            ((RadioButton) dialogView.findViewById(R.id.radioButton_life)).setChecked(true);
        }
    }
}

