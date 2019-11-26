package com.mk.phoneassistant;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.WindowManager;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    //public static BluetoothSocket bluetoothSocket;
    public static InputStream inputStream;
    public static OutputStream outputStream;

    private static final String TAG = "MainActivity";

    public static int tempInt = 0;
    public static Handler inputHandler;

    ActionBar actionBar;

    public static BluetoothAdapter bluetoothAdapter;
    private Thread bluetoothClientThread;
    private Thread bluetoothServerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("Гости из будущего - Я знаю только лучшее в тебе");
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter.isEnabled()) {
            //bluetoothServerThread = new Thread(new BluetoothServerRunnable(bluetoothAdapter));
            //bluetoothServerThread.start();
        }

        inputHandler = new Handler(new Handler.Callback() {
            public boolean handleMessage(Message message) {
                Bundle bundle = message.getData();
                String trackInfo = bundle.getString("trackInfo");
                //Log.d(TAG, "trackInfo: " + buttonNumber);
                if(actionBar != null) {
                    actionBar.setTitle(trackInfo);
                }

                return true;
            }
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    public  boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "enable");
        menu.add(0, 2, 0, "check");
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:

                break;
            case 2:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPrevious:
                sendCommand(1);
                break;
            case R.id.btnNext:
                sendCommand(2);
                break;
            case R.id.btnForward:
                sendCommand(3);
                break;
            case R.id.btnMinus:
                sendCommand(4);
                break;
            case R.id.btnPlayPause:
                sendCommand(5);
                break;
            case R.id.btnPlus:
                sendCommand(6);
                break;
        }
    }

    private  void sendCommand(int buttonNumber) {
        if(bluetoothClientThread == null) {
            startBluetoothClientThread(buttonNumber);
        } else {
            try {
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                dataOutputStream.writeInt(buttonNumber);
                Log.e(TAG, "Output: " + buttonNumber);
            } catch (IOException e) {
                Log.d(TAG, "Error occurred when sending data");

                if(bluetoothClientThread != null) {
                    bluetoothClientThread.interrupt();
                }
                startBluetoothClientThread(buttonNumber);
            }
        }
    }

    private void startBluetoothClientThread(int buttonNumber) {
        if(bluetoothAdapter.isEnabled()) {
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice("F4:F5:DB:69:2A:AE");
            bluetoothClientThread = new Thread(new BluetoothClientRunnable(bluetoothDevice, buttonNumber));
            bluetoothClientThread.start();
            bluetoothAdapter.cancelDiscovery();
        }
    }

    @Override
    protected void onDestroy() {
//        if(bluetoothServerThread != null) {
//            BluetoothServerRunnable.running = false;
//            bluetoothServerThread.interrupt();
//        }
        ConnectedThread.running = false;

        if(inputStream != null) {
            try {
                inputStream.close();
                Log.d(TAG, "inputStream was closed");
            } catch (IOException e) {
                Log.d(TAG, "Error occurred when closing inputStream");
            }
        }

        if(outputStream != null) {
            try {
                outputStream.close();
                Log.d(TAG, "outputStream was closed");
            } catch (IOException e) {
                Log.d(TAG, "Error occurred when closing outputStream");
            }
        }

//        if(bluetoothSocket != null) {
//            try {
//                bluetoothSocket.close();
//                Log.d(TAG, "bluetoothSocket was closed");
//            } catch (IOException e) {
//                Log.e(TAG, "Could not close bluetoothSocket", e);
//            }
//        }

        if(bluetoothClientThread != null) {
            bluetoothClientThread.interrupt();
        }

        Log.d(TAG, "finish");
        super.onDestroy();
    }

//    @Override
//    protected void onUserLeaveHint() {
//        super.onUserLeaveHint();
//        Log.d(TAG, "home key clicked");
//    }

}
