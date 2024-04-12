package com.enginecal.kioskapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

public class MyPhoneStateListener extends Service {

    private Method endCallMethod;
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        StateListener phoneStateListener = new StateListener();
        TelephonyManager telephonymanager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        telephonymanager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    class StateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch(state){
                case TelephonyManager.CALL_STATE_RINGING:
                    //Disconnect the call here...
                    Toast.makeText(getApplicationContext(),"Calling",Toast.LENGTH_SHORT).show();
                    endCall();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
            }
        }
    };

    public void endCall(){
        try{
            Toast.makeText(getApplicationContext(),"Calling11",Toast.LENGTH_SHORT).show();
            TelephonyManager manager = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            Class c = Class.forName(manager.getClass().getName());
            Toast.makeText(getApplicationContext(),"Calling21",Toast.LENGTH_SHORT).show();
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            Toast.makeText(getApplicationContext(),"Calling41",Toast.LENGTH_SHORT).show();
            ITelephony telephony = (ITelephony)m.invoke(manager);
            assert telephony != null;
            telephony.endCall();
        } catch(Exception e){
            Log.d("",e.getMessage());
        }

       /* try{
            Toast.makeText(getApplicationContext(),"Calling11",Toast.LENGTH_SHORT).show();
            TelephonyManager telephonyManager = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                Toast.makeText(getApplicationContext(),"Calling21",Toast.LENGTH_SHORT).show();
                try {
                    Toast.makeText(getApplicationContext(),"Calling31",Toast.LENGTH_SHORT).show();
                    endCallMethod = telephonyManager.getClass().getDeclaredMethod("endCall");
                    endCallMethod.setAccessible(true);
                    Toast.makeText(getApplicationContext(),"Calling41",Toast.LENGTH_SHORT).show();
                    //if (endCallMethod != null) {
                        Toast.makeText(getApplicationContext(),"Calling51",Toast.LENGTH_SHORT).show();
                        // Invoke the end call method using reflection
                        endCallMethod.invoke(telephonyManager);
                    //}
                    Toast.makeText(getApplicationContext(),"Calling61",Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }



                *//*Class c = Class.forName(telephonyManager.getClass().getName());
                Method m = c.getDeclaredMethod("getITelephony");
                m.setAccessible(true);
                Toast.makeText(context,"Calling3",Toast.LENGTH_SHORT).show();
                ITelephony telephony = (ITelephony)m.invoke(telephonyManager);
                assert telephony != null;
                telephony.endCall();*//*
                //Toast.makeText(getApplicationContext(),"Calling4",Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(getApplicationContext(),"Calling5",Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDestroy() {

    }
}
