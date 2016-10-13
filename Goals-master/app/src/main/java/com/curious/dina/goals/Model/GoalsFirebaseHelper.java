package com.curious.dina.goals.Model;

import android.os.AsyncTask;
import android.util.Log;
import com.firebase.client.DataSnapshot;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shaded.apache.http.HttpResponse;
import org.shaded.apache.http.HttpStatus;
import org.shaded.apache.http.StatusLine;
import org.shaded.apache.http.client.ClientProtocolException;
import org.shaded.apache.http.client.HttpClient;
import org.shaded.apache.http.client.methods.HttpGet;
import org.shaded.apache.http.impl.client.DefaultHttpClient;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GoalsFirebaseHelper {
    private RewardsModel rewardsModel;

    public GoalsFirebaseHelper(){

    }
    /**
     * Used by Model to get all day goals
     * Tested and works.
     * Returns
     * [{"year":"2016","month":"3","dayOfMonth":"4","goals":[{"name":"Eat Ice Cream","completed":false}]},{"year":"2016","month":"4","dayOfMonth":"1","goals":[{"name":"Say Hi to Dina","completed":false}]}]
     * @return
     */
    public JSONArray getAllDayGoalsWithAttribute(DataSnapshot userInfo, String key, String value){
        JSONArray result = new JSONArray();
        DataSnapshot years = userInfo.child("years");
        for(DataSnapshot year : years.getChildren()){
            DataSnapshot months = year.child("months");
            for(DataSnapshot month : months.getChildren()){
                DataSnapshot days = month.child("days");
                for(DataSnapshot day : days.getChildren()) {
                    try {
                        JSONArray goals = getGoalsWithAttribute(day, key, value);
                        JSONObject dayObj = new JSONObject();
                        dayObj.put("year", year.getKey());
                        dayObj.put("month", month.getKey());
                        dayObj.put("dayOfMonth", day.getKey());
                        dayObj.put("goals", goals);
                        result.put(dayObj);
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    public JSONArray getAllWeekGoalsWithAttribute(DataSnapshot userInfo, String key, String value){
        JSONArray result = new JSONArray();
        DataSnapshot years = userInfo.child("years");
        for(DataSnapshot year : years.getChildren()){
            DataSnapshot weeks = year.child("weeks");
            for(DataSnapshot week : weeks.getChildren()){
                try {
                    JSONArray goals = getGoalsWithAttribute(week, key, value);
                    if(goals.length()!=0) {
                        JSONObject obj = new JSONObject();
                        obj.put("year", year.getKey());
                        obj.put("week", week.getKey());
                        obj.put("goals", goals);
                        result.put(obj);
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }

            }
        }

        return result;
    }

    /**
     * Tested and works
     * Gives the output:
     * [{"year":"2016","month":"3","goals":[{"name":"exercise","completed":true}]},{"year":"2016","month":"4","goals":[{"name":"exercise","completed":true}]},{"year":"2016","month":"5","goals":[{"name":"Do Fun Stuff","completed":false},{"name":"exercise","completed":false}]}]
     * @param userInfo
     * @return
     */
    public JSONArray getAllMonthGoalsWithAttribute(DataSnapshot userInfo, String key, String value){
        JSONArray result = new JSONArray();
        DataSnapshot years = userInfo.child("years");
        for(DataSnapshot year : years.getChildren()){
            DataSnapshot months = year.child("months");
            for(DataSnapshot month : months.getChildren()){
                try {
                    JSONArray goals = getGoalsWithAttribute(month, key,value);
                    if(goals.length()!=0) {
                        JSONObject obj = new JSONObject();
                        obj.put("year", year.getKey());
                        obj.put("month", month.getKey());
                        obj.put("goals", goals);
                        result.put(obj);
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * Tested and works.
     * Returns:
     * [{"goals":[{"name":"Do Fun Stuff","completed":false},{"name":"go to Disney Land","completed":true},{"name":"go to Disney land","completed":true}]}]
     * @param userInfo
     * @return
     */
    public JSONArray getAllLifeGoalsWithAttribute(DataSnapshot userInfo, String key, String value){
        JSONArray result = new JSONArray();
        try {
            JSONArray goals = getGoalsWithAttribute(userInfo, key, value);
            if(goals.length()!=0) {
                JSONObject obj = new JSONObject();
                obj.put("goals", goals);
                result.put(obj);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Takes a data snapshot of all user info. Returns the data snapshot of all days of a certain
     * year and month.
     * @param userInfo
     * @param year
     * @param month
     * @return
     */
    public DataSnapshot getDaysSnapshotOfMonth(DataSnapshot userInfo, int year, int month){
        return userInfo.child("years/" + year + "/months/" + month + "/days/");
    }

    /**
     * Takes a data snapshot that has a child with all goals.
     * @param goals
     * @return
     * @throws JSONException
     */
    public JSONArray getGoalsArray(DataSnapshot goals) {

        JSONArray goalsObj = new JSONArray();
        goals = goals.child("goals");
        for(DataSnapshot goal: goals.getChildren()){
            goalsObj.put(getGoalAttributes(goal));
        }
        return goalsObj;
    }

    public JSONObject getGoalAttributes(DataSnapshot goal) {
        String goalName = goal.getKey();
        JSONObject goalAttributes = new JSONObject();
        try {
            goalAttributes.put("name", goalName);
            for (DataSnapshot attribute : goal.getChildren()) {
                goalAttributes.put(attribute.getKey(), attribute.getValue());
            }
        }catch(JSONException e){
            Log.e("JSONException","Got a JSON exception when trying to add a goals attributes.");
            e.printStackTrace();
        }
        return goalAttributes;
    }

    public JSONArray getGoalsWithAttribute(DataSnapshot snapshot, String attr, String value) throws JSONException{
        JSONArray goals = this.getGoalsArray(snapshot);

        JSONArray resultGoals = new JSONArray();
        for(int i=0; i<goals.length(); ++i){
            JSONObject goal = goals.getJSONObject(i);
            if(goal.get(attr).toString().equals(value)){
                resultGoals.put(goal);
            }
        }

        return resultGoals;
    }

    public JSONObject getAllGoalsWithAttribute(DataSnapshot snapshot, String attr, String value) throws JSONException{
        JSONObject result = new JSONObject();

        //Get life goals
        JSONArray goals = getGoalsWithAttribute(snapshot, attr, value);
        JSONObject life = new JSONObject();
        life.put("goals",goals);
        result.put("life",life);

        //Get month goals
        //Do this by looping all years, and all months
        DataSnapshot years = snapshot.child("years");
        JSONArray monthArray = new JSONArray();
        for(DataSnapshot year : years.getChildren()){
            DataSnapshot months = year.child("months");
            for(DataSnapshot month : months.getChildren()) {
                JSONObject monthObj = new JSONObject();
                monthObj.put("year", "" + year.getKey());
                monthObj.put("monthNumber",""+month.getKey());

                goals = getGoalsWithAttribute(month, attr, value);
                if(goals.length()!=0) {
                    monthObj.put("goals", goals);
                    monthArray.put(monthObj);
                }

                //Add day goals
                DataSnapshot days = month.child("days");
                JSONArray dayArray;
                //Check if this array already exists
                if(result.has("days")) {
                    dayArray = result.getJSONArray("days");

                }else{
                    dayArray = new JSONArray();
                }

                for(DataSnapshot day : days.getChildren()){
                    JSONObject dayObj = new JSONObject();
                    dayObj.put("year", "" + year.getKey());
                    dayObj.put("month", ""+day.child("month").getValue());
                    dayObj.put("dayOfMonth", ""+day.getKey());


                    goals = getGoalsWithAttribute(day, attr, value);
                    if(goals.length()!=0) {
                        dayObj.put("goals", goals);
                        dayArray.put(dayObj);
                    }
                }
                result.put("days",dayArray);

            }
            result.put("months",monthArray);

            //Add week goals
            DataSnapshot weeks = year.child("weeks");
            JSONArray weekArray = new JSONArray();
            for(DataSnapshot week : weeks.getChildren()){
                JSONObject weekObj = new JSONObject();
                weekObj.put("year", "" + year.getKey());
                weekObj.put("weekNumber", "" + week.getKey());

                goals = getGoalsWithAttribute(week, attr, value);
                if(goals.length()!=0) {
                    weekObj.put("goals", goals);
                    weekArray.put(weekObj);
                }
            }
            result.put("weeks",weekArray);
        }
        return  result;
    }

    public void performRequest(String requestString, Method methodOnFinish, RewardsModel rewardsModel){
        this.rewardsModel = rewardsModel;
        RequestTask task = new RequestTask(methodOnFinish);
        task.execute(requestString);
    }

    class RequestTask extends AsyncTask<String, String, String> {
        Method method;
        private RequestTask(Method m){
            method = m;
        }

        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();

            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();

                if(statusLine.getStatusCode() != HttpStatus.SC_OK){
                    try {
                        JSONObject o = new JSONObject(responseString);
                        String message = (String)o.getJSONObject("error").get("message");
                        Log.e("IOException", "API Error: " + message);

                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                    return null;
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                method.invoke(rewardsModel,result);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}


