package server;

import java.io.*;
import java.net.Socket;

/*
Võtab sisse ühe ühenduse ja selle ühenduse peal
võtab vastu käske ja tegeleb nendega
 */

public class HandleConnection implements Runnable {
    private final Socket sock;
    private static final int BUFFER_SIZE=1024;


    public HandleConnection(Socket sock) {
        this.sock = sock;
    }

    public void run() {

        try (DataInputStream dis = new DataInputStream(sock.getInputStream());
             DataOutputStream dos = new DataOutputStream(sock.getOutputStream())) {

            File file;
            if (dis.readUTF().equals("sending file")){
                file=receiveFile(dis);
            }
            else if(dis.readUTF().equals("sending URL")){

                //TODO convert URL to file
            }


            while (true){
                String command=receiveCommand(dis);
                //TODO Tuleks lisada erinevate käskudega tegelemine
            }



        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                sock.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String receiveCommand(DataInputStream dis) throws IOException {
        try {
            String command=dis.readUTF();
            return command;
        } catch (IOException e) {
            System.out.println("Error receiving command");
            e.printStackTrace();
            throw e;
        }
    }


    private static File receiveFile(DataInputStream dis) throws IOException {
        try {
            String fileName = dis.readUTF();
            long fileSize = dis.readLong();

            File file = new File(fileName);
            file.createNewFile();

            if (fileSize>0) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int count;
                    while ((count = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1 && fileSize > 0) {
                        fos.write(buffer, 0, count);
                        fileSize -= count;
                    }
                }
            }
            return file;
        }
        catch (Exception e){
            throw e;
        }
    }
}

