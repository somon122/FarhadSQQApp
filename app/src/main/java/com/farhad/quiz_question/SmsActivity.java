package com.farhad.quiz_question;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.farhad.quiz_question.All_StoreClass.SmsControl;
import com.farhad.quiz_question.All_StoreClass.WaitingControl;
import com.farhad.quiz_question.Quiz.QuizActivity;
import com.farhad.quiz_question.Short_Question.QuestionActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Random;

public class SmsActivity extends AppCompatActivity  {


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){

            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    InterstitialAd mInterstitialAd;

    ProgressDialog dialog;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String uId;

    FirebaseAuth auth;
    FirebaseUser user;
    private SmsClass smsClass = new SmsClass();
    private int mQuestionsLenght = smsClass.mQuestions.length;
    Random r;
    String smsText;


    Button smsNextButton,smsShareButton;
    TextView smsShowTV,smsCounterTV;
    SmsControl smsControl;
    int adsShowScore;
    int blockCount;
    int score;
    private AdView mAdView;


    //private static final long START_TIME_IN_MILLIS = 40000;

    private static final long START_TIME_IN_MILLIS = 3599000;

    private TextView waitingTV;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private long mEndTime;
    int waitingScore;
    SharedPreferences.Editor editor;

    WaitingControl waitingControl;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);


        Toolbar toolbar = findViewById(R.id.rewardToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("SMS World");

        smsNextButton = findViewById(R.id.nextSms_id);
        smsShareButton = findViewById(R.id.shareSms_id);
        smsShowTV = findViewById(R.id.smsView_id);
        smsCounterTV = findViewById(R.id.SmsCounter_id);
        smsCounterTV = findViewById(R.id.SmsCounter_id);
        waitingTV = findViewById(R.id.smsWaiting_id);
        waitingTV.setVisibility(View.GONE);

        dialog = new ProgressDialog(this);
        waitingControl = new WaitingControl(this);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
        smsControl = new SmsControl(this);

        r = new Random();

        updateQuestion(r.nextInt(mQuestionsLenght));
        smsCounterTV.setText(smsControl.getScore()+"/"+"60");



        if (user != null){
            uId = user.getUid();
            blockUser();

        }

        MobileAds.initialize(this, getString(R.string.test_AppUnitId));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.test_InterstitialAdUnit));

        mAdView = findViewById(R.id.smsBanner_id);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        smsNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                score = 1;
                int add = smsControl.getScore()+score;
                smsControl.setStoreScore(add);
                adsShowScore = adsShowScore+1;
                updateQuestion(r.nextInt(mQuestionsLenght));
                smsCounterTV.setText(smsControl.getScore()+"/"+"60");

                if (smsControl.getScore() >=59){
                    smsControl.Delete();
                    startActivity(new Intent(SmsActivity.this,ClickActivity.class));
                    finish();

                }else {

                    if (adsShowScore >=4) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        }
                    }

                    if (adsShowScore>=3){
                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    }

                }





            }
        });


        smsShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shareApp(smsText);

            }
        });



        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {


            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdLeftApplication() {


                int add = blockCount+1;

                if (add >=3){

                    BlockClass blockClass = new BlockClass(uId,user.getDisplayName(),user.getEmail());
                    myRef.child("BlockUser").child(uId).setValue(blockClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                Toast.makeText(SmsActivity.this, "You account is block", Toast.LENGTH_SHORT).show();
                                auth.signOut();

                            }else {
                                Toast.makeText(SmsActivity.this, "You are doing mistake", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                }else {

                    myRef.child("UserMistakeAmount").child(uId).setValue(String.valueOf(add)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(SmsActivity.this, "You are doing mistake", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                }


            }

            @Override
            public void onAdClosed() {

                if (waitingControl.getScore() >0){
                    startActivity(new Intent(SmsActivity.this,SmsActivity.class));
                }else {
                    adsShowScore =0;
                }


            }
        });



    }

    @Override
    public void onBackPressed() {

        if (mTimerRunning){
            finishAffinity();
        }
        super.onBackPressed();
    }

    private void updateQuestion(int num) {

        smsShowTV.setText(smsClass.getQuestion(num));
        smsText = smsClass.getQuestion(num);


    }

    private void shareApp(String text) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareSub = "Romantic SMS";
        intent.putExtra(Intent.EXTRA_SUBJECT,shareSub);
        intent.putExtra(Intent.EXTRA_TEXT,text);
        startActivity(Intent.createChooser(intent,"SMS World"));

    }


    private  void  blockUser(){

        myRef.child("UserMistakeAmount").child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    String value = dataSnapshot.getValue(String.class);
                    blockCount = Integer.parseInt(value);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        editor = prefs.edit();
        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);
        editor.apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        mTimeLeftInMillis = prefs.getLong("millisLeft", START_TIME_IN_MILLIS);
        mTimerRunning = prefs.getBoolean("timerRunning", false);



        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                //updateCountDownText();

                resetTimer();
            } else {
                waitingScore++;
                startTimer();
            }
        }

        if (waitingControl.getScore() >0){
            waitingScore++;
            startTimer();

        }

       /* Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            startTimer();

        }
*/
    }

    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                //updateButtons();
                waitingScore=0;
                resetTimer();

            }
        }.start();

        mTimerRunning = true;
    }



    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();

    }




    private void updateCountDownText() {
        int hour = (int) ((mTimeLeftInMillis/1000) /60) /60;
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d",hour, minutes, seconds);


        if (waitingScore >=1){
            waitingTV.setVisibility(View.VISIBLE);
            smsNextButton.setVisibility(View.GONE);
            smsShareButton.setVisibility(View.GONE);
            waitingTV.setText("Wait for continue SMS.."+"\n"+timeLeftFormatted);
        }else {
            waitingTV.setVisibility(View.GONE);
            smsNextButton.setVisibility(View.VISIBLE);
            smsShareButton.setVisibility(View.VISIBLE);

        }



    }





}
