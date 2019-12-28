package com.blogspot.noweshed.callafriend;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mFireDb;

    private EditText UserText;
    private EditText PhoneText;
    private EditText PasswordText;
    private CheckBox mchekbox;

    private TextView sendNumberId;
    private String phoneNumber;
    private String mVerificationId;

    private Button ConfirmRegBtn;
    private Button VerifyBtn;

    private ProgressDialog mProgressDialog;

    private LinearLayout registerLayout, otpLayout;

    private CountryCodePicker ccp;
    private PinView otp;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        View.OnClickListener monClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

        if (!isConnected(Register.this)) {
            Snackbar.make(findViewById(android.R.id.content), "Please connect to the internet first.", Snackbar.LENGTH_LONG)
                    .setAction("GOT IT!", monClickListener).setActionTextColor(Color.WHITE).show();
        }

        mProgressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        registerLayout = findViewById(R.id.registerLayId);
        otpLayout = findViewById(R.id.otpLayId);
        sendNumberId = findViewById(R.id.sendNumberId);

        ccp = findViewById(R.id.ccp);
        otp = findViewById(R.id.pinView);

        UserText = findViewById(R.id.usernameId);
        PhoneText = findViewById(R.id.emailId);
//        PasswordText = findViewById(R.id.passId);
        mchekbox = findViewById(R.id.checkboxId);
        ConfirmRegBtn = findViewById(R.id.ConfirmRegId);
        VerifyBtn = findViewById(R.id.verifyBtnId);

        ConfirmRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(UserText.getText().toString()) && !TextUtils.isEmpty(PhoneText.getText().toString())) {

                    phoneNumber = ccp.getDefaultCountryCodeWithPlus() + PhoneText.getText().toString();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            Register.this,    // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks

                    registerLayout.setVisibility(View.GONE);
                    otpLayout.setVisibility(View.VISIBLE);
                    sendNumberId.setText("Please type the verification code sent to\n" + ccp.getSelectedCountryCodeWithPlus() + PhoneText.getText());
                } else {
                    Toast.makeText(Register.this, "Provide a Valid Username & Phone Number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                String S_username = UserText.getText().toString();

                signInWithPhoneAuthCredential(phoneAuthCredential, S_username, phoneNumber);

                otp.setText(mVerificationId);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }
        };

        VerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String verificationCode = otp.getText().toString();
                if (verificationCode.isEmpty()) {
                    Toast.makeText(Register.this, "Enter verification code", Toast.LENGTH_SHORT).show();
                } else {

                    mProgressDialog.setTitle("Registering!");
                    mProgressDialog.setMessage("Your account has been registered. Please wait...");
                    mProgressDialog.show();
                    mProgressDialog.setCanceledOnTouchOutside(false);

                    String S_username = UserText.getText().toString();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);

                    signInWithPhoneAuthCredential(credential, S_username, phoneNumber);

                }
            }
        });


    }

    public boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnected()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else
                return false;
        } else
            return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent MainIntent = new Intent(Register.this, MainActivity.class);
            startActivity(MainIntent);
            finish();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential, final String s_username, final String s_phone) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = current_user.getUid();
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            mFireDb = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            HashMap<String, String> userMap = new HashMap<>();
                           

                            mFireDb.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mProgressDialog.dismiss();
                                        Intent mainIntent = new Intent(Register.this, MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                }
                            });

                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(Register.this, "Something wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
