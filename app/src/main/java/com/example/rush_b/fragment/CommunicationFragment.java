package com.example.rush_b.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.rush_b.MainViewActivity;
import com.example.rush_b.R;
import com.example.rush_b.User;
import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class CommunicationFragment extends Fragment {
    private LinearLayout layout;
    private ImageView sendButton;
    private EditText messageArea;
    private ScrollView scrollView;
    private Firebase ref1;
    private static User user;

    public CommunicationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUser= database.getReference("user");
        myUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                user = dataSnapshot.getValue(User.class);

            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_communication, container, false);
        layout = (LinearLayout) view.findViewById(R.id.layout1);
        sendButton = (ImageView) view.findViewById(R.id.send_button);
        messageArea = (EditText) view.findViewById(R.id.messageArea);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        Firebase.setAndroidContext(getActivity().getApplicationContext());
        ref1 = new Firebase("https://rush-b.firebaseio.com/");
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageArea.getText().toString();
                if (!message.equals("")) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", message);
                    ref1.child("user")
                            .child(getActivity()
                                    .getIntent()
                                    .getStringExtra(MainViewActivity.EXTRA_USERNAME))
                            .child("message").setValue(map);
                }
            }
        });
        ref1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = "";
                if (map.get("message") != null) {
                    message = map.get("message").toString();
                }
                addMessageBox("You:\n" + message, 1);
            }

            @Override
            public void onChildChanged(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.firebase.client.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return view;
    }

    public void addMessageBox(String message, int type) {
        TextView textView = new TextView(getActivity().getApplicationContext());
        textView.setText(message);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 10);
        textView.setLayoutParams(lp);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}
