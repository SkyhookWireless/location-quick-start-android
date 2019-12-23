package com.skyhook.locationquickstart;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.skyhookwireless.wps.IWPS;
import com.skyhookwireless.wps.WPSContinuation;
import com.skyhookwireless.wps.WPSLocation;
import com.skyhookwireless.wps.WPSLocationCallback;
import com.skyhookwireless.wps.WPSReturnCode;
import com.skyhookwireless.wps.WPSStreetAddressLookup;
import com.skyhookwireless.wps.XPS;

import java.util.Locale;

public class MainActivity
    extends AppCompatActivity
{
    private TextView tv;
    private ProgressBar progress;
    private IWPS xps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(String.format(
            Locale.ROOT,
            "%s (XPS v%s)",
            getString(R.string.app_name),
            XPS.getVersion()));

        tv = findViewById(R.id.tv);
        progress = findViewById(R.id.progress);

        xps = new XPS(this);

        try {
            xps.setKey("YOUR KEY");
        } catch (IllegalArgumentException e) {
            tv.setText("Put your API key in the source code");
        }

        ActivityCompat.requestPermissions(
            this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 0);
    }

    public void onClick(View view) {
        determineLocation();
    }

    private void determineLocation() {
        if (! hasLocationPermission()) {
            tv.setText("Permission denied");
            return;
        }

        tv.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);

        xps.getLocation(
            null,
            WPSStreetAddressLookup.WPS_FULL_STREET_ADDRESS_LOOKUP,
            true,
            new WPSLocationCallback() {
                @Override
                public void handleWPSLocation(final WPSLocation location) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(String.format(
                                Locale.ROOT,
                                "%.7f %.7f +/-%dm\n\n%s\n\n%s",
                                location.getLatitude(),
                                location.getLongitude(),
                                location.getHPE(),
                                location.hasTimeZone() ? location.getTimeZone() : "No timezone",
                                location.hasStreetAddress() ? location.getStreetAddress() : "No address"));
                        }
                    });
                }

                @Override
                public void done() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setVisibility(View.VISIBLE);
                            progress.setVisibility(View.INVISIBLE);
                        }
                    });
                }

                @Override
                public WPSContinuation handleError(final WPSReturnCode returnCode) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(returnCode.toString());
                        }
                    });

                    return WPSContinuation.WPS_CONTINUE;
                }
            });
    }

    private boolean hasLocationPermission() {
        return checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED;
    }
}
