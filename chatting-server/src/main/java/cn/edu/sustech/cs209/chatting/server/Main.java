package cn.edu.sustech.cs209.chatting.server;

import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.Status;
import cn.edu.sustech.cs209.chatting.common.User;
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

    private static final int PORT = 8899;
    private static final HashMap<String, User> names = new HashMap<>();
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

        public Handler(Socket socket) throws IOException {
            this.socket = socket;
        }

        public void run() {
            logger.info("Waiting user...");
            try {
                input_stream = socket.getInputStream();
                ob_input_stream = new ObjectInputStream(input_stream);
                output_stream = socket.getOutputStream();
                ob_output_stream = new ObjectOutputStream(output_stream);

                Message mes = (Message) ob_input_stream.readObject();
                if (check_username(mes)) {
                    writers.add(ob_output_stream);
                } else {
                    ob_output_stream.writeObject(false);
                    ob_output_stream.reset();
                }

                while (socket.isConnected()) {

                    Message input_msg = (Message) ob_input_stream.readObject();
                    if (input_msg != null) {
                        logger.info(input_msg.getSentBy() + "send msg" + input_msg.getData());
                        for (ObjectOutputStream writer : writers) {
                            input_msg.setList(names);
                            input_msg.setUsers(users);
                            writer.writeObject(input_msg);
                            writer.reset();
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
         * @param message
         * @return false means not connecting,true means connecting
         */
        private synchronized boolean check_username(Message message) {
            String name = message.getSentBy();
            logger.info(name + "is trying to connect.");
            if (!names.containsKey(name)) {
                this.name = name;
                user = new User();
                user.setName(name);
                user.setStatus(Status.ONlINE);
                users.add(user);
                names.put(name, user);
                logger.info(this.name + "has been added in chat room.");
                return false;
            } else {
                logger.error(name + "is already connected.");
                return true;
            }
        }

        private synchronized void closeConnect()  {
            logger.debug("closeConnections() method Enter");
            logger.info("HashMap names:" + names.size() + " writers:" + writers.size() + " usersList size:" + users.size());
            if (name != null) {
                names.remove(name);
                logger.info("User: " + name + " has been removed!");
            }
            if (user != null){
                users.remove(user);
                logger.info("User object: " + user + " has been removed!");
            }
            if (ob_output_stream != null){
                writers.remove(ob_output_stream);
                logger.info("Writer object: " + user + " has been removed!");
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
            if (ob_input_stream != null){
                try {
                    ob_input_stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            logger.info("HashMap names:" + names.size() + " writers:" + writers.size() + " usersList size:" + users.size());
            logger.debug("closeConnections() method Exit");
        }

    }
}
