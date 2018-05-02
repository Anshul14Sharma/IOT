package com.example.justin.finalsocket;

import android.os.AsyncTask;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by justin on 13-04-2018.
 */
/**
 * Created by justin on 12-04-2018.
 */
import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by justin on 12-04-2018.
 */

public class SendMessage extends AsyncTask<String, Void, Void> {

    private Exception exception;
    @Override
    protected  Void doInBackground(String... params) {
        try {
            try {
                //int port = Integer.parseInt(params[1]);
                Socket socket = new Socket(params[0],8081);
                PrintWriter outToserver = new PrintWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream()));
                outToserver.print(params[1]);
                // outToserver.print("IP"+params[1]);
                outToserver.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            this.exception = e;
            return null;
        }
        return null;
    }
}


