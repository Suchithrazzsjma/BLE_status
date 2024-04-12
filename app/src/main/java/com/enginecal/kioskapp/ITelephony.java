package com.enginecal.kioskapp;

public interface ITelephony {
    /**
     * End call if there is a call in progress, otherwise does nothing.
     *
     * @return whether it hung up
     */
    boolean endCall();

    /**
     * Silence the ringer if an incoming call is currently ringing.
     * (If vibrating, stop the vibrator also.)
     *
     * It's safe to call this if the ringer has already been silenced, or
     * even if there's no incoming call.  (If so, this method will do nothing.)
     *
     * TODO: this should be a oneway call too (see above).
     *       (Actually *all* the methods here that return void can
     *       probably be oneway.)
     */
    void silenceRinger();
}
