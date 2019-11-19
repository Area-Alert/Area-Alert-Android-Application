package com.example.areaalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private final String TAG = "SignInActivity";
    String email,password;

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

        Button Register = findViewById(R.id.Register);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallRegister(v);
            }
        });

        Button Register1 = findViewById(R.id.Register1);
        Register1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallRegister1();
            }
        });
    }

    private void CallRegister1() {
        startActivity(new Intent(SignInActivity.this, RegisterActivity1.class));
    }

    public void Login(View view){
        EditText Email = findViewById(R.id.Username);
        email = Email.getText().toString();
        EditText Password = findViewById(R.id.Password);
        password = Password.getText().toString();
        if(email == null || password == null) {
            Toast.makeText(SignInActivity.this,"Enter a value in both the parameters",Toast.LENGTH_LONG).show();
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                startActivity(new Intent(SignInActivity.this,Categories.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(SignInActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }

    public void CallRegister(View view){
        startActivity(new Intent(SignInActivity.this, RegisterActivity.class));
    }

}
