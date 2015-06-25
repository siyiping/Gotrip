package com.siyiping.gotrip.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.example.siyiping.gotrip.R;

public class Signin extends Activity {

    private EditText mTextphone;
    private EditText mTextcode;
    private String mPhone;
    private String mCode;
    private Button mGetcode;
    private Button mNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
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
        mTextcode=(EditText)findViewById(R.id.verification_code);
        mGetcode=(Button)findViewById(R.id.getcode);
        mGetcode.setOnClickListener(l);
        mNext=(Button)findViewById(R.id.sign_up_next);
        mNext.setOnClickListener(l);
    }

    View.OnClickListener l=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.getcode:
                    mTextphone.setEnabled(false);
                    String phone=mTextphone.getText().toString();
                    if(isPhoneValid(phone))
                        requestCode(phone);
                    else
                        mTextphone.setEnabled(true);
                        Toast.makeText(getApplicationContext(),"请输入正确的手机号码",Toast.LENGTH_LONG).show();

                    break;

                case R.id.sign_up_next:

                    break;

            }


        }
    };

    private boolean isPhoneValid(String phone) {
        //TODO: Replace this with your own logic
        return phone.length()==11;
    }

    private void requestCode(String mPhone){

        try {
            AVOSCloud.requestSMSCode(mPhone, getResources().getString(R.string.app_name), getResources().getString(R.string.operation_signin), 15);
        } catch (AVException e) {
            e.printStackTrace();
        }

    }

}
