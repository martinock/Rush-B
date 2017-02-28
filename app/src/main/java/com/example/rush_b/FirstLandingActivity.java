package com.example.rush_b;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FirstLandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_landing);

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        Button signUpButton = (Button) findViewById(R.id.sign_up_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}
