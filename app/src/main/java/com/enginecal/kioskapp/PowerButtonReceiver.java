package com.enginecal.kioskapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class PowerButtonReceiver extends BroadcastReceiver {
    Handler handler;
    MainActivity mainActivity;

    public PowerButtonReceiver(Handler handler,MainActivity mainActivity){
        this.handler = handler;
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("powerIntent",intent.getAction()+"");
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case Intent.ACTION_SCREEN_ON:
                    // Screen turned on
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(context,"ON",Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    // Screen turned off
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //mainActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            mainActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                            mainActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
                            //mainActivity.getWindow().addFlags(WindowManager.LayoutParams.SCREEN);

                            Toast.makeText(context,"OFF",Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
                case Intent.ACTION_USER_PRESENT:
                    // Device unlocked
                    Toast.makeText(context,"LOCK",Toast.LENGTH_SHORT).show();
                    break;
                case Intent.ACTION_USER_UNLOCKED:
                    // Device unlocked
                    Toast.makeText(context,"UNLOCK",Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    }
}
