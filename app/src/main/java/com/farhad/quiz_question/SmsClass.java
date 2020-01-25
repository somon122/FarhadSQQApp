package com.farhad.quiz_question;

public class SmsClass {

    String mSMS;
    String mTime;

    public SmsClass() {
    }

    public SmsClass(String mSMS, String mTime) {
        this.mSMS = mSMS;
        this.mTime = mTime;
    }

    public String getmSMS() {
        return mSMS;
    }

    public void setmSMS(String mSMS) {
        this.mSMS = mSMS;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }
}
