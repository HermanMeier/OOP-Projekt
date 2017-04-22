package server;

import DBStuff.SQLWriter;
import DBStuff.XMLhandler;
import org.jdom2.JDOMException;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/*
Võtab sisse ühe ühenduse ja selle ühenduse peal
võtab vastu käske ja tegeleb nendega
 */

public class HandleConnection implements Runnable {
    private final Socket sock;
    private static final int BUFFER_SIZE=1024;
    private static final List<String> commands = Arrays.asList("?", "db", "xml", "edit", "exit");
    private SQLWriter sql = null;
    private XMLhandler xml = null;



    public HandleConnection(Socket sock) {
        this.sock = sock;
    }

    public void run() {

        try (DataInputStream dis = new DataInputStream(sock.getInputStream());
             DataOutputStream dos = new DataOutputStream(sock.getOutputStream())) {

/*            File file;
            if (dis.readUTF().equals("sending file")){
                file=receiveFile(dis);
            }
            else if(dis.readUTF().equals("sending URL")){

                //TODO convert URL to file
            }*/


            while (true){
                String command=receiveCommand(dis);

                switch (command) {
                    case "?":
                        dos.writeBoolean(true);
                        break;
                    case "db":
                        dos.writeBoolean(true);

                        String[] DBinfo = new String[4];
                        for (int i = 0; i < DBinfo.length; i++) {
                            DBinfo[i] = dis.readUTF();
                        }

                        try {
                            sql = new SQLWriter(DBinfo[0], DBinfo[1], DBinfo[2], DBinfo[3]);
                            sql.connectToDB();
                            dos.writeUTF("Connected to database.");
                        } catch (SQLException e) {
                            dos.writeUTF("Failed to connect to database.");
                        }

                        break;
                    case "xml":
                        dos.writeBoolean(true);

                        File file = null;
                        String answer = dis.readUTF();
                        if (answer.equals("sending file")){
                            file=receiveFile(dis);
                        }
                        else if(answer.equals("sending URL")){
                            //TODO convert URL to file
                        }

                        if (!answer.equals(""))  {
                            try {
                                xml = new XMLhandler(file.getName());
                                xml.openXML();
                                dos.writeUTF("Opened XML document.");
                            } catch (JDOMException e) {
                                dos.writeUTF("Filed to open XML document.");
                            }
                        }

                        break;
                    case "edit":
                        dos.writeBoolean(true);

                        String db = dis.readUTF();
                        String table = dis.readUTF();
                        String xmlName = dis.readUTF();

                        if (!sql.getDbHost().equals(db)) {
                            dos.writeBoolean(false);
                            dos.writeUTF("Not connected to database.");
                            break;
                        }
                        if (!sql.getTableNames().contains(table))   {
                            dos.writeBoolean(false);
                            dos.writeUTF("No such table in database.");
                            break;
                        }
                        if (!xml.getXmlFileName().equals(xmlName)) {
                            dos.writeBoolean(false);
                            dos.writeUTF("XML document not opened.");
                            break;
                        }
                        dos.writeBoolean(true);
                        dos.writeUTF("Now in edit mode. Type exit to close.");

                        //TODO vaja see edit mode valmis teha, server peaks kasutama editDatabase klassi. xml ja ab sisu saatmiseks on osa koodi ui klassi commentis olemas
                        break;
                    case "exit":
                        dos.writeBoolean(true);
                        break;
                    default:
                        dos.writeBoolean(false);
                }
            }



        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            e.printStackTrace();
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

