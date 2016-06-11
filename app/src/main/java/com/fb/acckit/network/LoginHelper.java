package com.fb.acckit.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

import java.lang.ref.WeakReference;

/**
 * Created by Ashiq on 6/11/2016.
 */
public class LoginHelper {

    private Activity mActivity;
    private Context mContext;

    public static final int APP_LOGIN_REQUEST_CODE = 99;

    public LoginHelper(Activity activity) {
        this.mActivity = activity;
        mContext = mActivity.getApplicationContext();
    }

    public void init() {
        AccountKit.initialize(mContext);
    }

    public void onLogin(LoginType loginType) {
        final Intent intent = new Intent(mActivity, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(loginType, AccountKitActivity.ResponseType.TOKEN);

        // enable auto sms code pick
        configurationBuilder.setReadPhoneStateEnabled(true);
        configurationBuilder.setReceiveSMS(true);

        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
        mActivity.startActivityForResult(intent, APP_LOGIN_REQUEST_CODE);
    }

    public String validateRegistration(Intent data) {
        AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
        String accountId = null;
        if (loginResult.getError() != null) {
            accountId = null;
        } else if (loginResult.wasCancelled()) {
            accountId = null;
        } else {
            if (loginResult.getAccessToken() != null) {

                accountId = loginResult.getAccessToken().getAccountId();

                Log.e("Credentials", "Access token:" + loginResult.getAccessToken().getToken() + ", " +
                        "\n user access user id: " + loginResult.getAccessToken().getAccountId());

            } else {
                Log.e("Credentials", "Authorization code:" + loginResult.getAuthorizationCode());
            }
        }
        return accountId;
    }
}
