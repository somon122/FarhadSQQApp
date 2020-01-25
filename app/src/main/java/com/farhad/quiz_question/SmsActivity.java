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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;
import com.farhad.quiz_question.All_StoreClass.SmsControl;
import com.farhad.quiz_question.All_StoreClass.WaitingControl;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SmsActivity extends AppCompatActivity {


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private RewardedVideoAd mRewardedVideoAd;
    private InterstitialAd mInterstitialAd;
    private AdView adView;

    ProgressDialog dialog;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseFirestore mFirestore;
    private String uId;

    FirebaseAuth auth;
    FirebaseUser user;
    private SmsClass smsClass;
    private List<SmsClass>smsList;
    private int updateData;
    private int size;
    private String currentData;



    Button smsNextButton, smsShareButton;
    TextView smsShowTV, smsCounterTV;
    SmsControl smsControl;
    int adsShowScore;
    int blockCount;
    int score;

    private static final long START_TIME_IN_MILLIS2 = 5000;
    private CountDownTimer mCountDownTimer2;
    private boolean mTimerRunning2;
    private long mTimeLeftInMillis2;
    private long mEndTime2;

    private static final long START_TIME_IN_MILLIS = 3599000;
    private TextView waitingTV;
    private TextView waitingTV2;
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
        waitingTV = findViewById(R.id.smsWaiting_id);
        waitingTV2 = findViewById(R.id.smsWaiting2_id);
        waitingTV.setVisibility(View.GONE);
        waitingTV2.setVisibility(View.GONE);

        dialog = new ProgressDialog(this);
        waitingControl = new WaitingControl(this);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        mFirestore = FirebaseFirestore.getInstance();
        smsControl = new SmsControl(this);
        smsClass = new SmsClass();
        smsList = new ArrayList<>();


        AudienceNetworkAds.initialize(this);

        adView = new AdView(this, "122258155693784_122259775693622", AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = findViewById(R.id.banner_container);
        adContainer.addView(adView);
        adView.loadAd();

        mRewardedVideoAd = new RewardedVideoAd(this, "122258155693784_122260275693572");
        mInterstitialAd = new InterstitialAd(this, "122258155693784_122258845693715");

        mRewardedVideoAd.setAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoCompleted() {
                shareApp(currentData);
                mRewardedVideoAd.loadAd();
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }

            @Override
            public void onRewardedVideoClosed() {
                mRewardedVideoAd.loadAd();
            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {



            }
        });
        mRewardedVideoAd.loadAd();



        if (user != null) {
            uId = user.getUid();
            smsLoad();
            blockUser();

        }



        smsNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    if (updateData < size ){
                        if (adsShowScore >=3){
                            if (mInterstitialAd.isAdLoaded()){
                                mInterstitialAd.show();

                            }else {
                                nextSMS();
                            }

                        }else {
                            nextSMS();
                        }
                    }else {
                        dataStatus();
                    }

                }catch (Exception e){

                    dataStatus();
                }


            }
        });


        smsShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mRewardedVideoAd.isAdLoaded()){
                    mRewardedVideoAd.show();

                }else {
                    shareApp(currentData);
                }

            }
        });

        mInterstitialAd.setAdListener(new AbstractAdListener() {
            @Override
            public void onError(Ad ad, AdError error) {
                super.onError(ad, error);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                super.onAdLoaded(ad);
            }

            @Override
            public void onAdClicked(Ad ad) {
                super.onAdClicked(ad);


                int add = blockCount + 1;

                if (add >= 3) {

                    BlockClass blockClass = new BlockClass(uId, user.getDisplayName(), user.getEmail());
                    myRef.child("BlockUser").child(uId).setValue(blockClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(SmsActivity.this, "You account is block", Toast.LENGTH_SHORT).show();
                                auth.signOut();

                            } else {
                                Toast.makeText(SmsActivity.this, "You are doing mistake", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                } else {

                    myRef.child("UserMistakeAmount").child(uId).setValue(String.valueOf(add)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(SmsActivity.this, "You are doing mistake", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                }
            }

            @Override
            public void onInterstitialDisplayed(Ad ad) {
                super.onInterstitialDisplayed(ad);
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                super.onInterstitialDismissed(ad);

                adsShowScore = 0;
                mInterstitialAd.loadAd();

            }

            @Override
            public void onLoggingImpression(Ad ad) {
                super.onLoggingImpression(ad);
            }
        });
        mInterstitialAd.loadAd();




    }

    private void nextSMS() {

        adsShowScore = adsShowScore + 1;
        updateData = updateData +1;
        updateQuestion(updateData);
        smsCounterTV.setText(updateData + "/" + size);
        waitingTV2.setVisibility(View.VISIBLE);
        smsNextButton.setVisibility(View.GONE);
        startTimer2();

        if (!mInterstitialAd.isAdLoaded()){
        mInterstitialAd.loadAd();
        }

    }

    @Override
    public void onBackPressed() {

        if (mTimerRunning) {
            finishAffinity();
        }
        super.onBackPressed();
    }

    private void updateQuestion(int num) {

        if (!smsList.isEmpty()){
            smsClass = smsList.get(num);
            smsShowTV.setText(smsClass.getmSMS());
            currentData = smsClass.getmSMS();


        }else {
            Toast.makeText(this, "Data is not available", Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    protected void onDestroy() {
        if (mRewardedVideoAd != null){
            mRewardedVideoAd.destroy();
        } if (mInterstitialAd != null){
            mInterstitialAd.destroy();
        }
        super.onDestroy();
    }

    private void dataStatus() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(SmsActivity.this);

        builder.setTitle("Congratulation!")
                .setMessage("You are completed all SMS")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(SmsActivity.this, MainActivity.class));
                        finish();

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void shareApp(String text) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareSub = "Romantic SMS";
        intent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(intent, "SMS World"));

    }


    private void smsLoad() {


        CollectionReference collectionReference = mFirestore.collection("SMS_Collection");
        collectionReference.orderBy("mTime", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){

                    if (task.isSuccessful()) {

                        smsList.clear();

                        for (DocumentSnapshot document : task.getResult()) {

                            SmsClass smsClass = new SmsClass(
                                    document.getString("mSMS"),
                                    document.getString("mTime")
                            );

                            smsList.add(smsClass);

                        }
                        updateQuestion(0);
                        size = smsList.size()-1;
                        smsCounterTV.setText("0"+"/"+size);

                    } else {
                        Toast.makeText(SmsActivity.this, "Quiz Show is Field", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }


    private void blockUser() {

        myRef.child("UserMistakeAmount").child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

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

                resetTimer();
            } else {
                waitingScore++;
                startTimer();
            }
        }

        if (waitingControl.getScore() > 0) {
            waitingScore++;
            startTimer();

        }

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
                waitingScore = 0;
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
        int hour = (int) ((mTimeLeftInMillis / 1000) / 60) / 60;
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minutes, seconds);


        if (waitingScore >= 1) {
            waitingTV.setVisibility(View.VISIBLE);
            smsNextButton.setVisibility(View.GONE);
            smsShareButton.setVisibility(View.GONE);
            waitingTV.setText("Wait for continue SMS.." + "\n" + timeLeftFormatted);
        } else {
            waitingTV.setVisibility(View.GONE);
            smsNextButton.setVisibility(View.VISIBLE);
            smsShareButton.setVisibility(View.VISIBLE);

        }


    }


    private void startTimer2() {
        mEndTime2 = System.currentTimeMillis() + mTimeLeftInMillis2;
        mCountDownTimer2 = new CountDownTimer(mTimeLeftInMillis2, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis2 = millisUntilFinished;
                updateCountDownText2();
            }

            @Override
            public void onFinish() {
                mTimerRunning2 = false;
                waitingTV2.setVisibility(View.GONE);
                smsNextButton.setVisibility(View.VISIBLE);
                resetTimer2();

            }
        }.start();

        mTimerRunning2 = true;
    }


    private void resetTimer2() {
        mTimeLeftInMillis2 = START_TIME_IN_MILLIS2;
        updateCountDownText2();

    }


    private void updateCountDownText2() {

        int seconds = (int) (mTimeLeftInMillis2 / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d", seconds);
        waitingTV2.setText("Please Wait : "+timeLeftFormatted);


    }


}
