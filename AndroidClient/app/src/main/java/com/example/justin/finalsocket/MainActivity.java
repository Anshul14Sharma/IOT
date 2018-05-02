package com.example.justin.finalsocket;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


/**
 * Created by justin on 04-04-2018.
 */
import android.widget.Toast;

import org.w3c.dom.Text;


/**
 * Created by justin on 04-04-2018.
 */


public class MainActivity extends AppCompatActivity {

    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice = null;

    final byte delimiter = 33;
    int readBufferPosition = 0;
    EditText editText;
    TextView wifi;
    TextView pswd;

    String names[] ={"HP_UNIFI","HP_test","HtBeat1","diamond","OnePLus3"};

    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;
    int clickCounter=0;

    public void sendBtMsg(final String msg2send) {
        //UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"); //Standard SerialPortService ID
        try {

            if (mmDevice != null) {
                mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
                if (!mmSocket.isConnected()) {
                    try {
                        mmSocket.connect();
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                toastMsg("Connection Established");

                                String msg = msg2send;
                                //msg += "\n";
                                OutputStream mmOutputStream = null;
                                try {
                                    mmOutputStream = mmSocket.getOutputStream();
                                    mmOutputStream.write(msg.getBytes());

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                    } catch (IOException e) {
                        Log.e("", e.getMessage());
                        mmSocket.close();
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                toastMsg("Connection not Established");


                            }
                        });

                        /*try {
                            Log.e("", "trying fallback");
                            mmSocket =(BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(mmDevice,1);
                            mmSocket.connect();
                            Log.e("", "Connected");
                        }
                        catch (Exception e2){
                            Log.e("", "Couldn't establish Bluetooth connection!");
                            toastMsg("Couldn't establish Bluetooth connection!");
                        }*/
                    }
                }


/*
                if (mmOutputStream == null)
                    try {
                        mmOutputStream = mmSocket.getOutputStream();
                    } catch (IOException e) {
                        errorExit("Fatal Error", "In onResume(), input and output stream creation failed:" + e.getMessage() + ".");
                    }*/

            } else {
                if (mBluetoothAdapter != null) {


                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                    if (pairedDevices.size() > 0) {
                        for (BluetoothDevice device : pairedDevices) {
                            if (device.getName().equals("raspberrypi")) //Note, you will need to change this to match the name of your device
                            {
                                Log.e("Aquarium", device.getName());
                                mmDevice = device;
                                sendBtMsg(msg2send);
                                break;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
       /* try {

            mmSocket=createBluetoothSocket(mmDevice);
            if(!mmSocket.isConnected()){
                mmSocket.connect();
            }
            Log.e("","trying fallback...");
        }catch (Exception e){
            Log.e("","trying fallback...");
        }*/

       /* try {
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        } catch (Exception e) {Log.e("","Error creating socket");}

        try {
            mmSocket.connect();
            Log.e("","Connected");
        } catch (IOException e) {
            Log.e("",e.getMessage());
            try {
                Log.e("","trying fallback...");

                mmSocket =(BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(mmDevice,1);
                mmSocket.connect();

                Log.e("","Connected");
            }
            catch (Exception e2) {
                Log.e("", "Couldn't establish Bluetooth connection!");
            }
        }*/

   /* private BluetoothSocket createBluetoothSocket(BluetoothDevice device)
            throws IOException {
        UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
        if(Build.VERSION.SDK_INT >= 10){
            try {

                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, uuid);
            } catch (Exception e) {
                Log.e("TAG", "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(uuid);
    }*/

    BluetoothAdapter mBluetoothAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //    final TextView myLabel = (TextView) findViewById(R.id.btResult);
        //  final EditText editText = (EditText) findViewById(R.id.editText);
        // Button tempButton =(Button) findViewById(R.id.tempButton);

        final AutoCompleteTextView at=(AutoCompleteTextView)findViewById(R.id.autodemo);
        at.setAdapter(new ArrayAdapter(this,android.R.layout.simple_list_item_1,names));
        at.setThreshold(1);
        at.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv=(TextView)view;
                Toast.makeText(MainActivity.this, tv.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        final Handler handler = new Handler();

        final TextView myLabel = (TextView) findViewById(R.id.pi_response);
  //      wifi =(TextView)findViewById(R.id.textView1);
   //     pswd =(TextView)findViewById(R.id.textView2);

   //     final EditText wifiInput = (EditText) findViewById(R.id.wifi_input);
        final EditText Password = (EditText) findViewById(R.id.wifi_password);

     //   Password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
     //   Password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
     //   Password.setSelection(Password.length());


        Button keyButton = (Button) findViewById(R.id.key_button);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        final class workerThread implements Runnable {

            private String btMsg;

            public workerThread(String msg) {
                btMsg = msg;
            }

            public void run() {
                sendBtMsg(btMsg);
                if (mmSocket.isConnected()) {


                    while (!Thread.currentThread().isInterrupted()) {
                        int bytesAvailable;
                        boolean workDone = false;

                        try {

                      /*  if(mmSocket==null){
                            return;
                        } */

                            if (mmSocket != null) {

                                final InputStream mmInputStream;
                                mmInputStream = mmSocket.getInputStream();
                                if (mmInputStream == null) {
                                    return;
                                }
                                bytesAvailable = mmInputStream.available();
                                if (bytesAvailable > 0) {

                                    byte[] packetBytes = new byte[bytesAvailable];
                                    Log.e("Aquarium recv bt", "bytes available");
                                    byte[] readBuffer = new byte[1024];
                                    mmInputStream.read(packetBytes);

                                    for (int i = 0; i < bytesAvailable; i++) {
                                        byte b = packetBytes[i];
                                        if (b == delimiter) {
                                            byte[] encodedBytes = new byte[readBufferPosition];
                                            System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                            final String data = new String(encodedBytes, "US-ASCII");
                                            readBufferPosition = 0;

                                            //The variable data now contains our full command
                                            handler.post(new Runnable() {
                                                public void run() {

                                                    myLabel.setText(data);
                                                }
                                            });

                                            workDone = true;
/*

                                        Intent intent = new Intent();
                                        intent.setAction("com.example.justin.pratiksocket");
                                        intent.putExtra("ipAddress", data);
                                        startActivity(intent);
*/
                                            String finalip = new String();
                                            finalip = data.substring(data.length() - (bytesAvailable - 1));
                                       /*Intent intent1 = getPackageManager().getLaunchIntentForPackage("com.example.justin.pratiksocket");
                                        intent1.putExtra("ipAddress", data);
                                        startActivity(intent1);*/

                                            Intent intent1 = new Intent(MainActivity.this, MainActivity2.class);
                                            intent1.putExtra("ipAddress", finalip);
                                            startActivity(intent1);


                                            break;


                                        } else {
                                            readBuffer[readBufferPosition++] = b;
                                        }
                                    }

                                    if (workDone == true) {
                                        mmSocket.close();
                                        break;
                                    }

                                }

                            }
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                }

            }
        }
        ;
//        LoadPreferences();


        //Session key input handler
        keyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) throws RuntimeException {
                // Perform action on key button click
                String first = at.getText().toString();
                String second = Password.getText().toString();
                SavePreferences("Wifi Input", at.getText().toString());
   //             LoadPreferences();
                SavePreferences("Password", Password.getText().toString());
    //            LoadPreferences();
                if (first.equals("")) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Wifi Field Empty", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //wifiInput.setError("Input field require");
                }
                if (second.equals("")) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Password Field Empty", Toast.LENGTH_SHORT).show();

                        }
                    });

                    //Password.setError("Input field require");
                }
                final String wp = first + "," + second;
                (new Thread(new workerThread(wp))).start();
           //    listItems.add(first);
                // Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
     //           finish();
            }

        });
    /*   at.setAdapter(new ArrayAdapter(this,android.R.layout.simple_list_item_1,listItems));
        at.setThreshold(1);
        at.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv=(TextView)view;
                Toast.makeText(MainActivity.this, tv.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });*/

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("raspberrypi")) //Note, you will need to change this to match the name of your device
                {
                    Log.e("Aquarium", device.getName());
                    mmDevice = device;
                    break;
                }
            }
        }
    }

    private void SavePreferences(String password, String s) {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(password, s);
        editor.commit();

    }


  /*  private void LoadPreferences() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        String first = sharedPreferences.getString("Wifi Input", "");
        String second = sharedPreferences.getString("Password", "");
        wifi.setText(first);
        pswd.setText(second);
    }*/
 //   SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
  // SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
   /*     private void LoadPreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            String strSavedMem1 = sharedPreferences.getString("MEM1", "");
            String strSavedMem2 = sharedPreferences.getString("MEM2", "");
            textSavedMem1.setText(strSavedMem1);
            textSavedMem2.setText(strSavedMem2);
        }*/
   


  /* @Override
   public void onStop() {
       super.onStop();
   }


       private void SavePreferences(String key, String value){
           SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
           SharedPreferences.Editor editor = sharedPreferences.edit();
           editor.putString(key, value);
           editor.commit();
       }*/

    public void toastMsg(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

}