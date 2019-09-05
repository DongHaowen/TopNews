package com.example.topnews.utils;

import com.example.topnews.bean.WebPackage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;


public class UserClient extends Thread {
    final static String host = "192.168.8.105";
    final static int port = 8888;
    Socket socket;
    WebPackage webPackage;
    WebPackage request = null;
    WebPackage response = null;

    public void setWebPackage(WebPackage webPackage) {
        this.webPackage = webPackage;
    }

    public WebPackage getResponse(){
        return response;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        super.run();
        try {
            socket = new Socket(host,port);
            send();
            receive();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private void send() throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String value = gson.toJson(webPackage, WebPackage.class);
        writer.write(value + "\n");
        writer.flush();
        System.out.println("Client Send");
    }

    private void receive() throws IOException {
        InputStream inputStream = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        response = gson.fromJson(reader.readLine(), WebPackage.class);
        reader.close();
        System.out.println("Client Receive");
    }
}
