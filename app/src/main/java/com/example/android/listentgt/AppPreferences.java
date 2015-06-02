package com.example.android.listentgt;

/**
 * Created by Dylan on 5/26/2015.
 */
public class AppPreferences {
    private String name;
    private String device_name;
    public AppPreferences() {
        name = "IP Address";
        device_name= "Device Name";
    }
    public String getName(){
        return name;
    }

    public String getDeviceName(){return device_name;}
}
