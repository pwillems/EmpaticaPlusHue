package com.philips.lighting.quickstart;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.empatica.empalink.EmpaDeviceManager;
import com.empatica.empalink.config.EmpaSensorStatus;
import com.empatica.empalink.config.EmpaSensorType;
import com.empatica.empalink.config.EmpaStatus;
import com.empatica.empalink.delegate.EmpaDataDelegate;
import com.empatica.empalink.delegate.EmpaStatusDelegate;
import com.philips.lighting.R;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.philips.lighting.toolsd.LightsController;

import java.util.ArrayList;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class PickMood extends Activity implements EmpaDataDelegate, EmpaStatusDelegate {
    private PHHueSDK phHueSDK;
    public static final String TAG = "Pick-A-Mood";

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = 1;

    private static final long STREAMING_TIME = 100000; // Stops streaming 10 seconds after connection

    private static final String EMPATICA_API_KEY = "4f6e0427bda8425d9f31fc9b02874af9"; // TODO insert your API Key here

    private EmpaDeviceManager deviceManager = null;

    public Boolean tense = false;
    public Boolean irritated = false;
    public Boolean cheerful = false;
    public Boolean exited = false;
    public Boolean bored = false;
    public Boolean gloomy = false;
    public Boolean calm = false;
    public Boolean relaxed = false;

    public List edaMeasures = new ArrayList();
    public Double edaMean = 0.00;
    public Integer threshhold = 1;
    public Double standardDev = 0.00;
    public Long startTime = System.currentTimeMillis();
    public Long timer = System.currentTimeMillis();
    public Integer durationTimer = 60000;

    private TextView statusLabel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pick_mood);
        phHueSDK = PHHueSDK.create();

        statusLabel = (TextView) findViewById(R.id.statusText);

        // PHBridge bridge = phHueSDK.getSelectedBridge();
        // List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        Log.w(TAG, "Pick a Mood Activity opened");

        final LinearLayout pickAmood = (LinearLayout) findViewById(R.id.layout_pickAmood);

        /* Set the onClick for the E4 button */
        Button e4Button;
        e4Button = (Button) findViewById(R.id.e4Button);
        e4Button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                initEmpaticaDeviceManager();
            }

        });

        final Button showPickAMood;
        final Button hidePickAMood;
        hidePickAMood = (Button) findViewById(R.id.btn_HidePick);
        showPickAMood = (Button) findViewById(R.id.btn_ShowPick);
        showPickAMood.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                pickAmood.setVisibility(LinearLayout.VISIBLE);
                showPickAMood.setVisibility(Button.GONE);
                hidePickAMood.setVisibility(Button.VISIBLE);
            }

        });
        hidePickAMood.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                pickAmood.setVisibility(LinearLayout.GONE);
                showPickAMood.setVisibility(Button.VISIBLE);
                hidePickAMood.setVisibility(Button.GONE);
            }

        });

        Button setStateNeutral;
        Button setStateCozy;
        Button setStateConcentration;
        setStateNeutral = (Button) findViewById(R.id.btn_neutral);
        setStateCozy = (Button) findViewById(R.id.btn_cozy);
        setStateConcentration = (Button) findViewById(R.id.btn_concentration);

        setStateNeutral.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "Neutral Clicked");
                LightsController.changeLights(12000, 200, 200, null);
            }
        });

        setStateCozy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "Neutral Clicked");
                LightsController.changeLights(22000, 200, 200, null);
            }
        });

        setStateConcentration.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "Neutral Clicked");
                LightsController.changeLights(32000, 200, 200, null);
            }
        });

        ImageButton tenseButton;
        tenseButton = (ImageButton) findViewById(R.id.imageButton1);

        tenseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tense = true;
                Log.w(TAG, "Tense Clicked");
                LightsController.changeLights(0, 200, 200, null);
            }
        });

        ImageButton irritatedButton;
        irritatedButton = (ImageButton) findViewById(R.id.imageButton2);

        irritatedButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                irritated = true;
                Log.w(TAG, "Irritated Clicked");
                LightsController.changeLights(15000, 200, 200, null);
            }
        });

        ImageButton cheerfulButton;
        cheerfulButton = (ImageButton) findViewById(R.id.imageButton3);

        cheerfulButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cheerful = true;
                Log.w(TAG, "cheerful Clicked");
            }
        });

        ImageButton exitedButton;
        exitedButton = (ImageButton) findViewById(R.id.imageButton4);

        exitedButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                exited = true;
                Log.w(TAG, "exited Clicked");
            }
        });

        ImageButton boredButton;
        boredButton = (ImageButton) findViewById(R.id.imageButton5);

        boredButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                bored = true;
                Log.w(TAG, "bored Clicked");
            }
        });

        ImageButton gloomyButton;
        gloomyButton = (ImageButton) findViewById(R.id.imageButton6);

        gloomyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                gloomy = true;
                Log.w(TAG, "gloomy Clicked");
            }
        });

        ImageButton calmButton;
        calmButton = (ImageButton) findViewById(R.id.imageButton7);

        calmButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calm = true;
                Log.w(TAG, "calm Clicked");
            }
        });

        ImageButton relaxedButton;
        relaxedButton = (ImageButton) findViewById(R.id.imageButton8);

        relaxedButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                relaxed = true;
                Log.w(TAG, "relaxed Clicked");
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_COARSE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                Log.i("test","getResultBoven");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, yay!
                    Log.i("test","getResult");
                    initEmpaticaDeviceManager();
                } else {
                    // Permission denied, boo!
                    final boolean needRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);
                    new AlertDialog.Builder(this)
                            .setTitle("Permission required")
                            .setMessage("Without this permission bluetooth low energy devices cannot be found, allow it in order to connect to the device.")
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // try again
                                    if (needRationale) {
                                        // the "never ask again" flash is not set, try again with permission request
                                        initEmpaticaDeviceManager();
                                    } else {
                                        // the "never ask again" flag is set so the permission requests is disabled, try open app settings to enable the permission
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    }
                                }
                            })
                            .setNegativeButton("Exit application", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // without permission exit is the only way
                                    finish();
                                }
                            })
                            .show();
                }
                break;
        }
    }

    private void initEmpaticaDeviceManager() {
        // Android 6 (API level 23) now require ACCESS_COARSE_LOCATION permission to use BLE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Test", "Test");
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, REQUEST_PERMISSION_ACCESS_COARSE_LOCATION);
        } else {
            // Create a new EmpaDeviceManager. MainActivity is both its data and status delegate.
            deviceManager = new EmpaDeviceManager(getApplicationContext(), this, this);

            if (TextUtils.isEmpty(EMPATICA_API_KEY)) {
                new AlertDialog.Builder(this)
                        .setTitle("Warning")
                        .setMessage("Please insert your API KEY")
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // without permission exit is the only way
                                finish();
                            }
                        })
                        .show();
                return;
            }
            // Initialize the Device Manager using your API key. You need to have Internet access at this point.
            deviceManager.authenticateWithAPIKey(EMPATICA_API_KEY);
        }
    }

    @Override
    public void didReceiveGSR(float v, double v1) {

    }

    @Override
    public void didReceiveBVP(float v, double v1) {

    }

    @Override
    public void didReceiveIBI(float v, double v1) {

    }

    @Override
    public void didReceiveTemperature(float v, double v1) {

    }

    @Override
    public void didReceiveAcceleration(int i, int i1, int i2, double v) {

    }

    @Override
    public void didReceiveBatteryLevel(float v, double v1) {

    }

    @Override
    public void didUpdateStatus(EmpaStatus status) {
        // Update the UI
        updateLabel(statusLabel, status.name());



        // The device manager is ready for use
        if (status == EmpaStatus.READY) {
            updateLabel(statusLabel, status.name() + " - Turn on your device");
            // Start scanning
            deviceManager.startScanning();
            Log.i(TAG, "The app is starting to scan for the E4");
            // The device manager has established a connection
        } else if (status == EmpaStatus.CONNECTED) {
            Log.i(TAG, "Empatica is Connected");
            // Stop streaming after STREAMING_TIME - Deze aanpassen om fulltime te streamen!
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Disconnect device
                            Log.i(TAG, "Klaar met de timer, nu stoppen");
                            deviceManager.disconnect();
                        }
                    }, STREAMING_TIME);
                }
            });
            // The device manager disconnected from a device
        } else if (status == EmpaStatus.DISCONNECTED) {
            Log.i(TAG, "Empatica is Disconnected");
        }
    }

    @Override
    public void didUpdateSensorStatus(EmpaSensorStatus empaSensorStatus, EmpaSensorType empaSensorType) {

    }

    @Override
    public void didDiscoverDevice(BluetoothDevice bluetoothDevice, String s, int i, boolean b) {

    }

    @Override
    public void didRequestEnableBluetooth() {
        // Request the user to enable Bluetooth
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    // Update a label with some text, making sure this is run in the UI thread
    private void updateLabel(final TextView label, final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                label.setText(text);
            }
        });
    }
}

