package com.farhad.quiz_question.LogIn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.farhad.quiz_question.All_StoreClass.WaitingControl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.farhad.quiz_question.All_StoreClass.ClickControl;
import com.farhad.quiz_question.MainActivity;
import com.farhad.quiz_question.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogInActivity extends AppCompatActivity {


    Button signUpButton, logInButton;
    EditText emailEditText, passwordEditText;
    FirebaseAuth auth;
    FirebaseUser user;
    ProgressDialog dialog;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    WaitingControl waitingControl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");

        signUpButton = findViewById(R.id.gotoSignUpPageId);
        logInButton = findViewById(R.id.logInId);
        emailEditText = findViewById(R.id.logInEmailId);
        passwordEditText = findViewById(R.id.logInPasswordId);
        waitingControl = new WaitingControl(LogInActivity.this);


        dialog = new ProgressDialog(this);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClickControl clickControl = new ClickControl(LogInActivity.this);
                if (clickControl.getScore() <= 0) {

                    isLogIn();
                } else {
                    dattClearAlert();
                }


            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
            }
        });


    }

    private void dattClearAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);

        builder.setTitle("Data clear Alert !")
                .setMessage("Please clear this app data from setting first\n\n Then LogIn")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                finishAffinity();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void blockUser(final String uId) {

        myRef.child("UserMistakeAmount").child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    String value = dataSnapshot.getValue(String.class);
                    int blockCount = Integer.parseInt(value);
                    justify(blockCount, uId);

                } else {

                    waitingMethod(uId);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void justify(int point, String uId) {

        if (point >= 2) {

            FirebaseAuth.getInstance().signOut();
            dialog.dismiss();
            Toast.makeText(this, "your account is block for invalid click..!", Toast.LENGTH_SHORT).show();

        } else {

            waitingMethod(uId);
        }


    }


    private void waitingMethod(String uId) {


        myRef.child("WaitingTime").child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    dialog.dismiss();
                    Toast.makeText(LogInActivity.this, " Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                    intent.putExtra("Wait", "wait");
                    startActivity(intent);
                    finish();

                } else {

                    dialog.dismiss();
                    Toast.makeText(LogInActivity.this, " Login successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LogInActivity.this, MainActivity.class));
                    finish();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void isLogIn() {

        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Please Enter Valid Email Address");
        } else if (password.isEmpty()) {
            passwordEditText.setError("Please Enter Valid Password");
        } else {

            dialog.show();
            dialog.setMessage("LogIn is progressing ...");

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String uId = user.getUid();
                                blockUser(uId);

                            } else {
                                dialog.dismiss();
                                Toast.makeText(LogInActivity.this, "Email and Password is not match", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LogInActivity.this, "please check your net connection", Toast.LENGTH_SHORT).show();
                }
            });

        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }


}
