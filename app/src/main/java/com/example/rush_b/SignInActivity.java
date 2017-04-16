package com.example.rush_b;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class SignInActivity extends AppCompatActivity {
    private String email;
    private EditText etEmail;
    private EditText etPassword;
    private TextView etStatusTextView;
    private TextView tvGoToSignUp;
    private User user;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static final String TAG = "FirebaseAuthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        etEmail = (EditText) findViewById(R.id.email_edit_text);
        etPassword = (EditText) findViewById(R.id.password_edit_text);
        etStatusTextView = (TextView) findViewById(R.id.status_text_view);
        tvGoToSignUp = (TextView) findViewById(R.id.go_to_sign_up_textview);

        etStatusTextView.setText("");

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
            }
        };

        Button submitButton = (Button) findViewById(R.id.button_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString().trim();
                String username = usernameFromEmail(email);
                String password = etPassword.getText().toString().trim();
                if (!email.isEmpty() && !password.isEmpty()) {
                    signIn(email, username, password);
                } else {
                    if (email.isEmpty()) {
                        etEmail.setError(getString(R.string.email_empty_error));
                    }
                    if (password.isEmpty()) {
                        etPassword.setError(getString(R.string.password_empty_error));
                    }
                }
            }
        });

        tvGoToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // [START on_start_add_listener]
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    // [END on_stop_remove_listener]

    private void signIn(final String email, final String username, String password) {
        final String[] emailCheck = new String[1];
        Log.d(TAG, "signIn:" + email);

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(SignInActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                            etStatusTextView.setText(R.string.auth_failed);
                        }
                        else {
                            final FirebaseDatabase database = FirebaseDatabase.getInstance();
                            final DatabaseReference myUser = database.getReference("user/"+username);
                            myUser.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    user = dataSnapshot.getValue(User.class);
                                        SharedPreferences sp = getSharedPreferences(
                                                SplashActivity.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putString(MainViewActivity.EXTRA_USERNAME, user.username);
                                        editor.putString(MainViewActivity.EXTRA_NAME, user.name);
                                        editor.putInt(MainViewActivity.EXTRA_BOMB_DEFUSED, user.bombDefused);
                                        editor.putInt(MainViewActivity.EXTRA_TIME_SPENT, user.timeSpent);
                                        editor.putBoolean(MainViewActivity.EXTRA_STATUS, user.status);
                                        editor.apply();
                                        Intent intent = new Intent(getApplicationContext(), MainViewActivity.class);
                                        intent.putExtra(MainViewActivity.EXTRA_USERNAME, user.username);
                                        intent.putExtra(MainViewActivity.EXTRA_NAME, user.name);
                                        intent.putExtra(MainViewActivity.EXTRA_BOMB_DEFUSED, user.bombDefused);
                                        intent.putExtra(MainViewActivity.EXTRA_TIME_SPENT, user.timeSpent);
                                        intent.putExtra(MainViewActivity.EXTRA_STATUS, user.status);
                                        startActivity(intent);
                                        finish();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            Toast.makeText(SignInActivity.this, "Logged in successfully",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem items) {
        switch(items.getItemId()) {
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
