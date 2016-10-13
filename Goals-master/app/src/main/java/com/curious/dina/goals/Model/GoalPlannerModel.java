package com.curious.dina.goals.Model;

import android.util.Log;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.Locale;
import java.util.Observable;
import java.util.TimeZone;

/**
 * The Goal Planner model is responsible for everything that
 * concerns goal planning, that is creating new goals,
 * viewing goals for a specific time period,
 * changing the time period,
 * and checking a goal as completed.
 *
 * FetchGoalPlannerData will be called automatically when the model is changed,
 * but it must be called by the model the first time it should be collected.
 * The fetched data will be given to all observers of the model (the views).
 */
public class GoalPlannerModel extends Observable implements iGoalPlannerModel {
    private ValueEventListener databaseEventListener;
    private GoalsFirebaseHelper firebase;
    String userID;

    public GoalPlannerModel(String userID, GoalsFirebaseHelper firebaseHelper) {
        this.userID = userID;
        this.firebase = firebaseHelper;

        fetchGoalPlannerData();
    }

    /**
     * Call this function, the first time only, when you want to fetch
     * data from the database (the function is also automatically
     * called when the setters are called).
     * <p/>
     * This function does not return anything, but
     * informs its observers that data has been changed.
     * The data is sent back through an update
     * as a JSON-object.
     * The object has the form:
     * <p/>
     * {"life":[{"goals":[{"name":"Do Fun Stuff","completed":false},{"name":"go to Disney Land","completed":true},{"name":"go to Disney land","completed":true}]}],"month":[{"year":"2016","month":"3","goals":[{"name":"exercise","completed":true}]},{"year":"2016","month":"4","goals":[{"name":"exercise","completed":true}]},{"year":"2016","month":"5","goals":[{"name":"Do Fun Stuff","completed":false},{"name":"exercise","completed":false}]}],"week":[{"year":"2016","week":"14","goals":[{"name":"see movies","completed":true}]},{"year":"2016","week":"15","goals":[{"name":"Do Fun Stuff","completed":false}]}],"day":[{"year":"2016","month":"3","dayOfMonth":"4","goals":[{"name":"Eat Ice Cream","completed":false}]},{"year":"2016","month":"4","dayOfMonth":"1","goals":[{"name":"Say Hi to Dina","completed":false}]}]}
     * Use http://jsonprettyprint.com/ to see the data
     */
    public void fetchGoalPlannerData() {


        if(userID.equals(""))
            return;
        Firebase f = new Firebase(GoalsModel.FIREBASE_DATA_REQUEST +"/"+userID);


        if (databaseEventListener != null) {
            f.removeEventListener(databaseEventListener);
        }

        databaseEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                JSONObject completedGoals = getAllCompletedGoals(snapshot);

                JSONObject uncompletedGoals = getAllUncompletedGoals(snapshot);


                notifyObservers(completedGoals);

                setChanged();
                notifyObservers(uncompletedGoals);
                setChanged();

                JSONObject numComplGoals = getAllNumberOfCompletedGoals(completedGoals);

                JSONObject numUncomplGoals = getAllNumberOfUncompletedGoals(uncompletedGoals);

                notifyObservers(numComplGoals);

                setChanged();
                notifyObservers(numUncomplGoals);
                setChanged();
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        };
        f.addValueEventListener(databaseEventListener);
    }

    /*------------------------------Functions for getting list of goals-------------------------*/
    /**
     * Returns
     {"tag":"listOfCompletedGoals","life":[{"goals":[{"name":"go to Disney Land","completed":true},{"name":"go to Disney land","completed":true}]}],"month":[{"year":"2015","month":"1","goals":[{"name":"Go to the future","completed":true}]},{"year":"2016","month":"3","goals":[{"name":"exercise","completed":true}]},{"year":"2016","month":"4","goals":[{"name":"exercise","completed":true}]}],"week":[{"year":"2016","week":"14","goals":[{"name":"see movies","completed":true}]}],"day":[]}
     * @param snapshot
     * @return
     */
    private JSONObject getAllCompletedGoals(DataSnapshot snapshot) {
        JSONObject result = new JSONObject();
        try {
            result.put("tag", TAG_COMPLETED_GOALS);
            result.put("life", firebase.getAllLifeGoalsWithAttribute(snapshot, "completed", "true"));
            result.put("month", firebase.getAllMonthGoalsWithAttribute(snapshot, "completed", "true"));
            result.put("week", firebase.getAllWeekGoalsWithAttribute(snapshot, "completed", "true"));
            result.put("day", firebase.getAllDayGoalsWithAttribute(snapshot, "completed", "true"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }


    private JSONObject getAllUncompletedGoals(DataSnapshot snapshot) {
        JSONObject result = new JSONObject();
        try {
            result.put("tag", TAG_UNCOMPLETED_GOALS);
            result.put("life", firebase.getAllLifeGoalsWithAttribute(snapshot, "completed", "false"));
            result.put("month", firebase.getAllMonthGoalsWithAttribute(snapshot, "completed", "false"));
            result.put("week", firebase.getAllWeekGoalsWithAttribute(snapshot, "completed", "false"));
            result.put("day", firebase.getAllDayGoalsWithAttribute(snapshot, "completed", "false"));
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return result;
    }


    /*------------------------------Functions for getting amount of goals-------------------------*/
    /**
     * {"tag":"numberOfUncompletedGoals","life":2,"month":3,"week":1,"day":0}
     * @param uncompletedGoals
     * @return
     */
    private JSONObject getAllNumberOfUncompletedGoals(JSONObject uncompletedGoals){
        JSONObject numUncomplGoals = new JSONObject();
        try {
            numUncomplGoals.put("tag", TAG_NUM_UNCOMPLETED_GOALS);
            numUncomplGoals.put("life", getNumberOfGoals(uncompletedGoals, LIFE));
            numUncomplGoals.put("month", getNumberOfGoals(uncompletedGoals, MONTH));
            numUncomplGoals.put("week", getNumberOfGoals(uncompletedGoals, WEEK));
            numUncomplGoals.put("day", getNumberOfGoals(uncompletedGoals, DAY));
        }catch(JSONException e){
            e.printStackTrace();
        }
        return numUncomplGoals;
    }

    /**
     * Returns
     * Number of completed goals {"tag":"numberOfCompletedGoals","life":2,"month":3,"week":1,"day":0}
     * @param completedGoals
     * @return
     */
    private JSONObject getAllNumberOfCompletedGoals(JSONObject completedGoals){
        JSONObject numComplGoals = new JSONObject();
        try {
            numComplGoals.put("tag", TAG_NUM_COMPLETED_GOALS);
            numComplGoals.put("life", getNumberOfGoals(completedGoals, LIFE));
            numComplGoals.put("month", getNumberOfGoals(completedGoals, MONTH));
            numComplGoals.put("week", getNumberOfGoals(completedGoals, WEEK));
            numComplGoals.put("day", getNumberOfGoals(completedGoals, DAY));
        }catch(JSONException e){
            e.printStackTrace();
        }
        return numComplGoals;
    }

    private int getNumberOfGoals(JSONObject goals, int goalTag){
        int numGoals = 0;
        String goalTagString="";
        switch(goalTag){
            case LIFE :
                goalTagString="life";
                break;
            case MONTH :
                goalTagString="month";
                break;
            case WEEK :
                goalTagString="week";
                break;
            case DAY :
                goalTagString="day";
                break;
        }
        try {
            JSONArray timeArray = goals.getJSONArray(goalTagString);
            for(int i=0; i<timeArray.length(); ++i)
                numGoals+=timeArray.getJSONObject(i).getJSONArray("goals").length();
        }catch(JSONException e){
            e.printStackTrace();
        }
        return numGoals;
    }

    /**
     * Sets the specified goal as completed.
     * Updates the database. (FetchGoalPlannerData is called automatically afterwards).
     * If the goal is a day goal, then it is important that the setWeekDay method has been called,
     * so the correct day goal can be accessed
     *
     * @param goalName the name of the goal that should be set as completed.
     * @param goalType The goaltype can be GoalPlannerModel.DAY, GoalPlannerModel.MONTH,
     *                 GoalPlannerModel.WEEK, or GoalPlannerModel.LIFE
     *
     */
    public void setGoalAsCompleted(final String goalName, int goalType, int year, int month, int week, int day){
        Firebase f = getGoal(goalName, goalType, year, month, week, day);
        f.child("completed").setValue(true);
    }

    public void setGoalAsUncompleted(final String goalName, int goalType, int year, int month, int week, int day){
        Firebase f = getGoal(goalName, goalType, year, month, week, day);
        f.child("completed").setValue(false);
    }

    /**
     * Adds this goal to the database.
     * (FetchGoalPlannerData is called automatically afterwards).
     * If the goal is a day goal, then it is important that the setWeekDay method has been called,
     * so the correct day goal can be accessed
     *
     * @param name the name of the goal that should be added.
     * @param goalType The goaltype can be GoalPlannerModel.DAY, GoalPlannerModel.MONTH,
     *                 GoalPlannerModel.WEEK, or GoalPlannerModel.LIFE
     *
     */
    public void addNewGoal(String name, int goalType, int year, int month, int week, int day){
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Stockholm"), Locale.forLanguageTag("sv"));

        if(goalType==this.MONTH){
            if(c.get(Calendar.MONTH)>month){
                year = c.get(Calendar.YEAR)+1;
            }else {
                year = c.get(Calendar.YEAR);
            }
        }else if(goalType==this.WEEK){
            if(c.get(Calendar.WEEK_OF_YEAR)>(week)){
                year = c.get(Calendar.YEAR)+1;
            }else {
                year = c.get(Calendar.YEAR);
            }
        }
        getGoal(name, goalType,year,month,week,day).child("completed").setValue(false);
    }

    public void removeGoal(String name, int goalType, int year, int month, int week, int day){
        getGoal(name, goalType, year, month, week, day).removeValue();
    }

    private Firebase getGoal(String name, int goalType, int year, int month, int week, int day){
        Firebase myFirebaseRef = new Firebase(GoalsModel.FIREBASE_DATA_REQUEST +"/"+userID+"/");

        month++;
        String requestString="";
        if(goalType==this.MONTH){
            requestString = requestString.concat("/years/"+year+"/months/"+month);
        }else if(goalType==this.LIFE){
            //Do nothing
        }else if(goalType==this.WEEK){
            requestString=requestString.concat("/years/"+year+"/weeks/"+week);
        }else if(goalType==this.DAY){
            requestString=requestString.concat("/years/"+year+"/months/"+month+"/days/"+day);
        }
        requestString = requestString.concat("/goals/"+name);
        return myFirebaseRef.child(requestString);
    }

    public void setUserID(String userID){
        this.userID=userID;
    }

}
