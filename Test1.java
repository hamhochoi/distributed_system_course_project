package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Hung Nguyen Manh
 */
public class Test1 {

    public static void main(String[] args) {
        Socket socket;
        PrintWriter output;
        BufferedReader input;
        try {
            socket = new Socket("localhost", 5555);
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                String user;
                String pass;
                try {
                    System.out.print(">>");
                    user = keyboard.readLine();
                    System.out.print("   >>");
                    pass = keyboard.readLine();
                } catch (IOException ex) {
                    Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
                    continue;
                }
                output.println(user);
                output.println(pass);
                String returnedString = input.readLine();
                System.out.println(returnedString);
                if (returnedString.equals("Đăng nhập thành công")) {
                    if (user.equals("root")) {
                        rootThread(input, output);
                    } else {
                        userThread(input, output);
                    }
                    break;
                }
            } catch (IOException ex) {
                Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    static void rootThread(BufferedReader input, PrintWriter output) {
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintStream console = System.out;
        while (true) {
            try {
                System.out.print(">>");
                String instruction = keyboard.readLine();
                switch (instruction) {
                    case "create account": {
                        output.println(instruction);
                        console.print("   >>");
                        String tmp = keyboard.readLine();//nhận giá trị khởi tạo
                        while (tmp != null) {//nhập từng dòng với từng thông tin của chủ thẻ, bắt đầu là chủ chính
                            output.println(tmp);//thông tin của chủ thẻ bao gồm: số CMTND, họ và tên, ngày sinh (yyyy-MM-dd), địa chỉ
                            if (tmp.equals("out of information")) {
                                break;
                            }
                            console.print("   >>");
                            tmp = keyboard.readLine();
                        }
                        console.println(input.readLine());
                        break;
                    }
                    case "delete account": {
                        output.println(instruction);
                        console.print("   >>");
                        String id = keyboard.readLine();
                        output.println(id);
                        console.println(input.readLine());
                        break;
                    }
                    case "recharge": {
                        output.println(instruction);
                        console.print("   >>");
                        String id = keyboard.readLine();
                        console.print("   >>");
                        String value = keyboard.readLine();
                        output.println(id);
                        output.println(value);
                        console.println(input.readLine());
                        break;
                    }
                    case "withdraw": {
                        output.println(instruction);
                        console.print("   >>");
                        String id = keyboard.readLine();
                        console.print("   >>");
                        String value = keyboard.readLine();
                        output.println(id);
                        output.println(value);
                        console.println(input.readLine());
                        break;
                    }
                    case "reset password": {
                        output.println(instruction);
                        console.print("   >>");
                        output.println(keyboard.readLine());
                        console.println(input.readLine());
                        break;
                    }
                    case "get detail information": {
                        output.println(instruction);
                        console.print("   >>");
                        String id = keyboard.readLine();
                        output.println(id);
                        console.println(input.readLine());
                        break;
                    }
                    case "exit": {
                        return;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    static void userThread(BufferedReader input, PrintWriter output) {
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintStream console = System.out;
        while (true) {
            try {
                System.out.print(">>");
                String instruction = keyboard.readLine();
                switch (instruction) {
                    case "get detail information": {
                        output.println(instruction);
                        console.println(input.readLine());
                        break;
                    }
                    case "recharge": {
                        output.println(instruction);
                        console.print("   >>");
                        String value = keyboard.readLine();
                        output.println(value);
                        console.println(input.readLine());
                        break;
                    }
                    case "withdraw": {
                        output.println(instruction);
                        console.print("   >>");
                        String value = keyboard.readLine();
                        output.println(value);
                        console.println(input.readLine());
                        break;
                    }
                    case "reset password": {
                        output.println(instruction);
                        console.print("   >>");
                        output.println(keyboard.readLine());
                        console.println(input.readLine());
                        break;
                    }
                    case "exit": {
                        output.println(instruction);
                        return;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
