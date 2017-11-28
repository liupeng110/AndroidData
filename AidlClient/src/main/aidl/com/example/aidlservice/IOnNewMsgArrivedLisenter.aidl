// IOnNewMsgArrivedLisenter.aidl
package com.example.aidlservice;
import com.example.aidlservice.NewMessage;

// Declare any non-default types here with import statements

interface IOnNewMsgArrivedLisenter {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onNewMsgArrived(in NewMessage msg);
}
