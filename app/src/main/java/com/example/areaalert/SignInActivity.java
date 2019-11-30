package com.example.areaalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SignInActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private final String TAG = "SignInActivity";
    String phonenumber, CodeSent;
    EditText CodeSign;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        Button Login = findViewById(R.id.Login);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login(v);
            }
        });

        Button Register1 = findViewById(R.id.Register1);
        Register1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallRegister1();
            }
        });

        Button SignIn = findViewById(R.id.SignIn);
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckOnVerification();
            }
        });
        pb = findViewById(R.id.signbar);
        pb.setVisibility(View.INVISIBLE);
        CodeSign = findViewById(R.id.CodeSign);
    }

    private void CallRegister1() {
        startActivity(new Intent(SignInActivity.this, RegisterActivity1.class));
    }

    private void CheckOnVerification() {

        String CodeRecieved = CodeSign.getText().toString();

        if(CodeRecieved.isEmpty()) {
            CodeSign.setText("Code is Required");
            CodeSign.requestFocus();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(CodeSent, CodeRecieved);
        signInWithPhoneAuthCredential(credential);

    }

    public void Login(View view){
        EditText PhoneNumber = findViewById(R.id.PhoneNumberS);
        phonenumber = PhoneNumber.getText().toString();
        if(phonenumber == null) {
            Toast.makeText(SignInActivity.this,"Enter a value in both the parameters",Toast.LENGTH_LONG).show();
        }
        else if (phonenumber.length() < 10){
            PhoneNumber.setText("Please Enter a Valid Phone Number");
            PhoneNumber.requestFocus();
        }
        else {
            pb.setVisibility(View.VISIBLE);
            phonenumber = "+91" + phonenumber;
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phonenumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this,               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks

        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        pb.setVisibility(View.VISIBLE);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            pb.setVisibility(View.INVISIBLE);
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(SignInActivity.this, "Registered", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignInActivity.this, Categories.class));
                        }
                        else{
                            pb.setVisibility(View.INVISIBLE);
                            Toast.makeText(SignInActivity.this, "Failed, Try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(SignInActivity.this, "Please Register", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            pb.setVisibility(View.INVISIBLE);
            Toast.makeText(SignInActivity.this, "Code Sent", Toast.LENGTH_SHORT).show();
            CodeSent = s;
        }
    };

}
