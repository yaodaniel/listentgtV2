package com.example.android.listentgt;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by Dylan on 5/24/2015.
 * Modified by Daniel on 5/31/2015.
 */
public class FragmentSettingsPage extends Fragment implements View.OnClickListener{

    private final String Headers[] = {"IP Address", "Device Name", "Copyright", "Version", "Section E"};
    private ArrayList<String> categories;
    private ArrayList<String> subCategories;
    private ListView settingsView;

    public FragmentSettingsPage(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentActivity faActivity  = (FragmentActivity) super.getActivity();
        // Replace LinearLayout by the type of the root element of the layout you're trying to load
        RelativeLayout RLayout  = (RelativeLayout)  inflater.inflate(R.layout.settings_page, container, false);

        settingsView = (ListView)RLayout.findViewById(R.id.settings_list);
        categories = new ArrayList<String>();
        subCategories = new ArrayList<String>();
        for (String h : Headers)
            categories.add(h);
        subCategories.add(getIPAddress());
        subCategories.add(getDeviceName());
        subCategories.add("Daniel Yao, Nick Guan, Michael Shea, Dylan Ler");
        subCategories.add("1.0");
        subCategories.add("More Info Here...");

        SettingsAdapter settingAdt = new SettingsAdapter(this,faActivity,categories,subCategories);
        settingsView.setAdapter(settingAdt);

        return RLayout; // We must return the loaded Layout
    }

    private String getIPAddress(){
        WifiManager wifiMgr = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        if(ip == 0)
            return "Disconnected";
        String ipAddress = String.format(
                "%d.%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));
        return ipAddress;
    }

    private String getDeviceName(){
        return Build.MODEL;
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.settingsElement:
                break;
            default:
                break;
        }
    }
}
