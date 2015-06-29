package com.siyiping.gotrip.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.example.siyiping.gotrip.R;

public class ConfirpasswordActivity extends Activity {

    private TextView mPassword;
    private TextView mPasswordconfirn;
    private TextView mNickname;
    private Button mButtoncomplete;
    private AVUser newuser=null;
    private boolean isPasswordConfirned=false;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirpassword);

        newuser=new AVUser();

        Intent mIntent=new Intent();
        newuser.setMobilePhoneNumber(mIntent.getStringExtra("phone"));

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

    private void initView(){
        mButtoncomplete=(Button)findViewById(R.id.finish_signup);
        mButtoncomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNickname.length()==0){
                    mNickname.setError(getString(R.string.forget_input_nickname));
                    return;
                }else if(isPasswordConfirned){
                    newuser.setUsername(mNickname.getText().toString());
                    newuser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(AVException e) {
                            Toast.makeText(getApplicationContext(),"注册成功",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        mPassword=(TextView)findViewById(R.id.input_password);
        mPassword.setOnFocusChangeListener(listener);
        mPasswordconfirn=(TextView)findViewById(R.id.input_confirn_password);
        mPasswordconfirn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("siyiping","mPasswordconfirn.length="+mPasswordconfirn.length()+" mPassword.length="+mPassword.length()+" mPassword.Text="+mPassword.getText()+" mPasswordconfirn.Text="+mPasswordconfirn.getText());
                if(mPasswordconfirn.length()>0 && mPassword.length()>5 && mPassword.length()<16 && mPassword.getText().toString().equals(mPasswordconfirn.getText().toString()) ) {
                    Log.e("siyiping","button complete enabe true");
                    newuser.setPassword(mPassword.getText().toString());
                    isPasswordConfirned = true;
                    mButtoncomplete.setEnabled(true);
                }
            }
        });

        mNickname=(TextView)findViewById(R.id.input_nickname);
        mNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

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


}
