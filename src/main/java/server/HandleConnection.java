package server;

import DBStuff.SQLWriter;
import DBStuff.XMLhandler;
import org.apache.commons.io.FileUtils;
import org.jdom2.JDOMException;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLException;

public class HandleConnection implements Runnable {
    private final Socket sock;
    private static final int BUFFER_SIZE=1024;
    private SQLWriter db = null;
    private XMLhandler xml = null;

    HandleConnection(Socket sock) {
        this.sock = sock;
    }

    @Override
    public void run() {

        try (DataInputStream dis = new DataInputStream(sock.getInputStream());
             DataOutputStream dos = new DataOutputStream(sock.getOutputStream())) {
            boolean running =true;
            while (running) {
                String command = receiveCommand(dis);

                switch (command) {
                    case "?":
                        break;
                    case "db":
                        String[] DBinfo = new String[4];
                        for (int i = 0; i < DBinfo.length; i++) {
                            DBinfo[i] = dis.readUTF();
                        }

                        try {
                            db = new SQLWriter(DBinfo[0], DBinfo[1], DBinfo[2], DBinfo[3]);
                            db.connectToDB();
                            dos.writeUTF("Connected to database.");
                        } catch (SQLException e) {
                            dos.writeUTF("Failed to connect to database.");
                        }

                        break;
                    case "xml":
                        File file = null;
                        String answer = dis.readUTF();
                        if (answer.equals("file")) {
                            if (dis.readBoolean()) {
                                file = receiveFile(dis);
                                dos.writeUTF("File received.");
                            } else
                                break;
                        } else if (answer.equals("sending URL")) {
                            String url=dis.readUTF();
                            String filename=url.split("/")[url.split("/").length-1];
                            file = new File(filename);

                            FileUtils.copyURLToFile(new URL(url), file);
                            dos.writeUTF("File received.");
                            //TODO convert URL to file
                        }

                        if (!answer.equals("")) {
                            try {
                                xml = new XMLhandler(file.getName());
                                xml.openXML();
                                dos.writeUTF("Opened XML document.");
                            } catch (JDOMException e) {
                                dos.writeUTF("Failed to open XML document. Invalid path or url.");
                            }
                        }

                        break;
                    case "edit":
                        String db = dis.readUTF();
                        String table = dis.readUTF();
                        String xmlName = dis.readUTF();

                        if (!this.db.getDbHost().equals(db)) {
                            dos.writeBoolean(false);
                            dos.writeUTF("Not connected to database.");
                            break;
                        }
                        if (!this.db.getTableNames().contains(table)) {
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
                        running=false;
                        break;
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static String receiveCommand(DataInputStream dis) throws IOException {
        try {
            return dis.readUTF();
        } catch (IOException e) {
            System.out.println("Error receiving command");
            e.printStackTrace();
            throw e;
        }
    }


    private static File receiveFile(DataInputStream dis) throws IOException {
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
}

