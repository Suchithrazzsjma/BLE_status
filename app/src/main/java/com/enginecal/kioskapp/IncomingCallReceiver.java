package com.enginecal.kioskapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.lang.reflect.Method;

public class IncomingCallReceiver extends BroadcastReceiver {
    private Method endCallMethod;

    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (state != null && state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            // Reject incoming call
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if(MainActivity.mIsKioskEnabled) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                        Toast.makeText(context, "Call Receive and Disconnected", Toast.LENGTH_SHORT).show();
                        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                        if (telecomManager != null) {
                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            //telecomManager.silenceRinger();
                            telecomManager.endCall();
                        }
                    } else {
//                        Toast.makeText(context, "Call Receive and Disconnected", Toast.LENGTH_SHORT).show();
                        // For older versions, you can decline the call UI using a broadcast intent
                        Intent declineCallIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                        KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
                        declineCallIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
                        context.sendOrderedBroadcast(declineCallIntent, null);
                    }
                }
            }
        }




        // Handle incoming call event
        // For example, reject the call
        /*TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            telephonyManager.endCall();
        }*/

        /*try{
            Toast.makeText(context,"Calling1",Toast.LENGTH_SHORT).show();
            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                Toast.makeText(context,"Calling2",Toast.LENGTH_SHORT).show();
                try {
                    Toast.makeText(context,"Calling3",Toast.LENGTH_SHORT).show();
                    endCallMethod = telephonyManager.getClass().getDeclaredMethod("endCall");
                    endCallMethod.setAccessible(true);
                    Toast.makeText(context,"Calling4",Toast.LENGTH_SHORT).show();
                    if (endCallMethod != null) {
                        Toast.makeText(context,"Calling5",Toast.LENGTH_SHORT).show();
                        // Invoke the end call method using reflection
                        endCallMethod.invoke(telephonyManager);
                    }
                    Toast.makeText(context,"Calling6",Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }



                *//*Class c = Class.forName(telephonyManager.getClass().getName());
                Method m = c.getDeclaredMethod("getITelephony");
                m.setAccessible(true);
                Toast.makeText(context,"Calling3",Toast.LENGTH_SHORT).show();
                ITelephony telephony = (ITelephony)m.invoke(telephonyManager);
                assert telephony != null;
                telephony.endCall();*//*
                Toast.makeText(context,"Calling4",Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(context,"Calling5",Toast.LENGTH_SHORT).show();
            *//*Class c = Class.forName(manager.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(false);
            ITelephony telephony = (ITelephony)m.invoke(manager);
            assert telephony != null;
            telephony.endCall();*//*
        } catch(Exception e){
            Log.d("",e.getMessage());
        }*/
    }
}
