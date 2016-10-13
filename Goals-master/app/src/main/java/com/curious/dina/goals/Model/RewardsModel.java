package com.curious.dina.goals.Model;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Locale;
import java.util.Observable;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * The functions in this model are used in the first page of the
 * application - the progress view.
 * Progress model is responsible for collecting all completed goals
 * from the database, and all non-completed goals.
 * It is also responsible for getting and setting the users points and handling rewards.
 *
 * The goals are collected from the database when the respective method is called
 * (e.g. fetchCompletedGoals) and then returned with the update method to the observers.
 * The returned data is on the form:
 *
 * {"tag":"listOfUncompletedGoals", "life":[{"name":"Do Fun Stuff","completed":false}],"months":[{"year":"2016","monthNumber":"5","goals":[{"name":"Do Fun Stuff","completed":false},{"name":"exercise","completed":false}]}],"days":[{"year":"2016","weekNumber":"14","month":"4","dayOfMonth":"8","goals":[{"name":"Watch TV","completed":false}]},{"year":"2016","weekNumber":"14","month":"4","dayOfMonth":"11","goals":[{"name":"Do Fun Stuff","completed":false}]}],"weeks":[{"year":"2016","weekNumber":"15","goals":[{"name":"Do Fun Stuff","completed":false}]}]}
 *
 * use http://jsonprettyprint.com/ to view the JSON object
 *
 * the tag corresponds to the appropriate string constant defined in this class
 */
public class RewardsModel extends Observable implements iRewardsModel {

    private String userID;
    private GoalsFirebaseHelper firebase;
    private ValueEventListener qodEventListener;

    public RewardsModel(String userID, GoalsFirebaseHelper firebase){
        this.userID = userID;
        this.firebase = firebase;
    }

    /**
     * Fetches the number of points and sends with the correct tag to
     * the observers
     */
    @Override
    public void fetchPoints() {
        String requestString = GoalsModel.FIREBASE_DATA_REQUEST +"/"+userID+"/points";
        Firebase f = new Firebase(requestString);

        f.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    JSONObject result = new JSONObject();
                    result.put("tag", TAG_POINTS);
                    result.put("points", snapshot.getValue().toString());
                    notifyObservers(result);
                    setChanged();
                    Log.i("FirebaseResult", "JSON result: " + result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
    }

    /**
     * Sets the number of points, updates the database and then calls
     * fetchPoints
     *
     * @param points
     */
    @Override
    public void setPoints(int points) {
        String requestString = GoalsModel.FIREBASE_DATA_REQUEST +"/"+userID+"/points";
        Firebase f = new Firebase(requestString);
        f.setValue(points);
    }



    /**
     * Fetches all possible rewards from the database and returns with an update
     * and the correct tag.
     */
    @Override
    public void fetchRewards() {

        try {
            Method m = getClass().getMethod("handleRewardsResult", String.class);

            String request = "http://thecatapi.com/api/images/get?format=xml&type=png";
            firebase.performRequest(request, m,this);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    public void handleRewardsResult(String result){

        if(result==null){
            Log.e("CatImageError", "Result was empty, could not collect it");
            return;
        }

        JSONObject o = new JSONObject();
        try {
            o.put("tag", RewardsModel.TAG_REWARDS);
            o.put("url",  getURLFromXML(result));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("DBG", "Sending cat url to observers: " + o.toString());
        setChanged();
        notifyObservers(o);
        setChanged();
    }

    private String getURLFromXML(String result){
        String url = "";
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(result)));
            Element element = document.getDocumentElement();

            NodeList list = element.getElementsByTagName("url");
            if (list != null && list.getLength() > 0) {
                NodeList subList = list.item(0).getChildNodes();

                if (subList != null && subList.getLength() > 0) {
                    url = subList.item(0).getNodeValue();
                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return url;
    }



    public void handleQODResult(String result){
        if(result==null){
            Log.e("QODError", "QOD was empty, could not collect it");
            return;
        }

        try {
            JSONObject o = new JSONObject();
            o.put("tag", TAG_QUOTE_OF_THE_DAY);

            JSONObject resultJSON = new JSONObject(result);
            String quote = (String)resultJSON.getJSONObject("contents").getJSONArray("quotes").getJSONObject(0).get("quote");
            o.put("quote", quote);
            Log.i("QODResult", o.toString());
            setChanged();
            notifyObservers(o);
            setChanged();

            storeQuoteOfTheDayInDatabase(quote);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Method to store the quote of the day in firebase database.
     * @param quote
     */
    private void storeQuoteOfTheDayInDatabase(String quote){
        Firebase f = new Firebase("https://fiery-heat-4873.firebaseio.com//");

        int year = getCurrentTime(10);
        int month = getCurrentTime(GoalPlannerModel.MONTH);
        int day = getCurrentTime(GoalPlannerModel.DAY);
        Log.d("DBG_qod_date","Will store quote in firebase for date: "+"" + year + "" + month + "" + day);

        f.child("quotes").child(""+year+""+month+""+day).setValue(quote);

    }

    /**
     * Fetches quote of the day and returns with an update
     * and the correct tag.
     */
    @Override
    public void fetchQuoteOfTheDay(){
        fetchQuoteOfTheDayFromFirebase();
    }

    /**
     * This is called by fetchQuoteOfTheDayFromFirebase, if the firebase
     * database did not entail the quote of the day
     */
    private void fetchQuoteOfTheDayFromAPI() {
        try {
            Method m = getClass().getMethod("handleQODResult", String.class);

            String request = "http://quotes.rest/qod.json?category=inspire&api_key=GGT4cZ0PVNmsh5y2CRj3wfynIdE5p1xRG92jsnsaMRPM4k9FxS";
            firebase.performRequest(request, m,this);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function is called first to get the quote of the day.
     * If the quote of the day is not in firebase, this function
     * calls the QOD API instead.
     */
    private void fetchQuoteOfTheDayFromFirebase() {
        Firebase f = new Firebase("https://fiery-heat-4873.firebaseio.com//").child("quotes");


        int year = getCurrentTime(10);
        int month = getCurrentTime(GoalPlannerModel.MONTH);
        int day = getCurrentTime(GoalPlannerModel.DAY);
        Log.d("DBG_qod_date","Will fetch quote from firebase for date: "+"" + year + "" + month + "" + day);
        f= f.child("" + year + "" + month + "" + day);

        if(qodEventListener!=null){
            f.removeEventListener(qodEventListener);
        }

        qodEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot qod) {

                if(qod.exists()){

                    JSONObject o = new JSONObject();
                    try {
                        o.put("tag", TAG_QUOTE_OF_THE_DAY);
                        o.put("quote", qod.getValue());
                        setChanged();
                        notifyObservers(o);
                        Log.i("QuoteResult",o.toString());
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }else{
                    fetchQuoteOfTheDayFromAPI();
                }

            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        };

        f.addValueEventListener(qodEventListener);

    }

    private int getCurrentTime(int timeType){

        Calendar calendar;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Stockholm"), Locale.forLanguageTag("sv"));
        }else{
            calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Stockholm"),Locale.UK);
        }
        if(timeType==GoalPlannerModel.DAY){
            return calendar.get(Calendar.DAY_OF_MONTH);
        }else if(timeType==GoalPlannerModel.MONTH){
            return calendar.get(Calendar.MONTH)+1;
        }else {
            return calendar.get(Calendar.YEAR);
        }
    }

    public void setUserID(String userID){
        this.userID=userID;
    }


}
