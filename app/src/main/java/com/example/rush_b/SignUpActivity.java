package com.example.rush_b;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etYourName;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etEmail;
    private TextView tvGoToSignIn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
        public static final String TAG = "FirebaseAuthActivity";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        etYourName = (EditText) findViewById(R.id.your_name_edit_text);
        etPassword = (EditText) findViewById(R.id.password_edit_text);
        etConfirmPassword = (EditText) findViewById(R.id.confirm_password_edit_text);
        etEmail = (EditText) findViewById(R.id.email_edit_text);
        tvGoToSignIn = (TextView) findViewById(R.id.go_to_sign_in_textview);

        Button signUpButton = (Button) findViewById(R.id.button_sign_up);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString().trim();
                String username = usernameFromEmail(email);
                String yourname = etYourName.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                if (!email.isEmpty() && !yourname.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty() && !username.isEmpty()) {
                    if (password.equals(confirmPassword)) {
                        createAccount(username, yourname, email, password);
                    } else {
                        etPassword.setError(getString(R.string.password_confirmation_error));
                        etConfirmPassword.setError(getString(R.string.password_confirmation_error));
                    }
                } else {
                    if (email.isEmpty()) {
                        etEmail.setError(getString(R.string.email_empty_error));
                    }
                    if (yourname.isEmpty()) {
                        etEmail.setError(getString(R.string.yourname_empty_error));
                    }
                    if (password.isEmpty()) {
                        etPassword.setError(getString(R.string.password_empty_error));
                    }
                    if (confirmPassword.isEmpty()) {
                        etConfirmPassword.setError(getString(R.string.confirm_password_empty_error));
                    }
                    if (username.isEmpty()) {
                        etUsername.setError(getString(R.string.username_empty_error));
                    }
                }
            }
        });


        tvGoToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void createAccount(final String username, final String yourname, final String email, final String password) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myUser = database.getReference("user");
        Log.d(TAG, "createAccount:" + email);

//        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            User user = new User(username, yourname, email);
                            Toast.makeText(SignUpActivity.this, "Account has been made successfully", Toast.LENGTH_LONG).show();
                            myUser.child(username).setValue(user);
                            SharedPreferences sp = getSharedPreferences(
                                    SplashActivity.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(MainViewActivity.EXTRA_USERNAME, username);
                            editor.putString(MainViewActivity.EXTRA_NAME, yourname);
                            editor.putInt(MainViewActivity.EXTRA_BOMB_DEFUSED, 0);
                            editor.putInt(MainViewActivity.EXTRA_TIME_SPENT, 0);
                            editor.putBoolean(MainViewActivity.EXTRA_STATUS, false);
                            editor.apply();
                            Intent intent = new Intent(getApplicationContext(), MainViewActivity.class);
                            intent.putExtra(MainViewActivity.EXTRA_USERNAME, username);
                            intent.putExtra(MainViewActivity.EXTRA_NAME, yourname);
                            intent.putExtra(MainViewActivity.EXTRA_BOMB_DEFUSED, 0);
                            intent.putExtra(MainViewActivity.EXTRA_TIME_SPENT, 0);
                            intent.putExtra(MainViewActivity.EXTRA_STATUS, false);
                            startActivity(intent);
                            finish();
                        }

                        // [START_EXCLUDE]
//                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem items) {
        switch (items.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(items);
        }
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), FirstLandingActivity.class);
        startActivity(intent);
        finish();
    }
}
