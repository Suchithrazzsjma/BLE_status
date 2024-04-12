package com.enginecal.kioskapp;

import static com.enginecal.kioskapp.fgService.BleBatteryService.ACTION_DEVICE_NOT_FOUND;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BatteryReceiver extends BroadcastReceiver {

    MainActivity mainActivity;
    BatteryStateReceiver receiverInterfaceone;
    BTStateReceiver receiverInterfacetwo;

    public BatteryReceiver(MainActivity mainActivity, BatteryStateReceiver receiverInterface, BTStateReceiver receiverInterfaceB) {
        this.mainActivity = mainActivity;
        this.receiverInterfaceone = receiverInterface;
        this.receiverInterfacetwo = receiverInterfaceB;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (intent.getAction() == Intent.ACTION_POWER_CONNECTED) {
                receiverInterfaceone.batteryStatus(true);
            } else if (intent.getAction() == Intent.ACTION_POWER_DISCONNECTED) {
                receiverInterfaceone.batteryStatus(false);
            } else if (intent.getAction() == BluetoothDevice.ACTION_FOUND) {
                receiverInterfacetwo.BTstatus(true);
            } else if (intent.getAction() == ACTION_DEVICE_NOT_FOUND) {
                receiverInterfacetwo.BTstatus(false);
            }

        }
    }
}