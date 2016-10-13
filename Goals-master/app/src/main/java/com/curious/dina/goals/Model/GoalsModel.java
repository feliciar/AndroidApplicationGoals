package com.curious.dina.goals.Model;

import android.util.Log;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;

/**
 * This is the main model for the Goals application.
 * This class has one GoalPlannerModel object, one
 * RewardsModel object and one RewardsModel object.
 * The classes containing the respective classes will
 * be found in their respective interfaces.
 */

public class GoalsModel extends Observable {
    GoalPlannerModel goalPlanner;
    RewardsModel rewards;
    private String userID = "";
    public static String FIREBASE_DATA_REQUEST = "https://fiery-heat-4873.firebaseio.com/users/";
    public static String FIREBASE_REQUEST = "https://fiery-heat-4873.firebaseio.com/";

    private String email,password;

    public GoalPlannerModel getGoalPlannerModel(){return goalPlanner;}
    public RewardsModel getRewardsModel(){return rewards;}

    public GoalsModel(GoalsApplication app){
        Firebase.setAndroidContext(app);
        GoalsFirebaseHelper firebaseHelper = new GoalsFirebaseHelper();
        goalPlanner = new GoalPlannerModel(userID, firebaseHelper);
        rewards=new RewardsModel(userID, firebaseHelper);
    }


    public void logout(){
        Firebase ref = new Firebase("https://fiery-heat-4873.firebaseio.com");
        ref.unauth();
    }

    public String getUserID(){
        return userID;
    }

    public String getUserEmail(){
        Firebase ref = new Firebase("https://fiery-heat-4873.firebaseio.com");
        if(ref.getAuth()==null)
            return "";

        return ref.getAuth().getProviderData().get("email").toString();
    }

    public boolean isLoggedIn(){
        Firebase ref = new Firebase("https://fiery-heat-4873.firebaseio.com");
        if(ref.getAuth()==null)
            return false;
        String id = ref.getAuth().getUid();
        if(id.equals(""))
            return false;
        this.userID = id.toString();
        goalPlanner.setUserID(userID);
        rewards.setUserID(userID);
        return true;
    }

    public void setEmailAndPassword(String email, String password){
        this.email = email;
        this.password = password;
    }



    public void createUser(final String emailC, final String passwordC){
        Firebase ref = new Firebase("https://fiery-heat-4873.firebaseio.com");


        ref.createUser(emailC, passwordC, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {

                setEmailAndPassword(emailC, passwordC);
                authenticateUser();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                JSONObject o = new JSONObject();
                try {
                    o.put("success",false);
                    o.put("errorCode",firebaseError.getCode());
                    o.put("errorMessage",firebaseError.getMessage());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setChanged();
                notifyObservers(o);

            }
        });
    }

    public void authenticateUser(){
        Firebase ref = new Firebase("https://fiery-heat-4873.firebaseio.com");
        ref.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                userID = authData.getUid();
                goalPlanner.setUserID(userID);
                rewards.setUserID(userID);
                setChanged();
                JSONObject o = new JSONObject();
                try {
                    o.put("success",true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                notifyObservers(o);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {


                setChanged();


                JSONObject o = new JSONObject();
                try {
                    o.put("success", false);
                    o.put("errorCode", firebaseError.getCode());
                    o.put("errorMessage",firebaseError.getMessage());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                notifyObservers(o);
            }

        });
    }
}
