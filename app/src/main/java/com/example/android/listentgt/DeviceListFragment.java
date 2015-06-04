package com.example.android.listentgt;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.listentgt.R;

import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
//import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import NanoHTTPD.NanoHTTPD;
import NanoHTTPD.SimpleWebServer;

/**
 * A ListFragment that displays available peers on discovery and requests the
 * parent activity to handle user interaction events
 */
public class DeviceListFragment extends ListFragment implements PeerListListener, WifiP2pManager.ConnectionInfoListener, Handler.Callback {

    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    ProgressDialog progressDialog = null;
    View mContentView = null;
    private WifiP2pDevice device;

    //handler
    private final Handler handler = new Handler(this);


    //ServerThread variables
    private GroupOwnerSocketHandler serverThread;
    private ClientSocketHandler clientThread;
    private String httpHostIP = null;
    private Activity mActivity = getActivity();

    private File wwwroot = null;
    private NanoHTTPD httpServer = null;
    public static final int HTTP_PORT = 9002;

    private WifiP2pInfo info;

    private static boolean isServer = false;
    private static boolean isClient = false;

    //get whether I am server or client
    public boolean getIsServer() {
        return isServer;
    }

    public boolean getIsClient() {
        return isClient;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setListAdapter(new WiFiPeerListAdapter(getActivity(), R.layout.row_devices, peers));

    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mActivity = activity;

        // get the application directory
        wwwroot = mActivity.getApplicationContext().getFilesDir();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.device_list, null);
        this.setListAdapter(new WiFiPeerListAdapter(getActivity(), R.layout.row_devices, peers));

        updateThisDevice(device);
        return mContentView;
    }

    /**
     * @return this device
     */
    public WifiP2pDevice getDevice() {
        return device;
    }

    private static String getDeviceStatus(int deviceStatus) {
//        Log.d(WiFiDirectActivity.TAG, "Peer status :" + deviceStatus);
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";

        }
    }

    /**
     * Initiate a connection with the peer when listItem clicked.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                "Connecting to :" + device.deviceAddress, true, true
//                        new DialogInterface.OnCancelListener() {
//
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                ((DeviceActionListener) getActivity()).cancelDisconnect();
//                            }
//                        }
        );
        ((DeviceActionListener) getActivity()).connect(config);

    }

    /**
     * Array adapter for ListFragment that maintains WifiP2pDevice list.
     */
    private class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {

        private List<WifiP2pDevice> items;

        /**
         * @param context
         * @param textViewResourceId
         * @param objects
         */
        public WiFiPeerListAdapter(Context context, int textViewResourceId,
                                   List<WifiP2pDevice> objects) {
            super(context, textViewResourceId, objects);
            items = objects;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_devices, null);
            }
            WifiP2pDevice device = items.get(position);
            if (device != null) {
                TextView top = (TextView) v.findViewById(R.id.device_name);
                TextView bottom = (TextView) v.findViewById(R.id.device_details);
                if (top != null) {
                    top.setText(device.deviceName);
                }
                if (bottom != null) {
                    bottom.setText(getDeviceStatus(device.status));
                }
            }

            return v;

        }
    }

    /**
     * Update UI for this device.
     *
     * @param device WifiP2pDevice object
     */
    public void updateThisDevice(WifiP2pDevice device) {
        this.device = device;
        //this is buggish, I dunno why
        if (mContentView != null) {
            TextView view = (TextView) mContentView.findViewById(R.id.my_name);

            view.setText(Build.MODEL + ": " + device.deviceName);

            view = (TextView) mContentView.findViewById(R.id.my_status);
            view.setText(getDeviceStatus(device.status));
        }
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {

        Log.d("OnPeersAvailable: ", "No devices found");

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        peers.clear();
        peers.addAll(peerList.getDeviceList());

        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();

//        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
        if (peers.size() == 0) {
//            Log.d(WiFiDirectActivity.TAG, "No devices found");
            return;
        }

    }

    public void clearPeers() {
        peers.clear();
//        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
    }

    /**
     *
     */
    public void onInitiateDiscovery(Activity activity) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(activity, "Press back to cancel", "finding peers", true,
                true, new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                });

    }

    //Handler.Callback
    @Override
    public boolean handleMessage(Message msg)
    {
        switch (msg.what)
        {
            case GroupOwnerSocketHandler.SERVER_CALLBACK:
                serverThread = (GroupOwnerSocketHandler) msg.obj;
                Log.d(MainActivity.TAG, "Retrieved server thread.");
                break;
            //Deal with received message
            case ClientSocketHandler.EVENT_RECEIVE_MSG:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf);

                // interpret the command
                String[] cmdString = readMessage
                        .split(GroupOwnerSocketHandler.CMD_DELIMITER);

                Log.i("ClientSocketHandler", readMessage);

                if (cmdString[0].equals(GroupOwnerSocketHandler.PLAY_CMD)
                    && cmdString.length == 2)
                 {
                    //continue playing
                    Log.i("Client handling music", "continue to play" );
                    MainActivity.musicSrv.play();
                    //((DeviceActionListener) getActivity()).clientContinueMusic();

                 }
                else if (cmdString[0].equals(GroupOwnerSocketHandler.SET_POSITION))
                {
                    MainActivity.musicSrv.player.seekTo(Integer.parseInt(cmdString[1]));
                }
                else if (cmdString[0].equals(GroupOwnerSocketHandler.PLAY_CMD)
                        && cmdString.length > 2)
                {
                    try
                    {
                        Log.i("Client handling music", "trying to play");

                        MainActivity.musicSrv.clientPlaySong(cmdString[1], 0);

                    }
                    catch (NumberFormatException e)
                    {
                        Log.e(MainActivity.TAG,
                                "Could not convert to a proper time for these two strings: "
                                        + cmdString[2] + " and " + cmdString[3],
                                e);
                    }
                }
                else if (cmdString[0].equals(GroupOwnerSocketHandler.STOP_CMD)
                        && cmdString.length > 0)
                {
                    MainActivity.musicSrv.pause();
                    //((DeviceActionListener) getActivity()).stopMusic();
                }

                Log.d(MainActivity.TAG, readMessage);

                // Toast.makeText(mContentView.getContext(),
                // "Received message: " + readMessage, Toast.LENGTH_SHORT)
                // .show();
                break;

            case ClientSocketHandler.CLIENT_CALLBACK:
                clientThread = (ClientSocketHandler) msg.obj;
                Log.d(MainActivity.TAG, "Retrieved client thread.");
                break;

            default:
                Log.d(MainActivity.TAG, "I thought we heard something? Message type: "
                        + msg.what);
                break;
        }
        return true;
    }


    //ConnectionInfoListener

    //onConnectionInfoAvailable is triggered when you have info available
    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;

        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {

            //start the server thread
            try
            {
                isServer = true;
                isClient = false;
                // WARNING:
                // depends on the timing, if we don't get a server back in time,
                // we may end up running multiple threads of the server
                // instance!

                Toast.makeText(getActivity(),
                        "trying to start a DJ Server",
                        Toast.LENGTH_SHORT).show();

                //destroy the serverThread if it is not null
                if (this.serverThread != null)
                    this.serverThread = null;

                if (this.serverThread == null)
                {
                    Toast.makeText(getActivity(),
                            "starting a DJ Server",
                            Toast.LENGTH_SHORT).show();

                    Thread server = new GroupOwnerSocketHandler(this.handler);
                    server.start();

                    if (wwwroot != null)
                    {
                        if (httpServer == null)
                        {
                            httpHostIP = info.groupOwnerAddress
                                    .getHostAddress();

                            boolean quiet = false;

                            httpServer = new SimpleWebServer(httpHostIP,
                                    HTTP_PORT, wwwroot, quiet);
                            try
                            {
                                httpServer.start();
                                Log.d("HTTP Server",
                                        "Started web server with IP address: "
                                                + httpHostIP);
                                Toast.makeText(getActivity(),
                                        "DJ Server started.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            catch (IOException ioe)
                            {
                                Log.e("HTTP Server", "Couldn't start server:\n");
                                Toast.makeText(getActivity(),
                                        "Server failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else
                    {
                        Log.e("HTTP Server",
                                "Could not retrieve a directory for the HTTP server.");
                    }
                }
            }
            catch (IOException e)
            {
                Log.e(MainActivity.TAG, "Cannot start server.", e);
            }

        } else if (info.groupFormed) {
            //start the client thread
            // WARNING:
            // depends on the timing, if we don't get a server back in time,
            // we may end up running multiple threads of the client
            // instance!
            isServer = false;
            isClient = true;

            if (this.clientThread != null)
                this.clientThread = null;
            
            if (this.clientThread == null)
            {
                Thread client = new ClientSocketHandler(this.handler,
                        info.groupOwnerAddress);
                client.start();
            }

            Toast.makeText(mContentView.getContext(),
                    "Speaker client started.", Toast.LENGTH_SHORT).show();
        }

}



    /**
     * Updates the UI with device data
     *
     * @param device the device to be displayed
     */
    //no use yet
    public void showDetails(WifiP2pDevice device) {
//        this.device = device;
//        this.getView().setVisibility(View.VISIBLE);
//        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
//        view.setText(device.deviceAddress);
//        view = (TextView) mContentView.findViewById(R.id.device_info);
//        view.setText(device.toString());

    }


        /** Play the music on clients -- Server Function **/

    //this connects to the serverThread, so when button is pressed, we send Play to the client.
    //this prepares the music file.

    public void playMusicOnClients(File musicFile)
    {
        if (serverThread == null)
        {
            Log.d(MainActivity.TAG,
                    "Server has not started. No music will be played remotely.");
            return;
        }

        try
        {
            // copy the actual file to the web server directory, then pass the
            // URL to the client
            File webFile = new File(wwwroot, musicFile.getName());

            Utilities.copyFile(musicFile, webFile);

            Uri webMusicURI = Uri.parse("http://" + httpHostIP + ":"
                    + String.valueOf(HTTP_PORT) + "/" + webFile.getName());

            serverThread.sendPlay(webMusicURI.toString());
        }
        catch (IOException e1)
        {
            Log.e("HTTP Server", "Cannot copy file.", e1);
        }
    }

    public void continueMusicOnClients()
    {
        Log.i("Music Service", "Continue music on clients called.");
        serverThread.sendContinue();

    }

    public void stopMusicOnClients()
    {
        if (serverThread != null)
        {
            serverThread.sendStop();
        }
    }

    public void setPositionOnClients(int newPosition)
    {
        if (serverThread != null)
        {
            serverThread.setPosition(newPosition);
        }
    }
    /***
     * Helper Functions
     *
     */
    public String getFileFromURI(Uri path) {
        return Utilities.getRealPathFromUri(getActivity(), path);
    }

}
