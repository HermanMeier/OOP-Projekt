package server;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/*
Ühendab serveriga ja saadab kõigepealt faili ja seejärel
hakkab saatma käske, millega server tegeleb
 */

public class Client {
    private final static int BUFFER_SIZE=1024;

    public static void main(String[] args) throws IOException {


        try(Socket sock = new Socket("localhost", 1337);
            DataOutputStream dos=new DataOutputStream(sock.getOutputStream());
            DataInputStream dis=new DataInputStream(sock.getInputStream())
        ){



            List<String> commands = Arrays.asList("?", "db", "xml", "edit", "exit");
            try (Scanner sc = new Scanner(System.in))   {
                UI filesender=new UI(Arrays.asList("?", "http", "file"), sc);
                while (true) {
                    System.out.println("Type http to send url to server or file to select file from your own computer");
                    String comm = filesender.waitForCommand();
                    if (comm.equals("http")){
                        System.out.println("Insert url");
                        dos.writeUTF("sending URL");
                        dos.writeUTF(sc.nextLine());
                        break;
                    }
                    else if(comm.equals("file")){
                        sendFile(sc.nextLine(), dos);
                        break;
                    }
                }



                UI ui = new UI(commands, sc);

                while (true) {
                    String command = ui.waitForCommand();
                    if (command!=null){
                        sendCommand(command, dos);
                    }

                }
            }
        }

        catch (Exception e){
            System.out.println("Fault in connecting to server");
        }
    }

    private static boolean sendCommand(String command, DataOutputStream dos){
        try {
            dos.writeUTF("sending command");
            dos.writeUTF(command);
        } catch (IOException e) {
            System.out.println("Couldn't send command: "+command);
        }

        return true;
    }


     private static boolean sendFile(String fileName, DataOutputStream dos){
        File file= new File(fileName);
        if (file.exists()) {
            System.out.println("Given file does not exist.");
            return false;
        }

        try(InputStream fis= new FileInputStream(file)){
                dos.writeUTF("sending file");
                dos.writeUTF(file.getName());
                dos.writeLong(file.length());

                ByteArrayOutputStream fileData=new ByteArrayOutputStream(BUFFER_SIZE);
                int nRead;
                byte[] data= new byte[BUFFER_SIZE];
                while((nRead= fis.read(data, 0, data.length))!=-1){
                    fileData.write(data, 0, nRead);
                }
                fileData.writeTo(dos);

        }
        catch (Exception e){
            System.out.println("Error in sending file");
            e.printStackTrace();
            return false;
        }


        return true;
     }
}
