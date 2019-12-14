package com.farhad.quiz_question;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;

import com.farhad.quiz_question.All_StoreClass.WaitingControl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.farhad.quiz_question.Quiz.QuizActivity;
import com.farhad.quiz_question.Short_Question.QuestionActivity;
import com.squareup.picasso.Picasso;
import com.farhad.quiz_question.LogIn.LogInActivity;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    DrawerLayout drawer;
    NavigationView navigationView;


    TextView  userName,homeMainPointTV,waitingTimerShow;
    CircleImageView circleImageShowId, quiz,help,questionImage, smsImage;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String uId;

    SharedPreferences.Editor editor;

    //private static final long START_TIME_IN_MILLIS = 40000;

    private static final long START_TIME_IN_MILLIS = 3599000;


    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private long mEndTime;
    int waitingScore = 0;

    private WaitingControl waitingControl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        quiz =findViewById(R.id.quizButton_id);
        questionImage =findViewById(R.id.questionImage_id);

        help =findViewById(R.id.help_id);
        smsImage =findViewById(R.id.smsButton_id);

        auth = FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        userName = findViewById(R.id.homeProfileName_id);
        homeMainPointTV = findViewById(R.id.homeMainPoint_id);
        waitingTimerShow = findViewById(R.id.waitingTimerShow_id);
        circleImageShowId = findViewById(R.id.circleImageShowId);
        TextView email = findViewById(R.id.homeProfileEmail_id);
        waitingTimerShow.setVisibility(View.GONE);
        waitingControl = new WaitingControl(this);




        if (user != null){
            uId = user.getUid();
            notificationMethod();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            userName.setText(user.getDisplayName());
            email.setText(user.getEmail());
            Picasso.get().load(user.getPhotoUrl()).placeholder(R.drawable.person_black_24dp).into(circleImageShowId);

        }


        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    startActivity(new Intent(MainActivity.this, QuizActivity.class));


            }
        });



        questionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, QuestionActivity.class));

            }
        });

        smsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    startActivity(new Intent(MainActivity.this, SmsActivity.class));


            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this, "Help coming soon..", Toast.LENGTH_SHORT).show();


            }
        });




        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        if (id == R.id.nev_home){

            startActivity(new Intent(MainActivity.this, MainActivity.class));

        }
        if (id == R.id.nev_aboutMe_id){

            startActivity(new Intent(MainActivity.this, AboutMeActivity.class));

        }
        if (id == R.id.nev_PrivacyPolicy){

            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://supernicetitle.blogspot.com/2019/06/privacy-policy-fnf-developer-built.html?m=1")));

            }catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://supernicetitle.blogspot.com/2019/06/privacy-policy-fnf-developer-built.html?m=1")));
            }

        }

        if (id == R.id.nav_share){

            shareApp();

        }
        if (id == R.id.nav_rate_us){

            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
            }catch (ActivityNotFoundException e){

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));

            }


        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        return false;

            }
        });


    }



    private void notificationMethod() {


        myRef.child("UserMainPoints").child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String value = dataSnapshot.getValue(String.class);
                    homeMainPointTV.setText("Your Points: " + value);

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

       /* myRef.child("WaitingTime").child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String value = dataSnapshot.getValue(String.class);
                    waitingScore = Integer.parseInt(value);
                }else {

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });
*/

    }


        private void userProfile (){

        View userProfileUD = navigationView.getHeaderView(0);
        TextView userName = userProfileUD.findViewById(R.id.profileUserName_id);
        TextView userEmail = userProfileUD.findViewById(R.id.profileUserEmail_id);
        ImageView image = userProfileUD.findViewById(R.id.profileImageView);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        Picasso.get().load(user.getPhotoUrl()).placeholder(getDrawable(R.drawable.app_logo)).into(image);
        userName.setText(user.getDisplayName());
        userEmail.setText(user.getEmail());


    }




    private void shareApp() {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBody = "App link : "+"https://play.google.com/store/apps/details?id="+getPackageName();
        String shareSub = "Android App";
        intent.putExtra(Intent.EXTRA_SUBJECT,shareSub);
        intent.putExtra(Intent.EXTRA_TEXT,shareBody);
        startActivity(Intent.createChooser(intent,"Quiz_Question_app"));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_logOut){

          alert();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            finishAffinity();
        } else {
            super.onBackPressed();
            finishAffinity();
        }
    }

    private void alert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you Sure?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(MainActivity.this, "Successfully LogOut ", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),LogInActivity.class));
                        finish();



                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
                Toast.makeText(MainActivity.this, "Thank You for Staying...", Toast.LENGTH_SHORT).show();



            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }


 private void TimeAleart(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("You can able to work After complete Time")
               .setTitle("Waiting Time Alert !")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (waitingControl.getScore()==10){
                            finishAffinity();
                        }else {
                            dialog.dismiss();
                        }


                    }
                }).setNeutralButton("Watch Time", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                waitingTimerShow.setVisibility(View.VISIBLE);
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();


    }

    @Override
    public void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(MainActivity.this, LogInActivity.class));
        }else {
            userProfile();

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

           Bundle bundle = getIntent().getExtras();
            if (bundle != null){
                waitingControl.setStoreScore(10);
            }




        }
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

    private void timeValueDelete() {

        myRef.child("WaitingTime").child(uId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    waitingScore=0;
                    Toast.makeText(MainActivity.this, "You are able to work", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, MainActivity.class));

                }else {
                    Toast.makeText(MainActivity.this, "Please Check your net connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

            quiz.setEnabled(false);
            questionImage.setEnabled(false);
            smsImage.setEnabled(false);
            waitingTimerShow.setVisibility(View.VISIBLE);
            waitingTimerShow.setText("Waiting...\n"+timeLeftFormatted);


        }else {
            waitingControl.Delete();
            waitingTimerShow.setVisibility(View.GONE);
            quiz.setEnabled(true);
            questionImage.setEnabled(true);
            smsImage.setEnabled(true);
            timeValueDelete();



        }

    }




}