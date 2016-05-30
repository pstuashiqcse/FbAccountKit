package com.fb.acckit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
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

import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class MainActivity extends AppCompatActivity {

    private Activity mActivity;
    private Toolbar toolBar;
    private Button connectToFbButton, accKitPhoneButton, accKitEmailButton;
    private TextView loginStatusTextView;

    public static final int APP_REQUEST_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // don't forget to to initialize it before initialize your view
        AccountKit.initialize(getApplicationContext());
        mActivity = MainActivity.this;
        initViews();
        initListeners();
    }

    private void initViews() {
        setContentView(R.layout.activity_main);
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        connectToFbButton = (Button) findViewById(R.id.connectToFbButton);
        accKitPhoneButton = (Button) findViewById(R.id.accKitPhoneButton);
        accKitEmailButton = (Button) findViewById(R.id.accKitEmailButton);
        loginStatusTextView = (TextView) findViewById(R.id.loginStatusTextView);
    }

    private void initListeners() {
        connectToFbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        accKitPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoginPhone();
            }
        });

        accKitEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void checkAccessValidity() {
        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        if (accessToken != null) {
            //Handle Returning User
        } else {
            //Handle new or logged out user
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void onLoginPhone() {
        final Intent intent = new Intent(MainActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN);

        // enable auto sms code pick
        configurationBuilder.setReadPhoneStateEnabled(true);
        configurationBuilder.setReceiveSMS(true);

        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String statusMessage;
            if (loginResult.getError() != null) {
                statusMessage = loginResult.getError().getErrorType().getMessage();
            } else if (loginResult.wasCancelled()) {
                statusMessage = "Login Cancelled";
            } else {
                if (loginResult.getAccessToken() != null) {
                    statusMessage = "Success:" + loginResult.getAccessToken().getAccountId();

                    String hmac = hmacSha256(loginResult.getAccessToken().getToken(), getString(R.string.app_secret));

                    Log.e("Credentials", "Access token:"+loginResult.getAccessToken().getToken()+", " +
                                    "\nhmac value: "+hmac+
                                    "\n user access user id: "+loginResult.getAccessToken().getAccountId()

                    );

                } else {
                    statusMessage = String.format(
                            "Success:%s...",
                            loginResult.getAuthorizationCode().substring(0,10));

                    Log.e("Credentials", "Authorization code:"+loginResult.getAuthorizationCode()

                    );
                }

                // If you have an authorization code, retrieve it from
                // loginResult.getAuthorizationCode()
                // and pass it to your server and exchange it for an access token.

                // Success! Start your next activity...
                setRegistrationStatus(statusMessage);



            }


        }
    }

    private void setRegistrationStatus(String status) {
        loginStatusTextView.setText(status);

        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(final Account account) {
                // Get Account Kit ID
                String accountKitId = account.getId();

                // Get phone number
                PhoneNumber phoneNumber = account.getPhoneNumber();
                String phoneNumberString = phoneNumber.toString();

                // Get email
                String email = account.getEmail();

                Log.e("Current account", "Phone: "+phoneNumber);
            }

            @Override
            public void onError(final AccountKitError error) {
                // Handle Error
                Log.e("Current account", "Error: "+error.toString());
            }
        });
    }

    private static String toHexString(final byte[] bytes) {
        final Formatter formatter = new Formatter();
        for (final byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    public static String hmacSha256(final String key, final String s) {
        try {
            final Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA256"));
            return toHexString(mac.doFinal(s.getBytes()));
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
