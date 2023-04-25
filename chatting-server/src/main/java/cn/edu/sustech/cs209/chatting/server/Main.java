package cn.edu.sustech.cs209.chatting.server;

import cn.edu.sustech.cs209.chatting.common.Message;

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
    private static final int PORT = 8899;
    private static final HashMap<String , User> names = new HashMap<>();
    private static final ArrayList<User> users = new ArrayList<>();

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
        private static HashSet<ObjectOutputStream> writers = new HashSet<>();
        User user;

        private InputStream input_stream;
        private ObjectInputStream ob_input_stream;
        private OutputStream output_stream;
        private ObjectOutputStream ob_output_stream;

        public Handler (Socket socket) throws IOException{
            this.socket = socket;
        }
        @Override
        public void run (){
            logger.info("Waiting user...");
            try{
                input_stream = socket.getInputStream();
                ob_input_stream = new ObjectInputStream(input_stream);
                output_stream = socket.getOutputStream();
                ob_output_stream = new ObjectOutputStream(output_stream);

                Message mes = (Message) ob_input_stream.readObject();
                if (check_username(mes)){
                    writers.add(ob_output_stream);
                }else {

                };


            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         *
         * @param message
         * @return false means not connecting,true means connecting
         */
        private synchronized boolean check_username(Message message){
            String name = message.getSentBy();
            logger.info(name + "is trying to connect.");
            if (!names.containsKey(name)){
                this.name = name;
                user = new User();
                user.setName(name);
                user.setStatus(Status.ONlINE);
                users.add(user);
                names.put(name,user);
                logger.info(this.name + "has been added in chat room.");
                return false;
            }else {
                logger.error(name + "is already connected.");
                return true;
            }
        }
    }
}
