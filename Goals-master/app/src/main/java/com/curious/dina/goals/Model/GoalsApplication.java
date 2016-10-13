package com.curious.dina.goals.Model;

import android.app.Application;

public class GoalsApplication extends Application{
    private GoalsModel model;

    public GoalsApplication(){ }

    public GoalsModel getModel() {
        return model;
    }

    public void onCreate(){
        super.onCreate();
        model = new GoalsModel(this);
    }
    
}