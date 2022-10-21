package fpt.prm.orderfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    EditText phone, otp;
    Button btngenOTP, btnverify;
    FirebaseAuth mAuth;
    String  verificationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phone = findViewById(R.id.phone);
        otp = findViewById(R.id.otp);
        btngenOTP = findViewById(R.id.btngenerateOTP);
        btnverify = findViewById(R.id.btnverifyOTP);
        mAuth = FirebaseAuth.getInstance();

        btngenOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String number = null;
                if (TextUtils.isEmpty(phone.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter valid phone number", Toast.LENGTH_LONG).show();
                } else {
                    String number = phone.getText().toString();

                    sendverificationcode(number);  // send your phone number to firebase and firebase check
                }
            }
        });

        btnverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String number = null;
                    if (TextUtils.isEmpty(otp.getText().toString())) {
                        Toast.makeText(MainActivity.this, "Wrong OTP", Toast.LENGTH_LONG).show();
                    }
                    else{
                        verifycode(otp.getText().toString());  // verify code user input match firebase code sent
                    }


            }
        });
    }

    private void sendverificationcode(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+84" + phoneNumber)  // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


        private PhoneAuthProvider.OnVerificationStateChangedCallbacks
                mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
//            Log.d(TAG, "onVerificationCompleted:" + credential);

//            signInWithPhoneAuthCredential(credential);
            final String code = credential.getSmsCode();
            if (code != null) {
                verifycode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
//            Log.w(TAG, "onVerificationFailed", e);

//            if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                // Invalid request
//            } else if (e instanceof FirebaseTooManyRequestsException) {
            // The SMS quota for the project has been exceeded
//        }

        // Show a message and update the UI

            Toast.makeText(MainActivity.this, " Verification Failed! ", Toast.LENGTH_SHORT).show();

    }


        @Override
        public void onCodeSent(@NonNull String s,
                @NonNull PhoneAuthProvider.ForceResendingToken token) {
//            // The SMS verification code has been sent to the provided phone number, we
//            // now need to ask the user to enter the code and then construct a credential
//            // by combining the code with a verification ID.
//            Log.d(TAG, "onCodeSent:" + verificationId);
//
//            // Save verification ID and resending token so we can use them later
//            mVerificationId = verificationId;
//            mResendToken = token;

            super.onCodeSent(s, token);
            verificationID = s;

        }
    };

    private void verifycode(String Code) {
        PhoneAuthCredential credential= PhoneAuthProvider.getCredential(verificationID, Code); //place where code entered by user as well as the code sent by firebase is match
//        PhoneAuthCredential credential= PhoneAuthCredential.
        signinbyCredentials(credential);
    }

    private void signinbyCredentials(PhoneAuthCredential  credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                           @Override
                                           public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(MainActivity.this, "login successful", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                            }
                                           }
                                       }
                );


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser !=null ){
            startActivity(new Intent(MainActivity.this,HomeActivity.class));
            finish();
        }
    }
}