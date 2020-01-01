package com.farhad.quiz_question.LogIn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.farhad.quiz_question.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;

public class SignUpActivity extends AppCompatActivity {


    Button signUpButton, logInButton, selectImage;
    EditText emailEditText, passwordEditText, confirmPasswordET, userNameET;
    ProgressDialog dialog;

    ImageView imageView;
    FirebaseAuth auth;
    private StorageReference mStorageRef;

    private Uri imageUri = null;
    private Bitmap compressedImageFile;
    FirebaseUser user;

    private int PHONE_PERMISSION_CODE = 1;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    String pushId;

    UserDeviceIdClass deviceIdClass;
    List<UserDeviceIdClass> deviceIdList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpButton = findViewById(R.id.signUpButton_id);
        logInButton = findViewById(R.id.logInButton_id);
        selectImage = findViewById(R.id.selectedImage);

        emailEditText = findViewById(R.id.signUpUsernameId);
        passwordEditText = findViewById(R.id.signUpPasswordId);
        confirmPasswordET = findViewById(R.id.confirmPasswordId);
        userNameET = findViewById(R.id.userName_id);
        imageView = findViewById(R.id.signUp_imageView_id);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
        pushId = myRef.push().getKey();

        deviceIdList = new ArrayList<>();
        deviceIdClass = new UserDeviceIdClass();


        auth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("AccountImage");
        dialog = new ProgressDialog(this);

        uniqueUserDeviceId();


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(SignUpActivity.this,
                            Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

                        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        String deviceId = telephonyManager.getDeviceId();
                        checkExistsAccount(deviceId);


                    } else {
                        requestPhoneStatePermission();
                    }


                } else {

                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String deviceId = telephonyManager.getDeviceId();
                    checkExistsAccount(deviceId);

                }

            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
                finish();
                Toast.makeText(SignUpActivity.this, "call right", Toast.LENGTH_SHORT).show();

            }
        });


        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.activity()
                        .setMinCropResultSize(1080, 1920)
                        .setAspectRatio(1, 1)
                        .setAutoZoomEnabled(true)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SignUpActivity.this);


            }
        });

    }

    private void checkExistsAccount(String deviceId) {


        if (deviceIdClass.getmDeviceId().contains(deviceId)) {

            Toast.makeText(this, "You are already Registered", Toast.LENGTH_SHORT).show();

        } else {

            if (deviceId != null) {

                Toast.makeText(this, "You are able Registered", Toast.LENGTH_SHORT).show();
                isRegister(deviceId);

            } else {
                Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
            }


        }


    }

    private void uniqueUserDeviceId() {

        myRef.child("UniqueDeviceId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    deviceIdList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        deviceIdClass = snapshot.getValue(UserDeviceIdClass.class);

                    }
                    deviceIdList.add(deviceIdClass);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void isRegister(final String uploadDeviceId) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordET.getText().toString();

        final String userName = userNameET.getText().toString();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Please Enter Valid Email Address");
        } else if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Please Enter Valid Password");
        } else if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordET.setError("Please enter matching Password by above");
        } else if (TextUtils.isEmpty(userName)) {
            userNameET.setError("Please enter Name");
        } else {

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            if (firebaseUser == null) {

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "Confirm Password could not match ", Toast.LENGTH_SHORT).show();

                } else {


                    if (imageUri != null && uploadDeviceId != null) {

                        dialog.show();
                        dialog.setMessage("Register is progressing ...");

                        auth.createUserWithEmailAndPassword(email, confirmPassword)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            user = auth.getCurrentUser();


                                            File newImageFile = new File(imageUri.getPath());


                                            try {
                                                compressedImageFile = new Compressor(SignUpActivity.this)
                                                        .setMaxWidth(720)
                                                        .setMaxHeight(570)
                                                        .setQuality(75)
                                                        .compressToBitmap(newImageFile);

                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                            byte[] newImageData = baos.toByteArray();

                                            final StorageReference imageName = mStorageRef.child(imageUri.getLastPathSegment()).child(".jpg");


                                            imageName.putBytes(newImageData)
                                                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                                            if (task.isSuccessful()) {

                                                                imageName.getDownloadUrl()
                                                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                            @Override
                                                                            public void onSuccess(Uri uri) {


                                                                                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                                                                        .setDisplayName(userName)
                                                                                        .setPhotoUri(uri)
                                                                                        .build();
                                                                                user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {


                                                                                        if (task.isSuccessful()) {

                                                                                            deviceIdClass = new UserDeviceIdClass(uploadDeviceId);

                                                                                            myRef.child("UniqueDeviceId").child(pushId).setValue(deviceIdClass)
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {


                                                                                                            if (task.isSuccessful()) {

                                                                                                                dialog.dismiss();
                                                                                                                auth.signOut();
                                                                                                                Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
                                                                                                                intent.putExtra("alert", "alert");
                                                                                                                startActivity(intent);
                                                                                                                finish();

                                                                                                            } else {
                                                                                                                dialog.dismiss();
                                                                                                                Toast.makeText(SignUpActivity.this, "Try again", Toast.LENGTH_SHORT).show();

                                                                                                            }


                                                                                                        }
                                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                                @Override
                                                                                                public void onFailure(@NonNull Exception e) {

                                                                                                    dialog.dismiss();
                                                                                                    Toast.makeText(SignUpActivity.this, "Try again", Toast.LENGTH_SHORT).show();

                                                                                                }
                                                                                            });

                                                                                        } else {
                                                                                            dialog.dismiss();
                                                                                            Toast.makeText(SignUpActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                                                                                        }


                                                                                    }
                                                                                });


                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception exception) {

                                                                        dialog.dismiss();

                                                                    }
                                                                });


                                                            } else {

                                                                dialog.dismiss();
                                                                Toast.makeText(SignUpActivity.this, "Upload is Field", Toast.LENGTH_SHORT).show();
                                                            }


                                                        }
                                                    });


                                        } else {
                                            dialog.dismiss();
                                            Toast.makeText(SignUpActivity.this, " check Email, Password and net Connection", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    } else {

                        Toast.makeText(this, "Please select Image ", Toast.LENGTH_SHORT).show();
                    }

                }

            } else {
                dialog.dismiss();
                Toast.makeText(SignUpActivity.this, "You have already Registered", Toast.LENGTH_SHORT).show();
            }


        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                imageView.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Problem " + error, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void requestPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed for Register or SignUp")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(SignUpActivity.this,
                                    new String[]{Manifest.permission.READ_PHONE_STATE}, PHONE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, PHONE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PHONE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Permission granted and you can access this app", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}





