package controller;

import model.BankAccount;
import model.User;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

/**
 * Created by Do Chi Thanh on 4/21/2017.
 */
public class NewThread extends Thread{
    private Socket socket;
    private ObjectInputStream objectInput;
    private ObjectOutputStream objecOutput;
    private PrintWriter output ;


    public Socket connectPrimaryServer(){
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

    NewThread(){
        this.socket = connectPrimaryServer();
        start();
    }

    NewThread(Socket primarySocket){
        this.socket = primarySocket;
        start();
    }

    NewThread(PrintWriter output, ObjectInputStream objectInput){
        this.objectInput = objectInput;
        //this.objecOutput = objectOutput;
        this.output = output;
        start();
    }

    public void run(){
        while (true){
            String command = null;
            try {
                command = (String) objectInput.readObject();     //Wait for command from primary server
                System.out.println("New Thread command: " + command);

                switch (command){
                    case "withdraw":{
                        long value = 0;
                        long accID = 0;
                        try {
                            value = (long) objectInput.readObject();
                            accID = (long) objectInput.readObject();
                            BankAccount acc = new BankAccount(accID);
                            acc.minusMoney(value);
                            output.println("Rút tiền thành công!");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case "recharge":{
                        long value = 0;
                        long accID = 0;
                        try {
                            value = (long) objectInput.readObject();
                            accID = (long) objectInput.readObject();

                            BankAccount acc = new BankAccount(accID);
                            acc.addMoney(value);
                            output.println("Cộng tiền thành công!");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                    }
                    case "reset password": {
                        long userID = 0;
                        String newPassword = null;

                        try {
                            userID = (long) objectInput.readObject();
                            newPassword = (String) objectInput.readObject();

                            User.setPassword(userID, newPassword);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        output.println("Thay đổi mật khẩu thành công!");
                        break;
                    }
                    case "create account": {
                        long IDAccount = 0;
                        long IDAccountFromPrimaryServer = 0;
                        long initvalue = 0;
                        User[] accHost = null;
                        try {
                            initvalue = (long) objectInput.readObject();
                            accHost = (User[]) objectInput.readObject();
                            //IDAccount = (long) objectInput.readObject();
                            BankAccount.createAccount(initvalue, accHost);      // Check when IDAccount are the same in 2 database

                            output.println("Tạo tài khoản thành công!");
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case "delete account": {
                        long IDAccount = 0;
                        try {
                            IDAccount = (long) objectInput.readObject();
                            BankAccount.deleteAccount(IDAccount);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        output.println("Xóa tài khoản thành công!");
                        break;
                    }
                    default: {
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("New Thread error socket!");
                e.printStackTrace();
            }
        }

    }
}
