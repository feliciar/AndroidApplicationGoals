package com.curious.dina.goals.View;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.curious.dina.goals.Model.GoalPlannerModel;
import com.curious.dina.goals.Model.GoalsModel;
import com.curious.dina.goals.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ProgressView implements Observer{
    View view;
    GoalsModel model;
    private JSONObject numCompletedGoals;
    private JSONObject numUncompletedGoals;

    private String[] chartSlices;
    private PieChart chart;
    private String tagClicked;

    /**
     * @param view The view object of this view
     * @param model The model
     * @param chartSlices The strings of chart slice names
     * @param chart
     * @param tagClicked Is either GoalPlannerModel.TAG_NUM_COMPLETED_GOALS or GoalPlannerModel.TAG_NUM_UNCOMPLETED_GOALS
     */
    public ProgressView(View view, GoalsModel model, String[] chartSlices, PieChart chart,String tagClicked) {
        this.view = view;
        this.model = model;
        this.chartSlices = chartSlices;
        this.chart = chart;
        this.tagClicked = tagClicked;
        chart.setNoDataText("");

        model.getGoalPlannerModel().addObserver(this);

    }

    /**
     * Set the data for the chart.
     * @param count the number of slices in the chart
     * @param slices The names of the slices
     */
    private void setData(int count, float range, PieChart mChart, String[] slices) {

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();

        int j=0;
        for (int i = 0; i < count; i++) {
            int value=(int)getNumberOfGoalsOfType(slices[i]);
            if(value != 0) {
                Entry e = new Entry(value,j);
                e.setData(slices[i % slices.length]);
                yVals1.add(e);
                j++;
                xVals.add(slices[i % slices.length]);
            }

        }


        PieDataSet dataSet = new PieDataSet(yVals1, "");

        dataSet.setSliceSpace(5f);
        dataSet.setSelectionShift(6f);

        // add colors
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(ContextCompat.getColor(view.getContext(), R.color.colorProgressCompleted));
        colors.add(ContextCompat.getColor(view.getContext(), R.color.colorProgressRemaining));
        colors.add(ContextCompat.getColor(view.getContext(), R.color.colorMonthlyTab));
        colors.add(ContextCompat.getColor(view.getContext(), R.color.colorLifeTab));
        dataSet.setColors(colors);

        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(xVals, dataSet);
        //Transforms data to and from percent format
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return new DecimalFormat("###").format(v);
            }
        });
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(Typeface.defaultFromStyle(0));
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }


    /**
     * Help function for setData of pie chart
     */
    private float getNumberOfGoalsOfType(String name){
        float value = 0;
        try {
            if (name.equals("Completed")) {
                if(numCompletedGoals==null)return 0;
                value += numCompletedGoals.getInt("life");
                value += numCompletedGoals.getInt("month");
                value += numCompletedGoals.getInt("week");
                value += numCompletedGoals.getInt("day");
            } else if (name.equals("Remaining")) {
                if(numUncompletedGoals==null) return 1;
                value += numUncompletedGoals.getInt("life");
                value += numUncompletedGoals.getInt("month");
                value += numUncompletedGoals.getInt("week");
                value += numUncompletedGoals.getInt("day");
            }else {
                String timeTag = "";
                if (name.equals("Daily")) {
                    timeTag= "day";
                } else if (name.equals("Monthly")) {
                    timeTag = "month";
                }else if(name.equals("Weekly")){
                    timeTag="week";
                }else if(name.equals("Life")){
                    timeTag="life";
                }else {
                    Log.e("Error", "Something went wrong in ProgressView, when getting timeTag");
                }
                if (tagClicked.equals(GoalPlannerModel.TAG_NUM_COMPLETED_GOALS) && numCompletedGoals!=null) {
                    value = numCompletedGoals.getInt(timeTag);
                }
                else if(numUncompletedGoals!=null){
                    value = numUncompletedGoals.getInt(timeTag);
                }
            }
        }catch(JSONException e){e.printStackTrace();}
        return value;
    }

    public void drawChart(){
        chart.setUsePercentValues(false);
        chart.setDescription("");
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setCenterTextTypeface(Typeface.defaultFromStyle(0));

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(50f);
        chart.setTransparentCircleRadius(55f);

        chart.setDrawCenterText(false);

        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        setData(chartSlices.length, 100, chart, chartSlices);

        chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        // hide the legend
        chart.getLegend().setEnabled(false);
    }

    public void update(Observable o, Object data){
        if(data.getClass().equals(JSONObject.class)) {
            JSONObject jsonData = (JSONObject) data;
            try{
                //Primary chart
                if(tagClicked.equals("")){

                        if (jsonData.get("tag").equals(GoalPlannerModel.TAG_NUM_COMPLETED_GOALS))
                            numCompletedGoals = (JSONObject) data;
                        else if(jsonData.get("tag").equals(GoalPlannerModel.TAG_NUM_UNCOMPLETED_GOALS)){
                            numUncompletedGoals = (JSONObject)data;
                        }

                    drawChart();
                }
                //Secondary chart
                else if(tagClicked.equals(GoalPlannerModel.TAG_NUM_COMPLETED_GOALS)) {
                    if (jsonData.get("tag").equals(GoalPlannerModel.TAG_NUM_COMPLETED_GOALS)){
                        numCompletedGoals = (JSONObject) data;
                        drawChart();
                    }

                }else if(tagClicked.equals(GoalPlannerModel.TAG_NUM_UNCOMPLETED_GOALS)){
                    if(jsonData.get("tag").equals(GoalPlannerModel.TAG_NUM_UNCOMPLETED_GOALS)){
                        numUncompletedGoals = (JSONObject)data;
                        drawChart();
                    }
                }

            } catch (JSONException e) {e.printStackTrace();            }

        }
    }
}
