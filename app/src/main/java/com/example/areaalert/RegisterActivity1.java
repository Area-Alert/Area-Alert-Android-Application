package com.example.areaalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RegisterActivity1 extends AppCompatActivity {

    private final String TAG = "RegisterActivity1";

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    EditText Phone,Code,RegisterEmail,RegisterName;
    Button Send, Verify;
    String CodeSent,phoneNumber,EmailAddress,Name;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        Phone = findViewById(R.id.PhoneNumber);
        Code = findViewById(R.id.Code);
        RegisterEmail = findViewById(R.id.RegisterEmailValue);
        RegisterName = findViewById(R.id.RegisterNameValue);

        Send = findViewById(R.id.Verify);
        Verify = findViewById(R.id.CodeVerify);

        pb = findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendVerificationCode();
            }
        });
        Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckOnVerification();
            }
        });
    }


    private void CheckOnVerification() {

        String CodeRecieved = Code.getText().toString();

        if(CodeRecieved.isEmpty()) {
            Code.setText("Code is Required");
            Code.requestFocus();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(CodeSent, CodeRecieved);
        signInWithPhoneAuthCredential(credential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(RegisterActivity1.this, "Registered", Toast.LENGTH_SHORT).show();

                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            Map<String, Object> user = new HashMap<>();
                            user.put("name", Name);
                            user.put("email", EmailAddress);
                            user.put("phone number", phoneNumber);
                            user.put("uid", currentUser.getUid());

                            db.collection("users")
                                    .document(EmailAddress)
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot added with ID: " + EmailAddress);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                        }
                                    });


                            startActivity(new Intent(RegisterActivity1.this, Categories.class));
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void SendVerificationCode() {

        phoneNumber = "+91" + Phone.getText().toString();
        EmailAddress = RegisterEmail.getText().toString();
        Name = RegisterName.getText().toString();

        if(phoneNumber.isEmpty() || EmailAddress.isEmpty() || Name.isEmpty()){
            Phone.setText("Phone Number is Required");
            if(EmailAddress.isEmpty())
                RegisterEmail.requestFocus();
            else if(Name.isEmpty())
                RegisterName.requestFocus();
            else
                Phone.requestFocus();
            return;
        }

        if (phoneNumber.length() < 10){
            Phone.setText("Please Enter a Valid Phone Number");
            Phone.requestFocus();
            return;
        }

        pb.setVisibility(View.VISIBLE);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);

            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            pb.setVisibility(View.INVISIBLE);
            Toast.makeText(RegisterActivity1.this, "Failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            pb.setVisibility(View.INVISIBLE);
            Toast.makeText(RegisterActivity1.this, "Code Sent", Toast.LENGTH_SHORT).show();
            CodeSent = s;
        }
    };

}
