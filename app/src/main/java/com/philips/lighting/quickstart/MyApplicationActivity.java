package com.philips.lighting.quickstart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.philips.lighting.R;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

/**
 * MyApplicationActivity - The starting point for creating your own Hue App.  
 * Currently contains a simple view with a button to change your lights to random colours.  Remove this and add your own app implementation here! Have fun!
 * 
 * @author SteveyO
 *
 */
public class MyApplicationActivity extends Activity {
    private PHHueSDK phHueSDK;
    private static final int MAX_HUE= 65535;
    public static final String TAG = "QuickStart";

    private SeekBar seekHue;
    private SeekBar seekSat;
    private SeekBar seekBri;
    private int random;

    public boolean tense = false;
    public boolean irritated = false;
    public boolean cheerful = false;
    public boolean exited = false;
    public boolean bored = false;
    public boolean gloomy = false;
    public boolean calm = false;
    public boolean relaxed = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);
        phHueSDK = PHHueSDK.create();

        seekHue = (SeekBar) findViewById(R.id.seekBarHue);
        seekBri = (SeekBar) findViewById(R.id.seekBarBri);
        seekSat = (SeekBar) findViewById(R.id.seekBarSat);

        seekHue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                lightsChange();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBri.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                lightsChange();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekSat.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                lightsChange();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /* Set the onClick for the random Button */
        Button randomButton;
        randomButton = (Button) findViewById(R.id.buttonRand);
        randomButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                randomLights();
            }

        });

        /* Set the onClick for the red button */
        Button redButton;
        redButton = (Button) findViewById(R.id.buttonRed);
        redButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                redLights();
            }

        });

        /* Set the onClick for the blue button */
        Button blueButton;
        blueButton = (Button) findViewById(R.id.buttonBlue);
        blueButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                blueLights();
            }

        });

        /* Set the onClick for the green button */
        Button greenButton;
        greenButton = (Button) findViewById(R.id.buttonGreen);
        greenButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                greenLights();
            }

        });

        /* Set the onClick for the change three aspects button */
        Button changeButton;
        changeButton = (Button) findViewById(R.id.buttonChange);
        changeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                lightsChange();
            }

        });

        Button btn = (Button)findViewById(R.id.open_activity_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyApplicationActivity.this, PickMood.class));
            }
        });
    }

    public void randomLights() {
        PHBridge bridge = phHueSDK.getSelectedBridge();

        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        Random rand = new Random();

        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            random = rand.nextInt(MAX_HUE);
            lightState.setHue(random);
            // To validate your lightstate is valid (before sending to the bridge) you can use:  
            // String validState = lightState.validateState();
            bridge.updateLightState(light, lightState, listener);
            //  bridge.updateLightState(light, lightState);   // If no bridge response is required then use this simpler form.
        }
        seekHue.setProgress(random);
    }

    public void lightsChange() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        int hue = seekHue.getProgress();
        int sat = seekSat.getProgress();
        int bri = seekBri.getProgress();

        if (bri == 0) {
            bri = 1;
        }

        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setHue(hue);
            lightState.setBrightness(bri);
            lightState.setSaturation(sat);
            bridge.updateLightState(light, lightState, listener);
        }
    }

    public void redLights() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setHue(65535);
            bridge.updateLightState(light, lightState, listener);
        }
        seekHue.setProgress(65535);
    }

    public void blueLights() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setHue(46920);
            bridge.updateLightState(light, lightState, listener);
        }
        seekHue.setProgress(46920);
    }

    public void greenLights() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setHue(25500);
            bridge.updateLightState(light, lightState, listener);
        }
        seekHue.setProgress(25500);
    }

    // If you want to handle the response from the bridge, create a PHLightListener object.
    PHLightListener listener = new PHLightListener() {
        
        @Override
        public void onSuccess() {  
        }
        
        @Override
        public void onStateUpdate(Map<String, String> arg0, List<PHHueError> arg1) {
           Log.w(TAG, "Light has updated");
        }
        
        @Override
        public void onError(int arg0, String arg1) {}

        @Override
        public void onReceivingLightDetails(PHLight arg0) {}

        @Override
        public void onReceivingLights(List<PHBridgeResource> arg0) {}

        @Override
        public void onSearchComplete() {}
    };
    
    @Override
    protected void onDestroy() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        if (bridge != null) {
            
            if (phHueSDK.isHeartbeatEnabled(bridge)) {
                phHueSDK.disableHeartbeat(bridge);
            }
            
            phHueSDK.disconnect(bridge);
            super.onDestroy();
        }
    }
}
