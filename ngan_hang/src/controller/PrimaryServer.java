package controller;

import model.BankAccount;
import model.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

/**
 * Created by Do Chi Thanh on 4/18/2017.
 */
public class PrimaryServer {
    public static void main(String[] argv) {

        //while(true) {
            Socket socket = null;
            try {
                ServerSocket serverSocket = new ServerSocket(6666);
                socket = serverSocket.accept();

            } catch (IOException e) {
                e.getMessage();
                return ;
            }
            ObjectOutputStream objectOutput = null;
            ObjectInputStream objectInput = null;
            try {
                objectOutput = new ObjectOutputStream(socket.getOutputStream());
                objectInput = new ObjectInputStream(socket.getInputStream());

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            while (true) {
                try {
                    String command = null;
                    try {
                        command = (String) (objectInput.readObject());      // Recieve command from client->server->primary server
                        objectOutput.writeObject(command);                  // Send command to server
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                    switch (command) {
                        case "create account": {
                            long IDAccount = 0;
                            long initvalue = 0;
                            User[] accHost = null;
                            try {
                                initvalue = (long) objectInput.readObject();
                                accHost = (User[]) objectInput.readObject();
                                IDAccount = BankAccount.createAccount1(initvalue, accHost);

                                objectOutput.writeObject(initvalue);
                                objectOutput.writeObject(accHost);
                                //objectOutput.writeObject(IDAccount);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case "delete account": {
                            long IDAccount = 0;
                            try {
                                IDAccount = (long) objectInput.readObject();
                                BankAccount.deleteAccount1(IDAccount);

                                objectOutput.writeObject(IDAccount);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case "recharge": {
                            long value = 0;
                            long accID = 0;
                            try {
                                value = (long) objectInput.readObject();
                                accID = (long) objectInput.readObject();

                                BankAccount acc = new BankAccount(accID);
                                acc.addMoney1(value);

                                objectOutput.writeObject(value);
                                objectOutput.writeObject(accID);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            break;
                        }
                        case "withdraw": {
                            long value = 0;
                            long accID = 0;
                            try {
                                value = (long) objectInput.readObject();
                                accID = (long) objectInput.readObject();
                                BankAccount acc = new BankAccount(accID);
                                boolean boo = acc.minusMoney1(value);

                                if (boo == true) {
                                    objectOutput.writeObject(value);
                                    objectOutput.writeObject(accID);
                                }
                                //String status = (String)objectInput.readObject();
                                //if (status.compareTo("Rút tiền thành công!") == 0){
                                //    objectOutput.writeObject(status);
                                    //System.out.println("Successful with draw!");
                                //}

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

                                User.setPassword1(userID, newPassword);

                                objectOutput.writeObject(userID);
                                objectOutput.writeObject(newPassword);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            break;
                        }
                        default: {
                            break;
                        }
                    }
                } catch (Exception e) {
                    //System.out.println(e.getMessage());
                }

                /*
                try {
                    objectOutput.close();
                    objectInput.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */
            }
        //}
    }
}
