package com.curious.dina.goals.View;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.curious.dina.goals.Model.GoalsModel;
import com.curious.dina.goals.Model.RewardsModel;
import com.curious.dina.goals.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;

public class CatPhotosView implements Observer{
    private View view;
    private GoalsModel model;
    private ImageView imageView;
    private TextView textView;

    public CatPhotosView(View view, GoalsModel model){
        this.view = view;
        this.model = model;
        this.model.getRewardsModel().addObserver(this);

        imageView = (ImageView)view.findViewById(R.id.cat_image);
        textView = (TextView) view.findViewById(R.id.loading_text);
    }

    @Override
    public void update(Observable o, Object data){
        if(data.getClass().equals(JSONObject.class)){
            JSONObject j = (JSONObject) data;
          try {
                if (j.get("tag").equals(RewardsModel.TAG_REWARDS)) {
                    String url = j.getString("url");
                    loadImage(url);
                }
            }catch(JSONException e){e.printStackTrace();}
        }
    }

    /**
     * Help method to load the cat image
     */
    private void loadImage(String url){
        class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
            protected Bitmap doInBackground(String... urls) {
                String urldisplay = urls[0];
                Bitmap mIcon11 = null;
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                return mIcon11;
            }
            protected void onPostExecute(Bitmap result) {
                imageView.setImageBitmap(result);
                textView.setText("");
            }
        }
        new DownloadImageTask()
                .execute(url);
    }
}
