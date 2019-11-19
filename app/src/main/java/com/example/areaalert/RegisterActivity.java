package com.example.areaalert;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String email,password1,password2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        Button button = (Button) findViewById(R.id.RegisterBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register(v);
            }
        });
    }

    void Register(View view) {
        EditText et = (EditText) findViewById(R.id.RegisterEmail);
        email = et.getText().toString();
        et = (EditText) findViewById(R.id.RegisterPass1);
        password1 = et.getText().toString();
        et = (EditText) findViewById(R.id.RegisterPass2);
        password2 = et.getText().toString();
        if (!password1.equals(password2)){
            Toast.makeText(RegisterActivity.this,"Passwords don't match",Toast.LENGTH_LONG).show();
        }
        else if(email == null ||password1 == null ||password2 == null){
            Toast.makeText(RegisterActivity.this,"Fields should not be empty",Toast.LENGTH_LONG).show();
        }
        else {
            mAuth.createUserWithEmailAndPassword(email, password1)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Tag", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                startActivity(new Intent(RegisterActivity.this,Categories.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Tag", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}