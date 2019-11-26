package com.mk.phoneassistant;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ConnectedThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        //private byte[] mmBuffer;

        public static boolean running = false;

        private int buttonNumber;

        private static final String TAG = "MainActivity";

        public ConnectedThread(BluetoothSocket socket, int tempNumber) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            buttonNumber = tempNumber;

            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.d(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.d(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            running = true;

            MainActivity.inputStream = this.mmInStream;
            MainActivity.outputStream = this.mmOutStream;

            try {
                DataOutputStream dataOutputStream = new DataOutputStream(mmOutStream);
                dataOutputStream.writeInt(buttonNumber);
                Log.e(TAG, "Output: " + buttonNumber);

//            byte[] initialArray = { 0, 1, 2 };
//            outputStream.write(initialArray);
//            Log.d(TAG, "something was sent");
            } catch (IOException e) {
                Log.d(TAG, "Error occurred when sending data");
            }

            while (running) {
                if (mmInStream != null) {
                    try {
                        DataInputStream in = new DataInputStream(mmInStream);
                        String trackInfo = in.readUTF();
                        Log.e(TAG, "Input: " + trackInfo);

                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("trackInfo", trackInfo);
                        message.setData(bundle);
                        MainActivity.inputHandler.sendMessage(message);
                    } catch (IOException e) {
                        Log.d(TAG, "Input stream was disconnected");
                        try {
                            mmSocket.close();
                            Log.d(TAG, "bluetoothSocket was closed");
                        } catch (IOException be) {
                            Log.e(TAG, "Could not close bluetoothSocket", be);
                        }
                        break;
                    }
                }
            }
        }

//        public void write(byte[] bytes) {
//            try {
//                DataOutputStream dataOutputStream = new DataOutputStream(mmOutStream);
//                dataOutputStream.writeInt(1);
//                Log.e(TAG, "Output: " + 1);
//
//                //mmOutStream.write(bytes);
//                Log.d(TAG, "something was sent");
//            } catch (IOException e) {
//                Log.d(TAG, "Error occurred when sending data");
//            }
//        }
    }
