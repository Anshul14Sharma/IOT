package com.example.justin.finalsocket;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static android.text.TextUtils.isEmpty;
import static java.lang.Character.isDigit;

/**
 * Created by justin on 17-04-2018.
 */

public class MainActivity3 extends AppCompatActivity {

    private Socket socket;
    String SERVER_IP = null;
    TextView risp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        risp = (TextView) findViewById(R.id.pi_response);

        //final EditText port = (EditText)findViewById(R.id.port);
        final EditText session = (EditText) findViewById(R.id.session_id);
        final EditText acquistion = (EditText) findViewById(R.id.acq_freq);
        final EditText upload = (EditText) findViewById(R.id.upload_freq);


        final Button send = (Button) findViewById(R.id.send_button);
        final Button stop = (Button) findViewById(R.id.stop_button);
        final Button reset = (Button) findViewById(R.id.reset_button);
        final Button back = (Button) findViewById(R.id.back_button);
        stop.setEnabled(false);
        reset.setEnabled(false);

        Intent intent = getIntent();
        SERVER_IP = intent.getStringExtra("ipAdd");
        final String newAddress = SERVER_IP;
        class ConnectionTask extends AsyncTask<String, Void, String> {

            protected String doInBackground(String... params) {
                String responce = null;
                try {
                    socket = new Socket(params[0], 8081);
                    PrintWriter outToserver = new PrintWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream()));
                    outToserver.print(params[1]);
                    outToserver.flush();

                    InputStream input = socket.getInputStream();
                    int lockSeconds = 10 * 1000;

                    long lockThreadCheckpoint = System.currentTimeMillis();
                    int availableBytes = input.available();
                    while (availableBytes <= 0 && (System.currentTimeMillis() < lockThreadCheckpoint + lockSeconds)) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                        }
                        availableBytes = input.available();
                    }

                    byte[] buffer = new byte[availableBytes];
                    input.read(buffer, 0, availableBytes);
                    responce = new String(buffer);

                    outToserver.close();
                    input.close();
                    socket.close();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return responce;
            }

            protected void onPostExecute(String responce) {
                risp.setText(responce);
            }
        }
        /*class ClientThread implements Runnable {

            @Override
            public void run() {

                try {
                    InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

                    socket = new Socket(serverAddr, 8081);

                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }

        }
        new Thread(new ClientThread()).start();*/

        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stop.setEnabled(true);
                reset.setEnabled(true);
                if ((session.getText().toString().equals(""))) {
                    Toast.makeText(MainActivity3.this, "Key is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ((acquistion.getText().toString().equals(""))) {
                    Toast.makeText(MainActivity3.this, "Acquisition is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ((upload.getText().toString().equals(""))) {
                    Toast.makeText(MainActivity3.this, "Upload is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                String text1 = session.getText().toString();
                String text2 = acquistion.getText().toString();
                String text3 = upload.getText().toString();
                if (!(text1.equals(""))) {
                    if (text1.length() < 9)  {
                        Toast.makeText(MainActivity3.this, "key less than 9", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (text1.length() > 9) {
                        Toast.makeText(MainActivity3.this, "key more than 9", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
                boolean digitsOnly1 = TextUtils.isDigitsOnly(session.getText());
                boolean digitsOnly2 = TextUtils.isDigitsOnly(acquistion.getText());
                boolean digitsOnly3 = TextUtils.isDigitsOnly(upload.getText());


                String text4 = session.getText().toString();
                try {
                    int num = Integer.parseInt(text4);
                    Log.i("",num+" is a number");
                } catch (NumberFormatException e) {
                    Log.i("",text4+" is not a number");
                }

                String text5 = acquistion.getText().toString();
                try {
                    int num = Integer.parseInt(text5);
                    Log.i("",num+" is a number");
                } catch (NumberFormatException e) {
                    Log.i("",text5+" is not a number");
                }

                String text6 = upload.getText().toString();
                try {
                    int num = Integer.parseInt(text6);
                    Log.i("",num+" is a number");

                } catch (NumberFormatException e) {
                    Log.i("",text6+"is not a number");
                }

                final String wp = "t"+","+session.getText().toString() + ',' + acquistion.getText().toString() + ',' + upload.getText().toString();
                //new SendMessage().execute(newAddress, wp);
                new ConnectionTask().execute(newAddress,wp);
                toastMsg("Sent!");

            }

            public void toastMsg(String msg) {

                Toast.makeText(MainActivity3.this, msg, Toast.LENGTH_SHORT).show();

            }

        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop.setEnabled(false);
                //send.setEnabled(true);
                reset.setEnabled(true);
                new ConnectionTask().execute(newAddress,"e");
                toastMsg("Stopped!");
            }

            public void toastMsg(String msg) {

                Toast.makeText(MainActivity3.this, msg, Toast.LENGTH_SHORT).show();

            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send.setEnabled(true);
                reset.setEnabled(false);
                stop.setEnabled(true);
                acquistion.getText().clear();
                upload.getText().clear();
                new ConnectionTask().execute(newAddress,"r");
                toastMsg("Reset!");
            }

            public void toastMsg(String msg) {

                Toast.makeText(MainActivity3.this, msg, Toast.LENGTH_SHORT).show();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(MainActivity3.this, MainActivity2.class);
                startActivity(intent1);
                toastMsg("Back");

            }
            public void toastMsg(String msg) {
                Toast.makeText(MainActivity3.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean isDigits(String text1) {
        for (int i = 0; i < text1.length(); i++) {
            if (!Character.isDigit(text1.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}




