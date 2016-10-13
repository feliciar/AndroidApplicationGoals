package com.curious.dina.goals.Controller.Fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.curious.dina.goals.Adapters.ListBaseAdapter;
import com.curious.dina.goals.Model.ListViewItem;

import java.util.ArrayList;
import java.util.List;


public class TimeFragment extends ListFragment {
    List<ListViewItem> goals = new ArrayList<>();

    public void updateData(List<ListViewItem> object){
        goals = object;
        ListBaseAdapter listAdapter = new ListBaseAdapter(getActivity(),goals);
        setListAdapter(listAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        updateData(goals);
        int layoutObject = this.getArguments().getInt("layoutID");
        return inflater.inflate(layoutObject, container, false);
    }
}
