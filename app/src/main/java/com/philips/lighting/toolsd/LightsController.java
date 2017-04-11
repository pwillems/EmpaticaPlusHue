package com.philips.lighting.toolsd;

import android.util.Log;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.List;
import java.util.Map;

/**
 * Created by Pim on 10-Apr-17.
 */

public class LightsController {
    
    public static void changeLights(int hue, int sat, int bri, PHLightListener listener) {

        PHHueSDK phHueSDK = PHHueSDK.create();
        PHBridge bridge = phHueSDK.getSelectedBridge();
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        PHLightState lightState = new PHLightState();
        lightState.setHue(hue);
        lightState.setSaturation(sat);
        lightState.setBrightness(bri);
        // To validate your lightstate is valid (before sending to the bridge) you can use:
        // String validState = lightState.validateState();
        for (PHLight light : allLights) {
            bridge.updateLightState(light, lightState, listener);
        }

    }

}
