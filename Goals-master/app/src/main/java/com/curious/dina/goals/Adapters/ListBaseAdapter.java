package com.curious.dina.goals.Adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.curious.dina.goals.Controller.GoalPlannerTabsActivity;
import com.curious.dina.goals.Model.ListViewItem;
import com.curious.dina.goals.R;

import java.util.List;

/**
 * This class is used for populating list view with goals.
 */
public class ListBaseAdapter extends BaseAdapter {
    List<ListViewItem> goals;
    Activity activity;

    public ListBaseAdapter(Activity activity, List<ListViewItem> goals){
        this.activity = activity;
        this.goals = goals;
    }

    @Override
    public int getCount() {
        return goals.size();
    }

    @Override
    public Object getItem(int position) {
        return goals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position + 1;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup container) {
        TextView title = null, subtitle = null;
        ImageButton tickbox = null;

        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.list_item, container, false);
            title = (TextView) convertView.findViewById(R.id.title);
            subtitle = (TextView) convertView.findViewById(R.id.subtitle);
            tickbox = (ImageButton) convertView.findViewById(R.id.secondary_action);
        }

        final ListViewItem item = (ListViewItem) getItem(position);
        if (title != null) {
            title.setText(item.name);
        }
        if (subtitle != null) {
            subtitle.setText(item.date);
        }
        if(tickbox != null){
            if(item.isCompleted())
                tickbox.setImageResource(R.mipmap.ic_action_add);

        }

        convertView.findViewById(R.id.primary_target).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((GoalPlannerTabsActivity) activity).showDeleteDialog(item);
                    }
                });

        convertView.findViewById(R.id.secondary_action).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((GoalPlannerTabsActivity) activity).changeGoalStatus(item);
                    }
                });
        return convertView;
    }

}