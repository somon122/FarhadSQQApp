package com.farhad.quiz_question.Quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.farhad.quiz_question.BlockClass;
import com.farhad.quiz_question.Short_Question.QuestionActivity;
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
import com.google.firebase.database.ValueEventListener;
import com.farhad.quiz_question.All_StoreClass.QuizControlClass;
import com.farhad.quiz_question.All_StoreClass.ScoreControl;
import com.farhad.quiz_question.All_StoreClass.TimeControlClass;
import com.farhad.quiz_question.ClickActivity;
import com.farhad.quiz_question.Questions;
import com.farhad.quiz_question.R;

import java.util.Locale;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private RadioButton answerButtonNo1, answerButtonNo2, answerButtonNo3, answerButtonNo4;
    private TextView questionTV,counterTV;

    private Questions questions = new Questions();
    private String mAnswer;
    private int mQuestionsLenght = questions.mQuestions.length;
    private Random r;
    int score;
    int blockCount = 0;
    int adsShowCount=0;

    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button submit;

    ScoreControl scoreControl;
    private MediaPlayer player;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String uId;
    QuizControlClass quizControlClass;

    FirebaseAuth auth;
    FirebaseUser user;
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Toolbar toolbar = findViewById(R.id.quizToolBar_id);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Quiz Test");

        r = new Random();

        answerButtonNo1 = findViewById(R.id.answerNo1_id);
        answerButtonNo2 =  findViewById(R.id.answerNo2_id);
        answerButtonNo3 =  findViewById(R.id.answerNo3_id);
        answerButtonNo4 =  findViewById(R.id.answerNo4_id);
        submit =  findViewById(R.id.quizShowSubmit_id);

        questionTV =  findViewById(R.id.question_id);
        counterTV =  findViewById(R.id.counter_id);
        radioGroup = findViewById(R.id.quizOptionGroup);
        scoreControl = new ScoreControl();
        quizControlClass = new QuizControlClass(this);


        updateQuestion(r.nextInt(mQuestionsLenght));
        counterTV.setText(quizControlClass.getScore()+"/"+"60");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        if (user != null){
            uId = user.getUid();
            blockUser();

        }



        MobileAds.initialize(this, getString(R.string.test_AppUnitId));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.test_InterstitialAdUnit));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mAdView = findViewById(R.id.quizBanner_id);
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

                                Toast.makeText(QuizActivity.this, "You account is block", Toast.LENGTH_SHORT).show();
                                auth.signOut();

                            }else {
                                Toast.makeText(QuizActivity.this, "You are doing mistake", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(QuizActivity.this, "You are doing mistake", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else {

                    myRef.child("UserMistakeAmount").child(uId).setValue(String.valueOf(add)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(QuizActivity.this, "You are doing mistake", Toast.LENGTH_SHORT).show();

                            }else {
                                Toast.makeText(QuizActivity.this, "You are doing mistake", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }



            }

            @Override
            public void onAdClosed() {

                score = 1;
                checkAnswer(score);


            }
        });



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (radioGroup.getCheckedRadioButtonId() == -1){

                    radioGroup.clearCheck();
                    Toast.makeText(QuizActivity.this, "Please select any one option", Toast.LENGTH_SHORT).show();



                }else {
                    int radioId = radioGroup.getCheckedRadioButtonId();
                    radioButton = findViewById(radioId);
                    String status = radioButton.getText().toString();

                    if (status.equals(mAnswer)) {

                        if (adsShowCount >= 4) {
                            if (mInterstitialAd.isLoaded()) {
                                radioGroup.clearCheck();
                                play();
                                mInterstitialAd.show();


                            } else {
                                score = 1;
                                radioGroup.clearCheck();
                                play();
                                checkAnswer(score);
                            }
                        } else {
                            score = 1;
                            radioGroup.clearCheck();
                            play();
                            checkAnswer(score);
                        }

                    }else {

                        radioGroup.clearCheck();
                        playWrong();
                        updateQuestion(r.nextInt(mQuestionsLenght));
                        counterTV.setText(quizControlClass.getScore()+"/"+"60");

                    }
                }


            }
        });



    }

    private void playWrong() {

        if (player == null){
            player = MediaPlayer.create(QuizActivity.this,R.raw.wrong_ans);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopPlayer();
                }
            });
        }
        player.start();


    }




    private void play (){

        if (player == null){
            player = MediaPlayer.create(QuizActivity.this,R.raw.carrect_answer);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopPlayer();
                }
            });
        }
        player.start();

    }



    private void stopPlayer() {
        if (player != null){
            player.release();
            player=null;
        }
    }



    private void updateQuestion(int num) {

        questionTV.setText(questions.getQuestion(num));
        answerButtonNo1.setText(questions.getChoices1(num));
        answerButtonNo2.setText(questions.getChoices2(num));
        answerButtonNo3.setText(questions.getChoices3(num));
        answerButtonNo4.setText(questions.getChoices4(num));
        mAnswer = questions.getCarrectAnswer(num);


    }
    private void checkAnswer(final int mScore){

        AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);

        builder.setTitle("Answer Panel");
        builder.setMessage("Congratulation..!"+"\n\n"+"You Answer is Correct"+
                "\n"+" Click Ok For Next Quiz ..." +
                "\n")
                .setCancelable(false)
                .setPositiveButton(" Ok ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                        if (quizControlClass.getScore() >=59){

                            timeIsRunning();

                        }else {

                            adsShowCount = adsShowCount+1;
                            int value = quizControlClass.getScore()+mScore;
                            quizControlClass.setStoreScore(value);

                            updateQuestion(r.nextInt(mQuestionsLenght));
                            counterTV.setText(quizControlClass.getScore()+"/"+"60");


                            if (adsShowCount>=3){
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                            }


                        }





                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();


    }



    private void timeIsRunning() {

        quizControlClass.Delete();
        Intent intent = new Intent(QuizActivity.this, ClickActivity.class);
        startActivity(intent);
        finish();


    }



    @Override
    public void onStop() {
        super.onStop();

        if (player != null){
            stopPlayer();
        }

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



}