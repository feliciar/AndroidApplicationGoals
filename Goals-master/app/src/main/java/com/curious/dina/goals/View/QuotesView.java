package com.curious.dina.goals.View;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.curious.dina.goals.Model.GoalsApplication;
import com.curious.dina.goals.Model.GoalsModel;
import com.curious.dina.goals.Model.RewardsModel;
import com.curious.dina.goals.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

public class QuotesView implements Observer{
    private View view;
    private GoalsModel model;
    private TextView quotesTextView;

    public QuotesView(View view, GoalsModel model){
        this.view = view;
        this.model = model;
        this.model.getRewardsModel().addObserver(this);

        quotesTextView = (TextView)view.findViewById(R.id.quote_text_view);

        TextView t = (TextView)view.findViewById(R.id.user_email);
        if(t!=null) {
            t.setText(model.getUserEmail());
        }
    }

    @Override
    public void update(Observable o, Object data){
        if(data.getClass().equals(JSONObject.class)){
            JSONObject j = (JSONObject) data;
            try {
                if (j.get("tag").equals(RewardsModel.TAG_QUOTE_OF_THE_DAY)) {
                    quotesTextView.setText(j.getString("quote"));
                    quotesTextView.setTextColor(Color.BLACK);
                }
            }catch(JSONException e){e.printStackTrace();}
        }
    }
}