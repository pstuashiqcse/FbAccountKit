package com.fb.acckit.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.fb.acckit.R;
import com.fb.acckit.network.LoginHelper;
import com.fb.acckit.utility.PermissionUtils;


public class MainActivity extends AppCompatActivity {

    private Activity mActivity;
    private Context mContext;
    private Toolbar toolBar;
    private Button accKitPhoneButton, accKitEmailButton;
    private TextView loginStatusTextView;

    private LoginHelper loginHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = MainActivity.this;
        mContext =  mActivity.getApplicationContext();

        // don't forget to to initialize it before initialize your view
        loginHelper =  new LoginHelper(mActivity);
        loginHelper.init();

        initViews();
        initListeners();
    }

    private void initViews() {
        setContentView(R.layout.activity_main);
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        accKitPhoneButton = (Button) findViewById(R.id.accKitPhoneButton);
        accKitEmailButton = (Button) findViewById(R.id.accKitEmailButton);
        loginStatusTextView = (TextView) findViewById(R.id.loginStatusTextView);
    }

    private void initListeners() {

        accKitPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PermissionUtils.isPermissionGranted(mActivity, PermissionUtils.SMS_PERMISSIONS)) {
                    loginHelper.onLogin(LoginType.PHONE);
                }
            }
        });

        accKitEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginHelper.onLogin(LoginType.EMAIL);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionUtils.PERMISSIONS_REQUEST_CODE) {
            if(PermissionUtils.isPermissionResultGranted(grantResults)) {
                loginHelper.onLogin(LoginType.PHONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginHelper.APP_LOGIN_REQUEST_CODE) {
            String accountId = loginHelper.validateRegistration(data);
            setRegistrationStatus(accountId);
        }
    }

    private void setRegistrationStatus(String status) {
        if(status != null) {
            loginStatusTextView.setText("Success, Account ID: "+status);
            accKitPhoneButton.setEnabled(false);
        } else {
            loginStatusTextView.setText("Failed!");
        }
    }
}
