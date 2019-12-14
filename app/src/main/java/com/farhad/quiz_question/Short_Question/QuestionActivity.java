package com.farhad.quiz_question.Short_Question;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.farhad.quiz_question.All_StoreClass.WaitingControl;
import com.farhad.quiz_question.BlockClass;
import com.farhad.quiz_question.ClickActivity;
import com.farhad.quiz_question.SmsActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.farhad.quiz_question.All_StoreClass.QuestionControlClass;
import com.farhad.quiz_question.Questions;
import com.farhad.quiz_question.R;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Random;

public class QuestionActivity extends AppCompatActivity {


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (mTimerRunning){
            finishAffinity();

        }else {
            if (user == null){
                finishAffinity();
            }
            super.onBackPressed();
        }

    }

    Button questionCheckButton;
    TextView scoreTV,questionViewTV;
    private Questions questions = new Questions();
    private int mQuestionsLenght = questions.mQuestions.length;
    String mAnswer;
    Random r;
    private QuestionControlClass questionControlClass;

    int score;
    int adsShowScore;
    int blockCount = 0;

    private InterstitialAd mInterstitialAd;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String uId;
    FirebaseAuth auth;
    FirebaseUser user;
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
        setContentView(R.layout.activity_question);


        Toolbar toolbar = findViewById(R.id.questionToolBar_id);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Short Question");

        questionCheckButton = findViewById(R.id.questionCheckButton_id);
        scoreTV = findViewById(R.id.questionScore_id);
        questionViewTV = findViewById(R.id.questionMain_id);
        waitingTV = findViewById(R.id.questionWaiting_id);
        waitingTV.setVisibility(View.GONE);
        questions = new Questions();
        questionControlClass = new QuestionControlClass(this);
        waitingControl = new WaitingControl(this);
        r = new Random();

        updateQuestion(r.nextInt(mQuestionsLenght));
        scoreTV.setText(questionControlClass.getScore()+"/"+"60");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");


        if (user != null){
            uId = user.getUid();
           // balanceControl();
            blockUser();
        }



        questionCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (adsShowScore >=4){
                    if (mInterstitialAd.isLoaded()){
                        mInterstitialAd.show();

                    }else {
                        score = 1;
                        checkAnswer(score,mAnswer);
                    }
                }else {
                    score = 1;
                    checkAnswer(score,mAnswer);
                }


            }
        });


        MobileAds.initialize(this, getString(R.string.test_AppUnitId));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.test_InterstitialAdUnit));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mAdView = findViewById(R.id.questionBanner_id);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


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

                                Toast.makeText(QuestionActivity.this, "You account is block", Toast.LENGTH_SHORT).show();
                                auth.signOut();

                            }else {
                                Toast.makeText(QuestionActivity.this, "You are doing mistake", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(QuestionActivity.this, "You are doing mistake", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                }


            }

            @Override
            public void onAdClosed() {

                if (waitingControl.getScore() >0){
                    startActivity(new Intent(QuestionActivity.this,QuestionActivity.class));
                }else {
                    score = 1;
                    adsShowScore=0;
                    checkAnswer(score,mAnswer);
                }




            }
        });




    }

    private void updateQuestion(int num) {

        questionViewTV.setText(questions.getQuestion(num));
        mAnswer = questions.getCarrectAnswer(num);


    }
    private void checkAnswer(final int mScore, String answer){

        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionActivity.this);

        builder.setTitle("Answer Panel");
        builder.setMessage("Congratulation..!"+"\n\n"+"You Answer is : "+answer+
                "\n\n"+" Click ok for next question ..." +
                "\n")
                .setCancelable(false)
                .setPositiveButton(" Ok ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                        if (questionControlClass.getScore() >=59){

                          questionControlClass.Delete();
                          startActivity(new Intent(QuestionActivity.this, ClickActivity.class));
                          finish();


                        }else {

                            adsShowScore = adsShowScore+1;
                            int value = questionControlClass.getScore()+mScore;
                            questionControlClass.setStoreScore(value);
                            updateQuestion(r.nextInt(mQuestionsLenght));
                            scoreTV.setText(questionControlClass.getScore()+"/"+"60");

                            if (adsShowScore>=3){
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                            }


                        }





                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();


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
            questionCheckButton.setVisibility(View.GONE);
            waitingTV.setText("Wait for continue Question.."+"\n"+timeLeftFormatted);
        }else {
            waitingTV.setVisibility(View.GONE);
            questionCheckButton.setVisibility(View.VISIBLE);

        }



    }





}
