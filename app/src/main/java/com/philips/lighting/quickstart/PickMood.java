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
import android.widget.TextView;
import android.widget.Toast;

import com.empatica.empalink.ConnectionNotAllowedException;
import com.empatica.empalink.EmpaDeviceManager;
import com.empatica.empalink.config.EmpaSensorStatus;
import com.empatica.empalink.config.EmpaSensorType;
import com.empatica.empalink.config.EmpaStatus;
import com.empatica.empalink.delegate.EmpaDataDelegate;
import com.empatica.empalink.delegate.EmpaStatusDelegate;
import com.philips.lighting.R;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.toolsd.LightsController;

import java.util.ArrayList;

/**
 * A login screen that offers login via email/password.
 */
public class PickMood extends Activity implements EmpaDataDelegate, EmpaStatusDelegate {
    private PHHueSDK phHueSDK;
    public static final String TAG = "Pick-A-Mood";

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = 1;

    private static final long STREAMING_TIME = 250000; // Stops streaming 10 seconds after connection

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

    public Boolean currentlyNegative = false;
    public Boolean manualPeak = false;

    public Double valence = 0.0;
    public Double valenceThreshold = -0.40;

    public Boolean edaTempBool = false;
    public ArrayList<Float> edaMeasures = new ArrayList<Float>();
    public Float edaMean = 0.0f;
    public ArrayList<Float> edaTempMeasures = new ArrayList<Float>();
    public Integer threshold = 3;
    public Double standardDev = 0.0;
    public Long startTime = System.currentTimeMillis();
    public Long timer = System.currentTimeMillis();
    public Integer gsrSignalTimer = 2500;
    public Integer durationTimer = 60000;
    public Long timerDelay = System.currentTimeMillis() + 10000;

    public Integer lightState;
    public Integer lightStateSwitch;

    private TextView statusLabel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pick_mood);
        phHueSDK = PHHueSDK.create();

        // Set the lights to neutral state
        lightState = 0;
        lightStateSwitch = 0;
        LightsController.changeLights(24000, 150, 150, null);

        statusLabel = (TextView) findViewById(R.id.statusText);

        // PHBridge bridge = phHueSDK.getSelectedBridge();
        // List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        Log.w(TAG, "Pick a Mood Activity opened");

        /* Set the onClick for the E4 button */
        Button e4Button;
        e4Button = (Button) findViewById(R.id.e4Button);
        e4Button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                initEmpaticaDeviceManager();
            }

        });

        final LinearLayout pickAmood = (LinearLayout) findViewById(R.id.layout_pickAmood);
        final Button showPickAMood = (Button) findViewById(R.id.btn_ShowPick);
        final Button hidePickAMood = (Button) findViewById(R.id.btn_HidePick);

        showPickAMood.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showPickAMood();
            }

        });
        hidePickAMood.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                hidePickAMood();
            }

        });

        Button manualPeakButton;
        Button setStateNeutral;
        Button setStateCozy;
        Button setStateConcentration;
        manualPeakButton = (Button) findViewById(R.id.btn_manualpeak);
        setStateNeutral = (Button) findViewById(R.id.btn_neutral);
        setStateCozy = (Button) findViewById(R.id.btn_cozy);
        setStateConcentration = (Button) findViewById(R.id.btn_concentration);

        manualPeakButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                manualPeak = true;
            }
        });

        setStateNeutral.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "Neutral Clicked");
                lightState = 0;
            }
        });

        setStateCozy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "Cozy Clicked");
                lightState = 1;
            }
        });

        setStateConcentration.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "Concentration Clicked");
                lightState = 2;
            }
        });

        // Initiate all the buttons and use them to change the valence rating
        ImageButton tenseButton;
        tenseButton = (ImageButton) findViewById(R.id.imageButton1);

        tenseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tense = true;
                valence = valence - 0.25;
                Log.w(TAG, "Tense Clicked" + Double.toString(valence));
                hidePickAMood();
            }
        });

        ImageButton irritatedButton;
        irritatedButton = (ImageButton) findViewById(R.id.imageButton2);

        irritatedButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                irritated = true;
                valence = valence - 0.5;
                Log.w(TAG, "Irritated Clicked" + Double.toString(valence));
                hidePickAMood();
            }
        });

        ImageButton cheerfulButton;
        cheerfulButton = (ImageButton) findViewById(R.id.imageButton3);

        cheerfulButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cheerful = true;
                valence = valence + 0.5;
                Log.w(TAG, "cheerful Clicked" + Double.toString(valence));
                hidePickAMood();
            }
        });

        ImageButton exitedButton;
        exitedButton = (ImageButton) findViewById(R.id.imageButton4);

        exitedButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                exited = true;
                valence = valence + 0.25;
                Log.w(TAG, "exited Clicked" + Double.toString(valence));
                hidePickAMood();
            }
        });

        ImageButton boredButton;
        boredButton = (ImageButton) findViewById(R.id.imageButton5);

        boredButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                bored = true;
                valence = valence - 0.25;
                Log.w(TAG, "bored Clicked" + Double.toString(valence));
                hidePickAMood();
            }
        });

        ImageButton gloomyButton;
        gloomyButton = (ImageButton) findViewById(R.id.imageButton6);

        gloomyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                gloomy = true;
                Log.w(TAG, "gloomy Clicked" + Double.toString(valence));
                valence = valence - 0.5;
                hidePickAMood();
            }
        });

        ImageButton calmButton;
        calmButton = (ImageButton) findViewById(R.id.imageButton7);

        calmButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calm = true;
                Log.w(TAG, "calm Clicked" + Double.toString(valence));
                valence = valence + 0.25;
                hidePickAMood();
            }
        });

        ImageButton relaxedButton;
        relaxedButton = (ImageButton) findViewById(R.id.imageButton8);

        relaxedButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                relaxed = true;
                Log.w(TAG, "relaxed Clicked" + Double.toString(valence));
                valence = valence + 0.5;
                hidePickAMood();
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

    private void showPickAMood() {

        final LinearLayout pickAmood = (LinearLayout) findViewById(R.id.layout_pickAmood);
        final Button showPickAMood = (Button) findViewById(R.id.btn_ShowPick);
        final Button hidePickAMood = (Button) findViewById(R.id.btn_HidePick);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pickAmood.setVisibility(LinearLayout.VISIBLE);
                showPickAMood.setVisibility(Button.GONE);
                hidePickAMood.setVisibility(Button.VISIBLE);
            }
        });
    }

    private void hidePickAMood() {
        final LinearLayout pickAmood = (LinearLayout) findViewById(R.id.layout_pickAmood);
        final Button showPickAMood = (Button) findViewById(R.id.btn_ShowPick);
        final Button hidePickAMood = (Button) findViewById(R.id.btn_HidePick);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pickAmood.setVisibility(LinearLayout.GONE);
                showPickAMood.setVisibility(Button.VISIBLE);
                hidePickAMood.setVisibility(Button.GONE);
            }
        });
    }

    private void initEmpaticaDeviceManager() {
        // Android 6 (API level 23) now require ACCESS_COARSE_LOCATION permission to use BLE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Test", "Test");
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, REQUEST_PERMISSION_ACCESS_COARSE_LOCATION);
        } else {
            // Create a new EmpaDeviceManager. MainActivity is both its data and status delegate.
            deviceManager = new EmpaDeviceManager(getApplicationContext(), this, this);
            Log.i("E4","New EmpaDeviceManager");

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
    public void didReceiveGSR(float gsr, double timestamp) {

        // This is the location for the GSR data which is needed to check for arousal
        if(edaTempBool == false){
            startTime = System.currentTimeMillis();
            edaTempBool = true;
        }

        // Check if the second (gsrSignalTimer) has passed to calculate new Mean and SD
        if(System.currentTimeMillis() > startTime + gsrSignalTimer && System.currentTimeMillis() > timerDelay){

            // First get the temp GSR measurement, so that it only have one GSR (mean) value per second
            Float sum = 0.0f;
            for(int i = 0; i < edaTempMeasures.size(); i++){
                sum = sum + edaTempMeasures.get(i);
            }
            Float tempGSR = sum / edaTempMeasures.size();
            edaMeasures.add(tempGSR);
            edaTempMeasures.clear();

            // After the GSR is updated, also update the mean GSR value
            sum = 0.0f;
            for(int i = 0; i < edaMeasures.size(); i++){
                sum = sum + edaMeasures.get(i);
            }
            edaMean = sum / edaMeasures.size();

            // Use the mean to calculate the Standard Deviation
            sum = 0.0f;
            for(int i = 0;i<edaMeasures.size();i++){
                sum = sum + (edaMeasures.get(i) - edaMean) * (edaMeasures.get(i) - edaMean);
            }
            float squaredDiffMean = (sum) / (edaMeasures.size());
            standardDev = (Math.sqrt(squaredDiffMean));

            // Check if the current GSR value is bigger than the mean + SD value
            Log.d("E4 GSR mean", "GSR Mean + SD Measure added: " + Float.toString(edaMean) + " " + Double.toString(standardDev));

            if(tempGSR > edaMean + (threshold * standardDev) || tempGSR < edaMean - (threshold * standardDev) || manualPeak == true){
                // There is a peak detected! Enable the Pick-A-Mood!
                Log.d("E4 GSR", "Peak Detected! GSR is " + Float.toString(tempGSR));

                showPickAMood();
                if(manualPeak = true) {
                    manualPeak = false;
                }
            }
            else {
                Log.d("E4 GSR", "No peak detected, ah");
            }
            startTime = System.currentTimeMillis();
        }
        else{
            edaTempMeasures.add(gsr);
            // Log.d("E4 GSR temp", "GSR Measure added");
        }

        // Check if the time has passed to prompt Pick-A-Mood
        if(System.currentTimeMillis() > timer + durationTimer){
            Log.d("E4 Timer", "Timer done, prompt Pick-A-Mood");

            showPickAMood();

            timer = System.currentTimeMillis();
        }

        // If the valence is too low, try to improve the mood
        if(valence < valenceThreshold){
            if(currentlyNegative == false) {
                LightsController.changeLights(29000, 150, 150, null);
                Log.w(TAG, "Valence has become to low, changing lights");
                currentlyNegative = true;
            }
        }
        else {
            if(currentlyNegative == true){
                Log.w(TAG, "Valence is normal");
                lightStateSwitch = 4;
            }
            currentlyNegative = false;
            // Set the old lightstate back on
        }

        if(lightState == 0 && !lightState.equals(lightStateSwitch)){
            // Neutral Light State needs to be set active
            LightsController.changeLights(24000, 150, 150, null);
            Log.i("Hue", "Lightstate updated " + lightState + lightStateSwitch);
            lightStateSwitch = lightState;
        }
        if(lightState == 1 && !lightState.equals(lightStateSwitch)){
            // Coze Light State needs to be set active
            LightsController.changeLights(0, 100, 100, null);
            Log.i("Hue", "Lightstate updated " + lightState + lightStateSwitch);
            lightStateSwitch = lightState;
        }
        if(lightState == 2 && !lightState.equals(lightStateSwitch)){
            // Concentration Light State needs to be set active
            LightsController.changeLights(52000,240, 240, null);
            Log.i("Hue", "Lightstate updated " + lightState + lightStateSwitch);
            lightStateSwitch = lightState;
        }

    }

    @Override
    public void didReceiveBVP(float bvp, double timestamp) {

    }

    @Override
    public void didReceiveIBI(float ibi, double timestamp) {

    }

    @Override
    public void didReceiveTemperature(float temp, double timestamp) {

    }

    @Override
    public void didReceiveAcceleration(int x, int y, int z, double timestamp) {

    }

    @Override
    public void didReceiveBatteryLevel(float battery, double timestamp) {

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
    protected void onPause() {
        super.onPause();
        if (deviceManager != null) {
            deviceManager.stopScanning();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (deviceManager != null) {
            deviceManager.cleanUp();
        }
    }

    @Override
    public void didDiscoverDevice(BluetoothDevice bluetoothDevice, String deviceName, int rssi, boolean allowed) {
        // Check if the discovered device can be used with your API key. If allowed is always false,
        // the device is not linked with your API key. Please check your developer area at
        // https://www.empatica.com/connect/developer.php
        if (allowed) {
            // Stop scanning. The first allowed device will do.
            deviceManager.stopScanning();
            try {
                // Connect to the device
                deviceManager.connectDevice(bluetoothDevice);
                // updateLabel(deviceNameLabel, "To: " + deviceName);
            } catch (ConnectionNotAllowedException e) {
                // This should happen only if you try to connect when allowed == false.
                Log.i("E4", "Toast error, allowed is off!");
                // Toast.makeText(MainActivity.this, "Sorry, you can't connect to this device", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void didRequestEnableBluetooth() {
        // Request the user to enable Bluetooth
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // The user chose not to enable Bluetooth
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            // You should deal with this
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
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

