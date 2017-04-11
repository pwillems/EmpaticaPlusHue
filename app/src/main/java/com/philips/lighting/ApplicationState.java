package com.philips.lighting;

import android.app.Application;
import android.bluetooth.BluetoothDevice;

import com.empatica.empalink.EmpaDeviceManager;
import com.empatica.empalink.config.EmpaSensorStatus;
import com.empatica.empalink.config.EmpaSensorType;
import com.empatica.empalink.config.EmpaStatus;
import com.empatica.empalink.delegate.EmpaDataDelegate;
import com.empatica.empalink.delegate.EmpaStatusDelegate;

/**
 * Created by Pim on 10-Apr-17.
 */

public class ApplicationState extends Application implements EmpaDataDelegate, EmpaStatusDelegate {


    @Override
    public void onCreate() {
        super.onCreate();




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
    public void didUpdateStatus(EmpaStatus empaStatus) {

    }

    @Override
    public void didUpdateSensorStatus(EmpaSensorStatus empaSensorStatus, EmpaSensorType empaSensorType) {

    }

    @Override
    public void didDiscoverDevice(BluetoothDevice bluetoothDevice, String s, int i, boolean b) {

    }

    @Override
    public void didRequestEnableBluetooth() {

    }
}
