package com.farhad.quiz_question;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.farhad.quiz_question.All_StoreClass.ClickControl;
import com.farhad.quiz_question.All_StoreClass.ControlClass;
import com.farhad.quiz_question.All_StoreClass.QuizControlClass;
import com.farhad.quiz_question.LogIn.LogInActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ClickActivity extends AppCompatActivity {


    private Button clickButton;

    private InterstitialAd mInterstitialAd;
    private int adCount;
    private CountDownTimer countDownTimer;
    private long timeLeft = 59000;
    private boolean timeRunning;
    private String timeText;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;

    String uId;
    int oldBalance;
    int oldClicks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        if (user != null) {
            uId = user.getUid();
            notificationMethod();
        }
        clickButton = findViewById(R.id.clickButton_id);


        MobileAds.initialize(this, getString(R.string.test_AppUnitId));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.click_InterstitialAdUnit));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        clickButton.setVisibility(View.GONE);


        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {

                    Toast.makeText(ClickActivity.this, " Try Again.. Ok! ", Toast.LENGTH_SHORT).show();

                }


            }
        });

        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {

                clickButton.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(ClickActivity.this, "Please try Again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {

            }

            @Override
            public void onAdLeftApplication() {

                startTimer();

            }

            @Override
            public void onAdClosed() {

                if (adCount >= 1) {

                    ClickControl clickControl = new ClickControl(ClickActivity.this);
                    clickControl.setStoreScore(100);
                    SignOut();


                } else {
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    Toast.makeText(ClickActivity.this, " Try Again.. Ok! ", Toast.LENGTH_SHORT).show();

                }


            }

        });


    }

    private void notificationMethod() {

        myRef.child("UserMainPoints").child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String value = dataSnapshot.getValue(String.class);
                    oldBalance = Integer.valueOf(value);

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        myRef.child("UserClickList").child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String value = dataSnapshot.getValue(String.class);
                    oldClicks = Integer.valueOf(value);

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


    }


    private void SignOut() {

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ClickActivity.this, LogInActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    private void startTimer() {
        if (timeRunning) {
            stopTime();
        } else {
            startTime();
        }

    }


    private void startTime() {
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimer();

            }

            @Override
            public void onFinish() {

                int newBalance = oldBalance + 3;
                myRef.child("UserMainPoints").child(uId).setValue(String.valueOf(newBalance)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            myRef.child("WaitingTime").child(uId).setValue(String.valueOf(100)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        int newClick = oldClicks + 1;

                                        myRef.child("UserClickList").child(uId).setValue(String.valueOf(newClick))
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {

                                                            adCount++;
                                                        } else {
                                                            adCount++;

                                                        }

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                                    } else {
                                        adCount++;
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    adCount++;
                                }
                            });


                        } else {

                            adCount++;
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        adCount++;
                    }
                });


            }
        }.start();
        timeRunning = true;
    }

    private void updateTimer() {

        int minutes = (int) (timeLeft / 60000);
        int seconds = (int) (timeLeft % 60000 / 1000);
        timeText = "" + minutes;
        timeText += ":";
        if (seconds < 10) timeText += "0";
        timeText += seconds;
        Toast.makeText(this, "Wait: " + timeText, Toast.LENGTH_SHORT).show();

    }

    private void stopTime() {
        countDownTimer.cancel();
        timeRunning = false;


    }

}
