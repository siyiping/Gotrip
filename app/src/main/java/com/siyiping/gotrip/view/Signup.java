package com.siyiping.gotrip.view;

import android.app.Activity;
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
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.siyiping.gotrip.R;


public class Signup extends Activity {

    private EditText mTextphone;
    private EditText mTextCode;
    private Button mGetcode;
    private EditText mPassword;
    private EditText mPasswordconfirn;
    private EditText mNickname;
    private Button mButtoncomplete;
    private boolean mGetButtonclick;
    private String nickname=null;
    private String phone=null;
    private String password=null;
    private String passwordconfirm=null;
    private String code=null;
    private boolean isPasswordConfirned=false;;

    private AVUser newuser = new AVUser();

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

        mTextphone=(EditText)findViewById(R.id.new_account_phone);
        mGetcode=(Button)findViewById(R.id.getcode);
        mGetcode.setOnClickListener(l);
        mButtoncomplete=(Button)findViewById(R.id.finish_signup);
        mButtoncomplete.setOnClickListener(l);
        mPassword=(EditText)findViewById(R.id.input_password);
        mPassword.setOnFocusChangeListener(listener);
        mPasswordconfirn=(EditText)findViewById(R.id.input_confirn_password);
        mNickname=(EditText)findViewById(R.id.input_nickname);
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
                if(s.length()==6)
                    mButtoncomplete.setEnabled(true);
            }
        });
    }

    //提示密码不符合输入规则
    View.OnFocusChangeListener listener=new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(!hasFocus){
                if(mPassword.length()<6) {
                    mPassword.setError(getString(R.string.password_too_short));
                    mPassword.setText("");
                }else if (mPassword.length()>15) {
                    mPassword.setError(getString(R.string.password_too_long));
                    mPassword.setText("");
                }

            }
        }
    };


    View.OnClickListener l=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Looper.getMainLooper();

            switch (v.getId()) {

                case R.id.getcode:
                    if(!checkInputIsLegal())
                        return;
                    mGetcode.setEnabled(false);
                    mGetButtonclick = true;
                    mTextCode.requestFocus();
                    downTimer.start();
                    newuser.setUsername(nickname);
                    newuser.setPassword(password);
                    newuser.setMobilePhoneNumber(phone);

                    newuser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(AVException e) {
                            if(e!=null){
                                //成功注册
                            }else{
                                e.printStackTrace();
                            }
                        }
                    });


                    break;

                case R.id.finish_signup:
                    String mCode = mTextCode.getText().toString();
                    if (!isCodeValid(mCode)) {
                        mTextCode.setError(getString(R.string.pleas_input_correct_code));
                        return;
                    }

                    newuser.verifyMobilePhoneInBackground(mCode, new AVMobilePhoneVerifyCallback() {
                        @Override
                        public void done(AVException e) {
                            // TODO Auto-generated method stub
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


    //重发短信倒计时
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


    private boolean ispasswordlegal(){
        int length=mPassword.length();
        if(length<6){
            mPassword.setError(getString(R.string.password_too_short));
            return false;
        }else if(length>15){
            mPassword.setError(getString(R.string.password_too_long));
            return false;
        }else{
            return true;
        }
    }

    private boolean checkInputIsLegal(){
        nickname=mNickname.getText().toString();
        if(nickname.length()<5 || nickname.length()>15){
            mNickname.setError("请输入正确长度的昵称");
            return false;
        }

        phone = mTextphone.getText().toString();
        if (!isPhoneValid(phone)) {
            mTextphone.setError("请输入正确的手机号");
            return false;
        }

        password=mPassword.getText().toString();
        if(password.length()<6 || password.length()>15){
            mPassword.setError("请输入正确长度的密码");
            return false;
        }

        passwordconfirm=mPasswordconfirn.getText().toString();
        if(!passwordconfirm.equals(password)){
            mPasswordconfirn.setError("两次输入的密码不对");
            mPasswordconfirn.setText(null);
            mPassword.setText(null);
            return false;
        }
        return true;
    }



}
