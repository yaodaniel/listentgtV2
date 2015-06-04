package com.example.android.listentgt;

/**
 * Created by DanielY on 6/3/2015.
 */

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.View;


/**
 * An interface-callback for the activity to listen to fragment interaction
 * events.
 */
public interface DeviceActionListener {

    void showDetails(WifiP2pDevice device);
    void connect(WifiP2pConfig config);
    void disconnect();
    void createGroup(View view);

    /**
     * An interface-callback for the activity to listen to fragment interaction
     * events.
     */
    /*public interface DeviceActionListener {

        void showDetails(WifiP2pDevice device);

        //        void cancelDisconnect();
//
        void connect(WifiP2pConfig config);
//
//        void disconnect();

//        void playMusic();

        void stopMusic();

        void clientPlayMusic(String URL);

        void clientContinueMusic();
    }*/
}