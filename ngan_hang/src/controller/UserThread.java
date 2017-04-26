/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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

    Socket socket;

    public UserThread(Socket socket) {
        this.socket = socket;
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
        }
    }

    private String login() throws IOException, ClassNotFoundException, SQLException {
        String userID;
        String pass;
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter ouput = new PrintWriter(socket.getOutputStream(), true);
        while (true) {
            userID = input.readLine();
            System.out.println(userID);
            pass = input.readLine();
            System.out.println(pass);
            if (isNumeric(userID)) {
                if (User.checkPassword(Long.parseLong(userID), pass)) {
                    ouput.println("Đăng nhập thành công");
                    break;
                } else {
                    ouput.println("Người dùng hoặc mật khẩu không đúng");
                }
            } else if (User.checkPassword(userID, pass)) {
                ouput.println("Đăng nhập thành công");
                break;
            } else {
                ouput.println("Người dùng hoặc mật khẩu không đúng");
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

    private void userThread(BankAccount acc, long userID) {
        BufferedReader input;
        PrintWriter output;
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
            Logger.getLogger(UserThread.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        outerloop:
        while (true) {
            String instructionName;
            try {
                instructionName = input.readLine();
            } catch (IOException ex) {
                output.println(ex.getMessage());
                break;
            }
            switch (instructionName) {
                case "get detail information":
                    output.println(acc.getDetails());
                    break;
                case "recharge": {
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
                        acc.addMoney(Long.parseLong(value));
                    } catch (ClassNotFoundException | SQLException ex) {
                        output.println(ex.getMessage());
                        break;
                    }
                    output.println("Thành công");
                    break;
                }
                case "withdraw": {
                    String value;
                    try {
                        value = input.readLine();
                    } catch (IOException ex) {
                        output.println(ex.getMessage());
                        break;
                    }
                    try {
                        if (!acc.minusMoney(Long.parseLong(value))) {
                            output.println("Số tiền trong tài khoản không đủ");
                            break;
                        }
                    } catch (ClassNotFoundException | SQLException ex) {
                        output.println(ex.getMessage());
                        break;
                    }
                    output.println("Thành công");
                    break;
                }
                case "reset password": {
                    String newPassword;
                    try {
                        newPassword = input.readLine();
                    } catch (IOException ex) {
                        output.println(ex.getMessage());
                        break;
                    }
                    try {
                        User.setPassword(userID, newPassword);
                    } catch (ClassNotFoundException | SQLException ex) {
                        output.println(ex.getMessage());
                        break;
                    }
                    output.println("Thành công");
                    break;
                }
                case "exit":
                    break outerloop;
            }
        }
    }

    private void rootThread() {
        BufferedReader input = null;
        PrintWriter output = null;
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return;
        }
        outerloop1:
        while (true) {
            String instruction;
            try {
                instruction = input.readLine();
            } catch (IOException ex) {
                output.println(ex.getMessage());
                break;
            }
            outerloop2:
            switch (instruction) {
                case "create account": {
                    String initialMoney;
                    try {
                        initialMoney = input.readLine();
                    } catch (IOException ex) {
                        output.println(ex.getMessage());
                        break;
                    }
                    ArrayList<User> users = new ArrayList<>();
                    long inivalue = 0;
                    String tmp;
                    try {
                        tmp = input.readLine();
                    } catch (IOException ex) {
                        output.println(ex.getMessage());
                        break;
                    }
                    while (tmp != null) {
                        if (tmp.equals("out of information")) {
                            break;
                        }
                        String id = tmp;
                        String fullName;
                        try {
                            fullName = input.readLine();
                        } catch (IOException ex) {
                            output.println(ex.getMessage());
                            break outerloop2;
                        }
                        String birthDay;
                        try {
                            birthDay = input.readLine();
                        } catch (IOException ex) {
                            output.println(ex.getMessage());
                            break outerloop2;
                        }
                        String address;
                        try {
                            address = input.readLine();
                        } catch (IOException ex) {
                            output.println(ex.getMessage());
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
                            output.println(ex.getMessage());
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
                            output.println(ex.getMessage());
                            break;
                        }
                    }
                    long IDAccount;
                    try {
                        IDAccount = BankAccount.createAccount(inivalue, accHost);
                    } catch (ClassNotFoundException | SQLException ex) {
                        output.println(ex.getMessage());
                        break;
                    }
                    output.println("Đã tạo tài khoản thành công. Số tài khoản là: " + IDAccount);
                    break;
                }

                case "delete account": {
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
                        BankAccount.deleteAccount(Long.parseLong(IDAccount));
                    } catch (SQLException | ClassNotFoundException ex) {
                        output.println(ex.getMessage());
                        break;
                    }
                    output.println("Đã xóa tài khoản " + IDAccount + " thành công");
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
                case "recharge": {
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
                        acc.addMoney(Long.parseLong(value));
                    } catch (ClassNotFoundException | SQLException ex) {
                        output.println(ex.getMessage());
                        break;
                    }
                    output.println("Thành công");
                    break;
                }
                case "withdraw": {
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
                        acc.minusMoney(Long.parseLong(value));
                    } catch (ClassNotFoundException | SQLException ex) {
                        output.println(ex.getMessage());
                        break;
                    }
                    output.println("Thành công");
                    break;
                }
                case "reset password": {
                    String newPassword;
                    try {
                        newPassword = input.readLine();
                    } catch (IOException ex) {
                        output.println(ex.getMessage());
                        break;
                    }
                    try {
                        User.setPassword("root", newPassword);
                    } catch (ClassNotFoundException | SQLException ex) {
                        output.println(ex.getMessage());
                        break;
                    }
                    output.println("Thành công");
                    break;
                }
                case "exit": {
                    break outerloop1;
                }
            }
        }
    }
}
