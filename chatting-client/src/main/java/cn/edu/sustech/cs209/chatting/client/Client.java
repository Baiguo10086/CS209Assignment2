package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.Message;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.IO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    //    private static Socket socket;
    public String hostname;

    public int port;

    public static String username;

    private static final int PORT = 9990;
    private InputStream input_stream;
    private ObjectInputStream ob_input_stream;
    private OutputStream output_stream;
    private ObjectOutputStream ob_output_stream;


    public Client(String hostname, int prot, String username) {
        this.hostname = hostname;
        this.port = prot;
        this.username = username;
    }

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", PORT);
            InputStreamReader input_stream =  new InputStreamReader(socket.getInputStream());
            OutputStream output_stream = socket.getOutputStream();
            BufferedReader input = new BufferedReader(input_stream);
            PrintWriter output = new PrintWriter(output_stream, true);
            System.out.println("client start");
            new Thread(() -> {
                String response;
                try{
                    while ((response = input.readLine()) != null) {
                        System.out.println(response);
                    }
                }catch (IOException e){
                    System.out.println("Error of connect to server");
                }
            }).start();
            System.out.println("plz,input name:");
            String inmes;
            BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
            while (((inmes = scanner.readLine())!= null)){
                output.println(inmes);
            }
//            while (true){
//                username = scanner.next();
//                output.println(username);
//                if (input.nextLine().equals("OK")) {
//                    break;
//                }
//            }
//            System.out.println("begin");
//            while (true) {
//                String type = scanner.next();
//                String send_to = scanner.next();
//                String send_by = username;
//                String data = scanner.next();
//
//                String msg = send_to + " " + send_by + " " + type + " " + data;
//                output.println(msg);
//            }
        } catch (Exception e) {
            System.out.println();
        }
    }

//    public static void connect() throws IOException {
//        Message createMessage = new Message();
//        createMessage.set(username);
//        createMessage.setType(CONNECTED);
//        createMessage.setMsg(HASCONNECTED);
//        createMessage.setPicture(picture);
//        oos.writeObject(createMessage);
//    }
}
