package com.farhad.quiz_question.All_StoreClass;

import android.content.Context;
import android.content.SharedPreferences;

public class SmsControl {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private Context context;

    public SmsControl(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("SmsControl",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setStoreScore (int score){
        editor.putInt("score",score);
        editor.commit();

    }
    public int getScore(){
        int score = sharedPreferences.getInt("score",0);
        return score;


    }

    public void Delete(){
        editor.clear();
        editor.commit();

    }


}
