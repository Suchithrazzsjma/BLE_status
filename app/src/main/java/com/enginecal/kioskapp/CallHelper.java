package com.enginecal.kioskapp;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

public class CallHelper {
    private Context context;
    private TelephonyManager telephonyManager;
    private Method endCallMethod;

    public CallHelper(Context context) {
        this.context = context;
        this.telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try {
            // Get the end call method from the TelephonyManager class
            endCallMethod = telephonyManager.getClass().getDeclaredMethod("endCall");
            endCallMethod.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void endCall() {
        try {
            if (endCallMethod != null) {
                // Invoke the end call method using reflection
                endCallMethod.invoke(telephonyManager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
