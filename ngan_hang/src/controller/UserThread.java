/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.BankAccount;
import model.User;

/**
 *
 * @author Hung Nguyen Manh
 */
public class UserThread extends Thread {

    private BufferedReader input;
    private PrintWriter output;
    private ObjectOutputStream objectOutput;
    //private ObjectInputStream objectInput;

    public UserThread(BufferedReader input, PrintWriter output, ObjectOutputStream objectOutput) {
        this.input = input;
        this.output = output;
        //this.objectInput = objectInput;
        this.objectOutput = objectOutput;
    }

    @Override
    public void run() {
        try {
            String user = login();

            if (user.equals("root")) {
                rootThread();
            } else {
                BankAccount acc = new BankAccount(User.getAccountID(Long.parseLong(user)));
                long userID = Long.parseLong(user);
                userThread(acc, userID);
            }
        } catch (IOException ex) {
            System.out.println("Lỗi vào ra");
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println("Lỗi cơ sở dữ liệu");
            System.out.println( ex.getMessage() );
        }
    }

    private String login() throws IOException, ClassNotFoundException, SQLException {
        String userID;
        String pass;

        while (true) {
            userID = input.readLine();
            System.out.println(userID);
            pass = input.readLine();
            System.out.println(pass);
            if (isNumeric(userID)) {
                if (User.checkPassword(Long.parseLong(userID), pass)) {
                    output.println("Đăng nhập thành công");
                    break;
                } else {
                    output.println("Người dùng hoặc mật khẩu không đúng");
                }
            } else if (User.checkPassword(userID, pass)) {
                output.println("Đăng nhập thành công");
                break;
            } else {
                output.println("Người dùng hoặc mật khẩu không đúng");
            }
        }
        return userID;
    }

    private boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

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

    public void userThread(BankAccount acc, long userID) {

        outerloop:
        while (true) {
            // This part transfer command from user to primary server
            String instructionClient = null;
            try {
                instructionClient = input.readLine();     // Recieve instruction from client
                if (instructionClient.compareTo("recharge") == 0
                        || instructionClient.compareTo("withdraw") == 0
                        || instructionClient.compareTo("reset password") == 0 ) {

                    objectOutput.writeObject(instructionClient);      // Send instruction to primary server
                }

            } catch (Exception ex) {
                output.println(ex.getMessage());
                break;
            }

            try {
                switch (instructionClient) {
                    case "get detail information": {
                        output.println(acc.getDetails());
                        break;
                    }
                    case "recharge": {              // Need to change
                        String value;
                        try {
                            value = input.readLine();
                        } catch (IOException ex) {
                            output.println(ex.getMessage());
                            break;
                        }
                        if (!isNumeric(value)) {
                            output.println("Giá trị không hợp lệ");
                            break;
                        }
                        try {
                            objectOutput.writeObject(Long.parseLong(value));
                            objectOutput.writeObject(acc.getAccountID());
                        } catch (Exception ex) {
                            output.println(ex.getMessage());
                            break;
                        }
                        break;
                    }
                    case "withdraw": {              // Need to change
                        String value;
                        try {
                            value = input.readLine();
                        } catch (IOException ex) {
                            System.out.println("withdraw error");
                            output.println(ex.getStackTrace());
                            break;
                        }
                        try {
                            objectOutput.writeObject(Long.parseLong(value));
                            objectOutput.writeObject(acc.getAccountID());
                            //String status = (String) objectInput.readObject();

                            //if (status.compareTo("Rút tiền thành công!") == 0){
                            //    output.println(status);
                            //}
                            //output.println("Rút tiền thành công!");
                        } catch (Exception ex) {
                            output.println(ex.getMessage());
                            break;
                        }
                        break;
                    }
                    case "reset password": {            // Need to change
                        String newPassword;
                        try {
                            newPassword = input.readLine();
                        } catch (IOException ex) {
                            output.println(ex.getMessage());
                            break;
                        }
                        try {
                            objectOutput.writeObject(userID);
                            objectOutput.writeObject(newPassword);
                        } catch (Exception ex) {
                            output.println(ex.getMessage());
                            break;
                        }
                        break;
                    }
                    case "exit":
                        break outerloop;
                    default:
                        output.printf("The command not included! ");
                        break;
                }
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    public void rootThread() {

        outerloop1:
        while (true) {
            // This part process command come frome client
            String instruction;
            try {
                instruction = input.readLine();     // Recieve instruction from client

                if (instruction.compareTo("recharge") == 0
                        || instruction.compareTo("withdraw") == 0
                        || instruction.compareTo("reset password") == 0
                        || instruction.compareTo("create account") == 0
                        || instruction.compareTo("delete account") == 0) {

                    objectOutput.writeObject(instruction);      // Send instruction to primary server
                }

            } catch (IOException ex) {
                output.println(ex.getMessage());
                break;
            }
            outerloop2:
            switch (instruction) {
                case "create account": {            // Need to change
                    String initialMoney;
                    try {
                        initialMoney = input.readLine();
                    } catch (IOException ex) {
                        output.println("InitialMoney wrong value!");
                        break;
                    }
                    ArrayList<User> users = new ArrayList<>();
                    long inivalue = 0;
                    String tmp;
                    try {
                        tmp = input.readLine();
                    } catch (IOException ex) {
                        output.println("Tmp wrong value!");
                        break;
                    }

                    //
                    while (tmp != null) {
                        if (tmp.equals("out of information")) {
                            break;
                        }
                        String id = tmp;
                        String fullName;
                        try {
                            fullName = input.readLine();
                        } catch (IOException ex) {
                            output.println("Full name wrong value!");
                            break outerloop2;
                        }
                        String birthDay;
                        try {
                            birthDay = input.readLine();
                        } catch (IOException ex) {
                            output.println("Birthday wrong value!");
                            break outerloop2;
                        }
                        String address;
                        try {
                            address = input.readLine();
                        } catch (IOException ex) {
                            output.println("Address wrong value");
                            break outerloop2;
                        }
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");


                        User user;
                        try {
                            user = new User(Long.parseLong(id), fullName, dateFormatter.parse(birthDay), address);
                        } catch (ParseException | NumberFormatException ex) {
                            output.println("Lỗi: số chứng minh thư nhân dân phải là dạng số, định dạng ngày phải là yyyy-MM-dd");
                            break outerloop2;
                        }
                        users.add(user);
                        try {
                            inivalue = Long.parseLong(initialMoney);
                        } catch (NumberFormatException ex) {
                            output.println("Lỗi: giá trị khởi tạo phải là số");
                            break outerloop2;
                        }
                        try {
                            tmp = input.readLine();
                        } catch (IOException ex) {
                            output.println("Temp wrong value!");
                            break outerloop2;
                        }
                    }


                    User[] accHost = users.toArray(new User[1]);
                    if (accHost.length < 1) {
                        output.println("Phải có ít nhất một chủ tài khoản");
                        break;
                    }


                    for (User user : accHost) {
                        try {
                            if (User.checkExist(user.getID())) {
                                output.println("Người dùng " + user.getName() + " (" + user.getID() + ") đã có tài khoản");
                                break outerloop2;
                            }
                        } catch (ClassNotFoundException | SQLException ex) {
                            output.println("Error! User already have an account!");
                            break;
                        }
                    }

                    // create Account
                    long IDAccount;
                    long IDAccount2;
                    try {
                        objectOutput.writeObject(inivalue);
                        objectOutput.writeObject(accHost);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println(ex.getStackTrace());
                        break;
                    }
                    break;
                }
                case "delete account": {        // Need to change
                    String IDAccount;

                    try {
                        IDAccount = input.readLine();
                    } catch (IOException ex) {
                        output.println(ex.getMessage());
                        break;
                    }

                    if (!isNumeric(IDAccount)) {
                        output.println("Số tài khoản không hợp lệ!");
                        break;
                    }

                    try {
                        objectOutput.writeObject( Long.parseLong(IDAccount) );
                    } catch (Exception ex) {
                        output.println(ex.getMessage());
                        break;
                    }
                    break;
                }
                case "get detail information": {
                    String accID;
                    try {
                        accID = input.readLine();
                    } catch (IOException ex) {
                        output.println(ex.getMessage());
                        break;
                    }
                    if (!isNumeric(accID)) {
                        output.println("Tài khoản không hợp lệ");
                        break;
                    }
                    BankAccount acc = new BankAccount(Long.parseLong(accID));
                    output.println(acc.getDetails());
                    break;
                }
                case "recharge": {              // Need to change
                    String accID;
                    String value;
                    try {
                        accID = input.readLine();
                        value = input.readLine();
                    } catch (IOException ex) {
                        output.println(ex.getMessage());
                        break;
                    }
                    if (!isNumeric(accID) || !isNumeric(value)) {
                        output.println("Số tài khoản và giá trị phải là số");
                        break;
                    }
                    BankAccount acc = new BankAccount(Long.parseLong(accID));

                    try {
                        objectOutput.writeObject(Long.parseLong(value) );
                        objectOutput.writeObject(Long.parseLong(accID) );
                    } catch (Exception ex) {
                        output.println(ex.getMessage());
                        break;
                    }
                    break;
                }
                case "withdraw": {              // Need to change
                    String accID;
                    String value;
                    try {
                        accID = input.readLine();
                        value = input.readLine();
                    } catch (IOException ex) {
                        output.println(ex.getMessage());
                        break;
                    }
                    if (!isNumeric(accID) || !isNumeric(value)) {
                        output.println("Số tài khoản và giá trị phải là số");
                        break;
                    }
                    BankAccount acc = new BankAccount(Long.parseLong(accID));
                    try {
                        objectOutput.writeObject(Long.parseLong(value) );
                        objectOutput.writeObject(Long.parseLong(accID) );
                    } catch (Exception ex) {
                        output.println(ex.getMessage());
                        break;
                    }
                    break;
                }
                case "reset password": {        // Reset password root account so don't need to change
                    String accID;
                    String newPassword;
                    try {
                        accID = input.readLine();
                        newPassword = input.readLine();
                    } catch (IOException ex) {
                        output.println(ex.getMessage());
                        break;
                    }
                    try {
                        objectOutput.writeObject(Long.parseLong(accID));
                        objectOutput.writeObject(newPassword);
                    } catch (Exception ex) {
                        output.println(ex.getMessage());
                        break;
                    }
                    break;
                }
                case "exit": {
                    try {
                        input.close();
                        output.close();
                        objectOutput.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("IO error!");
                    }
                    break outerloop1;
                }
                default:
                    output.printf("The command is not included!");
                    break;
            }
        }
    }
}
