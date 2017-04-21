package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException{

        try(ServerSocket ss = new ServerSocket(1337);){

            while (true) {
                Socket sock = ss.accept();
                Thread thread = new Thread(new HandleConnection(sock));
                thread.start();
            }
        }
    }
}
