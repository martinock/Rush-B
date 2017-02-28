package com.example.rush_b;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.example.rush_b.fragment.StatisticFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by martinock on 28/02/17.
 */
public class FetchAddressIntentService extends IntentService {
    private static final String TAG = "FetchAddressIntentService";
    private static List<Address> addresses;
    protected ResultReceiver mReceiver;

    public FetchAddressIntentService() {
        super(TAG);
    }
    public FetchAddressIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses == null || addresses.size() == 0) {
            deliverResultToReceiver(Constants.FAILURE_RESULT, "Sorry address can't be found");
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragment = new ArrayList<>();
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragment.add(address.getAddressLine(i));
            }
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragment));
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }
}
