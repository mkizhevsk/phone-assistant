package com.mk.phoneassistant;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothClientRunnable implements Runnable {

    private final BluetoothSocket bluetoothSocket;

    private int buttonNumber;

    private static final String TAG = "MainActivity";

    public BluetoothClientRunnable(BluetoothDevice device, int tempNumber) {

        BluetoothSocket tmp = null;

        try {
            UUID myId = UUID.fromString("302a5a70-c085-4946-b702-fc1deb1046af");
            tmp = device.createRfcommSocketToServiceRecord(myId);
        } catch (IOException e) {

            Log.e(TAG, "Socket's create() method failed", e);
        }
        bluetoothSocket = tmp;

        buttonNumber = tempNumber;
    }

    @Override
    public void run() {
        Log.d(TAG, "bluetooth client run start");
        // Cancel discovery because it otherwise slows down the connection.
        //MainActivity.bluetoothAdapter.cancelDiscovery();

        try {
            Log.d(TAG, "try to connect..");
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            bluetoothSocket.connect();
        } catch (IOException connectException) {
            Log.d(TAG, "can not connect");
            // Unable to connect; close the socket and return.
            try {
                bluetoothSocket.close();
                Log.d(TAG, "bluetoothSocket was closed");
            } catch (IOException e) {
                Log.e(TAG, "Could not close bluetoothSocket", e);
            }
            return;
        }
        Log.d(TAG, "client connection success!");

        //MainActivity.bluetoothSocket = this.bluetoothSocket;

        Thread thread = new Thread(new ConnectedThread(bluetoothSocket, buttonNumber));
        thread.start();

//        try {
//            OutputStream outputStream = bluetoothSocket.getOutputStream();
//
//            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
//
//            dataOutputStream.writeInt(buttonNumber);
//            Log.e(TAG, "Output: " + buttonNumber);
//
//            //cancel();
//        } catch (IOException e) {
//            Log.d(TAG, "Error occured when creating output stream");
//            cancel();
//        }

    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            bluetoothSocket.close();
            Log.d(TAG, "bluetoothSocket was closed");
        } catch (IOException e) {
            Log.e(TAG, "Could not close bluetoothSocket", e);
        }
    }

}
