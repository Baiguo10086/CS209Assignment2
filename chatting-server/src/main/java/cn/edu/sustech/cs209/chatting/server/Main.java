package cn.edu.sustech.cs209.chatting.server;

import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.Status;
import cn.edu.sustech.cs209.chatting.common.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


public class Main {

    private static final int PORT = 9990;
    private static final HashMap<String, User> names = new HashMap<>();

    private static final HashMap<String,PrintWriter> name_link = new HashMap<>();
    private static final ArrayList<String> users = new ArrayList<>();

    static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        System.out.println("Starting server");
        logger.info("this");
        try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class Handler extends Thread {

        private String name;
        private Socket socket;
        private Logger logger = LoggerFactory.getLogger(Handler.class);
        private static HashSet<PrintWriter> writers = new HashSet<>();

        private InputStream input_stream;
        private ObjectInputStream ob_input_stream;
        private OutputStream output_stream;
        private ObjectOutputStream ob_output_stream;

        private BufferedReader  in;
        private PrintWriter out;

        public Handler(Socket socket) throws IOException {
            this.socket = socket;
        }

        public void run() {
            logger.info("Waiting user...");
            try {
//                input_stream = socket.getInputStream();
//                ob_input_stream = new ObjectInputStream(input_stream);
//                output_stream = socket.getOutputStream();
//                ob_output_stream = new ObjectOutputStream(output_stream);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(),true);

                while (true) {
                    name = in.readLine();
//                    System.out.println(name);
                    if (check_username(name)) {
                        name_link.put(name, out);
                        users.add(name);
                        out.println("OK");
                        break;
                    } else {
//                    ob_output_stream.writeObject(false);
                        out.println("choose another name");
//                    ob_output_stream.reset();
                    }
                }
                exit:
                while (socket.isConnected()) {
//                    Message input_msg = (Message) ob_input_stream.readObject();
                    while (true){
                    out.println("plz, send mail as [type,receiver,data]");
                    String inmsg = in.readLine();
                    String[] total = inmsg.split("\\s+");
//                    logger.info(name + "send msg: " + total[2] + " to "+total[1]+"type: " + total[0]);
                    System.out.println(total);
                    PrintWriter printWriter;
                    switch (total[0]) {
                        case "ls":
                            for (String n : users) {
                                if (!n.equals(name)) {
                                    out.println(n);
                                }
                            }
                            break;
                        case "private":
                            if (users.contains(total[1])) {
                                printWriter = name_link.get(total[1]);
                                printWriter.println(total[2]);
                            } else
                                out.println(total[1] + "not found");
                            break;
                        case "group":
                            break;
                        case "exit":
                            break exit;
                    }
                }
                }
            }catch (SocketException socketException){
                logger.error("socket exception for "+name);
            }catch (Exception e){
                logger.error("Exception in run () method for " + name);
            }finally {
                closeConnect();
            }
        }

        /**
         * @param name
         * @return false means not connecting,true means connecting
         */
        private synchronized boolean check_username(String name) {
            logger.info(name + "is trying to connect.");
            if (!name_link.containsKey(name)) {
                this.name = name;
                users.add(name);
                logger.info(this.name + "has been added in chat room.");
                return true;
            } else {
                logger.error(name + "is already connected.");
                return false;
            }
        }


        private synchronized void closeConnect()  {
            if (name != null) {
                names.remove(name);
                users.remove(name);
                logger.info("User: " + name + " has been removed!");
            }

            if (out != null){
                writers.remove(out);
                logger.info("Writer object: " + name + " has been removed!");
            }
            if (input_stream != null){
                try {
                    input_stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (output_stream != null){
                try {
                    output_stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
