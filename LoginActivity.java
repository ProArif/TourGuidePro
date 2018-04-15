package com.arifulislam.tourguidepro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.arifulislam.tourguidepro.currency.CurrencyConverterActivity;
import com.arifulislam.tourguidepro.events.EventActivity;
import com.arifulislam.tourguidepro.locations.MapsActivity;
import com.arifulislam.tourguidepro.locations.NearbyPlacesActivity;
import com.arifulislam.tourguidepro.weather.WeatherActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Button btnRegister,btnSignIn;
    private EditText edtEmail,edtPass;
    private String email,pass;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        edtEmail = findViewById(R.id.email);
        edtPass = findViewById(R.id.password);
        btnRegister = findViewById(R.id.register_btn);
        btnSignIn = findViewById(R.id.sign_in_button);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initialization();
                if (!validation()){
                    Toast.makeText(LoginActivity.this,"Login Failed.Please enter information's correctly",Toast.LENGTH_LONG).show();
                }else{
                    attempLogin();
                }
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent n = new Intent(LoginActivity.this,RegistrationActivity.class);
               startActivity(n);
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void attempLogin(){
        final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "Please wait", "Logging In..", true);
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    Intent n = new Intent(LoginActivity.this,MapsActivity.class);
                    n.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(n);
                    finish();
                }else {
                    progressDialog.hide();
                    if (task.getException() !=null) {
                        Log.e("Error", task.getException().toString());
                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void initialization(){
        email = edtEmail.getText().toString().trim();
        pass = edtPass.getText().toString().trim();
    }

    private boolean validation(){
        boolean valid = true;
        if (email.isEmpty() && !email.contains("@")){
            edtEmail.setError(getString(R.string.error_invalid_email));
            valid = false;
        }
        if (pass.isEmpty() && pass.length()<6){
            edtPass.setError(getString(R.string.error_field_required));
            valid = false;
        }

        return valid;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

       /* if (id == R.id.profile) {

            Intent n = new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(n);

        } else*/ if (id == R.id.nav_weather) {
            Intent n = new Intent(LoginActivity.this,WeatherActivity.class);
            startActivity(n);

        } else if (id == R.id.nav_locations) {
            Intent n = new Intent(LoginActivity.this,MapsActivity.class);
            startActivity(n);

        }else if (id == R.id.nav_nearby){
            Intent n = new Intent(LoginActivity.this, NearbyPlacesActivity.class);
            startActivity(n);

        }else if (id == R.id.nav_currency) {
            Intent n = new Intent(LoginActivity.this, CurrencyConverterActivity.class);
            startActivity(n);


        } else if (id == R.id.nav_share) {

        }else if (id == R.id.nav_event){
            Intent n = new Intent(LoginActivity.this, EventActivity.class);
            startActivity(n);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
