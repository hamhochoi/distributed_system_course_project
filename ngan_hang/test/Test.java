
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
public class Test {

    public static void main(String[] args) {
        Socket socket;
        PrintWriter output;
        BufferedReader input;
        try {
            socket = new Socket("localhost", 5555);
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
                    continue;
                }
                output.println(user);
                output.println(pass);
                String returnedString = input.readLine();
                System.out.print("      " + returnedString);
                if (returnedString.equals("Đăng nhập thành công")) {
                    if (user.equals("root")) {
                        rootThread(input, output);
                    } else {
                        userThread(input, output);
                    }
                    break;
                }
            } catch (IOException ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    static void rootThread(BufferedReader input, PrintWriter output){
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintStream console = System.out;
        while (true) {
            try {
                System.out.print(">>");
                String instruction = keyboard.readLine();
                output.println(instruction);
                switch (instruction) {
                    case "create account": {
                        console.print("   >>");
                        String tmp = keyboard.readLine();
                        while (tmp != null) {
                            output.println(tmp);
                            if(tmp.equals("out of information"))
                                break;
                            console.print("   >>");
                            tmp = keyboard.readLine();
                        }
                        console.println(input.readLine());
                        break;
                    }
                    case "delete account": {
                        console.print("   >>");
                        String id = keyboard.readLine();
                        output.println(id);
                        console.println(input.readLine());
                        break;
                    }
                    case "recharge": {
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
                        console.print("   >>");
                        String id = keyboard.readLine();
                        console.print("   >>");
                        String value = keyboard.readLine();
                        output.println(id);
                        output.println(value);
                        console.println(input.readLine());
                        break;
                    }
                    case "exit": {
                        return;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    static void userThread(BufferedReader input, PrintWriter output){
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintStream console = System.out;
        while (true) {
            try {
                System.out.println(">>");
                String instruction = keyboard.readLine();
                output.println(instruction);
                switch (instruction) {
                    case "get detail information": {
                        console.println(input.readLine());
                        break;
                    }
                    case "recharge": {
                        console.print("   >>");
                        String value = keyboard.readLine();
                        output.println(value);
                        console.println(input.readLine());
                        break;
                    }
                    case "withdraw": {
                        console.print("   >>");
                        String value = keyboard.readLine();
                        output.println(value);
                        console.println(input.readLine());
                        break;
                    }
                    case "exit":{
                        return;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
