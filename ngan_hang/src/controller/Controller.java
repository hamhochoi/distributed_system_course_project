/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.sun.corba.se.spi.activation.Server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hung Nguyen Manh
 */
public class Controller {

    public static void main(String[] args) {
        try {
            //InetAddress addr = InetAddress.getByName("117.0.203.229");
            //ServerSocket serverSocket = new ServerSocket(5555, 50, addr);

            ServerSocket serverSocket = new ServerSocket(5555);

            while (true) {
                Socket socket = serverSocket.accept();

                PrintWriter output = null;
                BufferedReader input = null;
                ObjectOutputStream objectOutput = null;
                ObjectInputStream objectInput = null;
                Socket primarySocket = connectPrimaryServer();
                try {
                    output = new PrintWriter(socket.getOutputStream(), true);
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    objectOutput = new ObjectOutputStream(primarySocket.getOutputStream());
                    objectInput = new ObjectInputStream(primarySocket.getInputStream());
                } catch (Exception ex) {
                    System.out.println("Socket error!");
                    //Logger.getLogger(UserThread.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                }

                new UserThread(input,output, objectOutput).start();
                //System.out.println("Test");
                //socket.close();
                new NewThread(output, objectInput);
                //System.out.println("Test");
            }
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Socket connectPrimaryServer(){
        Socket socket = null;
        try{
            socket = new Socket("localhost",6666);
            //System.out.println("Connected to primary server!");
        }
        catch (Exception e){
            e.getMessage();
        }

        return socket;
    }
}
