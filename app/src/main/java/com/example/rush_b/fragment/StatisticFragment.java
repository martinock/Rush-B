package com.example.rush_b.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rush_b.FirstLandingActivity;
import com.example.rush_b.MainViewActivity;
import com.example.rush_b.R;
import com.example.rush_b.SplashActivity;
import com.example.rush_b.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;

public class StatisticFragment extends Fragment {

    private final String TAG = "FirebaseDb";

    public StatisticFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        TextView tvUsername = (TextView) view.findViewById(R.id.username_text_view);
        TextView tvYourName = (TextView) view.findViewById(R.id.your_name_text_view);
        TextView tvBombDefused = (TextView) view.findViewById(R.id.bomb_defused_text_view);
        TextView tvTimeSpent = (TextView) view.findViewById(R.id.time_spent_text_view);
        TextView tvLocation = (TextView) view.findViewById(R.id.location_text);
        TextView tvWeather = (TextView) view.findViewById(R.id.weather_text_view);
        tvUsername.setText("ID : " + getActivity().getIntent().getStringExtra(
                MainViewActivity.EXTRA_USERNAME));
        tvYourName.setText(getActivity().getIntent().getStringExtra(MainViewActivity.EXTRA_NAME));
        tvBombDefused.setText("Bomb defused : " + getActivity().getIntent().getIntExtra(
                MainViewActivity.EXTRA_BOMB_DEFUSED, 0));
        tvTimeSpent.setText("Time spent : " + getActivity().getIntent().getIntExtra(
                MainViewActivity.EXTRA_TIME_SPENT, 0));
        tvLocation.setText(MainViewActivity.LOCATION == null ? "" : MainViewActivity.LOCATION);
        tvWeather.setText(MainViewActivity.WEATHER == null ? "" : MainViewActivity.WEATHER);

        // Inflate the layout for this fragment
        Button signOutButton = (Button) view.findViewById(R.id.button_sign_out);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myUser = database.getReference("user");
                SharedPreferences sp = getActivity().getSharedPreferences(
                        SplashActivity.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                String username = sp.getString(MainViewActivity.EXTRA_USERNAME, "");
                myUser.child(username).child("status").setValue(false);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(getActivity(), FirstLandingActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }
}
