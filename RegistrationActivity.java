package com.arifulislam.tourguidepro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {
    private TextView mEmailRegisterView;
    private EditText mPasswordRegisterView;
    private EditText mNameRegisterView;
    private Button btnRegister;
    private String name,email,pass;
    private FirebaseAuth mAuth;
    private UsersInformation usersInformation;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mNameRegisterView = findViewById(R.id.register_name);
        mEmailRegisterView = findViewById(R.id.register_email);
        mPasswordRegisterView = findViewById(R.id.register_password);
        btnRegister = findViewById(R.id.signup_in_button);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            String id = mUser.getUid();
            mReference = mDatabase.getReference().child("Users").child(id);
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initialization();
                if (!validation()){
                    Log.e("Entered not valid","not valid");
                    Toast.makeText(RegistrationActivity.this,"Registration Failed.Please enter information's correctly",Toast.LENGTH_LONG).show();
                }else {
                    Log.e("Entered valid"," valid");
                    attemptSignUp();
                }
            }
        });
    }

    private void initialization(){
        email = mEmailRegisterView.getText().toString().trim();
        name = mNameRegisterView.getText().toString().trim();
        pass = mPasswordRegisterView.getText().toString().trim();
        if (usersInformation != null) {
            usersInformation.setPass(pass);
            usersInformation.setName(name);
            usersInformation.setEmail(email);
            usersInformation = new UsersInformation(email, name, pass);
        }
    }

    private boolean validation(){
        boolean valid = true;
        if (email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmailRegisterView.setError(getString(R.string.error_field_required));
            valid = false;
        }
        if (name.isEmpty() && name.length()<4){
            mNameRegisterView.setError(getString(R.string.error_field_required));
            valid = false;
        }
        if (pass.isEmpty() && pass.length()<6){
            mPasswordRegisterView.setError(getString(R.string.error_field_required));
            valid = false;
        }

        return valid;
    }


    private void attemptSignUp(){

        if (mAuth != null){
            Log.e("Entered not null","not null");
            final ProgressDialog progressDialog = ProgressDialog.show(RegistrationActivity.this, "Please Wait...", "While we process your information", true);
            progressDialog.setCanceledOnTouchOutside(false);
            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                            Log.e("Entered successful","successful");
                            progressDialog.dismiss();
                            saveUsersInformation();

                    }else {

                        // If sign in fails, display a message to the user.
                        Log.w("failed", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    private void saveUsersInformation(){
        if (mReference != null) {
            Log.e("value saving","entered");
            usersInformation = new UsersInformation(name,email,pass);

            mReference.setValue(usersInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.e("user name",name);
                        Log.e("value saving","successful");
                        Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                        Intent n = new Intent(RegistrationActivity.this, LoginActivity.class);
                        n.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(n);
                    }

                }
            });
        }

    }


}
