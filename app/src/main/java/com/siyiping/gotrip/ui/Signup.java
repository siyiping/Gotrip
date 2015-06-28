package com.siyiping.gotrip.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.example.siyiping.gotrip.R;


public class Signup extends Activity {

    private EditText mTextphone;
    private EditText mTextCode;
    private Button mGetcode;
    private Button mNext;
    private boolean mGetButtonclick;
    private String phone=null;
    public final Context mContext=getApplicationContext();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initView(){

        mNext=(Button)findViewById(R.id.sign_up_next);
        mNext.setOnClickListener(l);
        mNext.setEnabled(false);
        mTextphone=(EditText)findViewById(R.id.new_account_phone);
        mGetcode=(Button)findViewById(R.id.getcode);
        mGetcode.setOnClickListener(l);
        mTextCode=(EditText)findViewById(R.id.verification_code);
        mTextCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==4 && mGetButtonclick)
                    mNext.setEnabled(true);
            }
        });

    }


    View.OnClickListener l=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Looper.getMainLooper();
            switch (v.getId()){
                case R.id.getcode:
                    mGetButtonclick=true;
                    mTextCode.requestFocus();
                    phone=mTextphone.getText().toString();
                    if(!isPhoneValid(phone)){
                        mTextphone.setError("请输入正确的手机号");
                        return;
                    }
                    mGetcode.setEnabled(false);
                    downTimer.start();
                    AVOSCloud.requestSMSCodeInBackground(phone, "自游行", "注册", 15, new RequestMobileCodeCallback(){
                        @Override
                        public void done(AVException e) {
                            e.printStackTrace();
                        }
                    });
//                    new AsyncTask<Void,Void,Void>(){
//                        boolean suc;
//
//                        @Override
//                        protected Void doInBackground(Void... par) {
//
//                            try {
//                                AVOSCloud.requestSMSCodeInBackground(phone, "自游行", "注册", 15);
//                                suc=true;
//                            } catch (AVException e) {
//                                e.printStackTrace();
//                                suc=false;
//                            }
//                            return null;
//                        }
//
//                        @Override
//                        protected void onPostExecute(Void aVoid) {
//                            super.onPostExecute(aVoid);
//                        }
//                    }.execute();

                    break;

                case R.id.sign_up_next:

                    String mCode=mTextCode.getText().toString();
                    if(!isCodeValid(mCode)){
                        mTextCode.setError(getString(R.string.pleas_input_correct_code));
                        return;
                    }

                    if(isCodeValid(mCode) && isPhoneValid(phone))


                    AVOSCloud.verifyCodeInBackground(mCode,phone,
                            new AVMobilePhoneVerifyCallback(){
                                @Override
                                public void done(AVException e){
                                    if(e==null){
                                        //发送成功
                                        Intent registernext=new Intent();
                                        registernext.setClass(mContext,ConfirpasswordActivity.class);
                                        registernext.putExtra("phone",phone);
                                        mContext.startActivity(registernext);
                                    }
                                }
                            });


                    break;

            }


        }
    };

    private boolean isPhoneValid(String phone) {
        //TODO: Replace this with your own logic
        return phone.length()==11;
    }

    private boolean isCodeValid(String code) {
        //TODO: Replace this with your own logic
        return code.length()==6;
    }


    CountDownTimer downTimer=new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mGetcode.setText(millisUntilFinished/1000+"秒后重发");
            }

            @Override
            public void onFinish() {
                mGetcode.setEnabled(true);
                mGetcode.setText(getString(R.string.get_verification_code));
            }
        };





}
