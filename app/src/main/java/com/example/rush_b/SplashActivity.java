package com.example.rush_b;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {
    public static final String SHARED_PREFERENCES_NAME = "com.example.rush_b_shared_preferences";
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        timer = new CountDownTimer(2000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                SharedPreferences sp = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                if (sp.contains(MainViewActivity.EXTRA_USERNAME)) {
                    Intent intent = new Intent(getApplicationContext(), MainViewActivity.class);
                    intent.putExtra(MainViewActivity.EXTRA_USERNAME,
                            sp.getString(MainViewActivity.EXTRA_USERNAME, ""));
                    intent.putExtra(MainViewActivity.EXTRA_NAME,
                            sp.getString(MainViewActivity.EXTRA_NAME, ""));
                    intent.putExtra(MainViewActivity.EXTRA_TIME_SPENT,
                            sp.getInt(MainViewActivity.EXTRA_TIME_SPENT, 0));
                    intent.putExtra(MainViewActivity.EXTRA_BOMB_DEFUSED,
                            sp.getInt(MainViewActivity.EXTRA_BOMB_DEFUSED, 0));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), FirstLandingActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.start();
    }
}
