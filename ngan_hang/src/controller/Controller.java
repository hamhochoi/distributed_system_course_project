/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
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
            ServerSocket serverSocket = new ServerSocket(5555);
//            while (true) {
                Socket sk = serverSocket.accept();
                new UserThread(sk).start();
//            }
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
