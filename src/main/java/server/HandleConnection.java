package server;

import DBStuff.SQLWriter;
import DBStuff.XMLhandler;
import DBStuff.editDatabase;
import com.mysql.jdbc.StringUtils;
import org.jdom2.JDOMException;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HandleConnection implements Runnable {
    private final Socket sock;
    private static final int BUFFER_SIZE=1024;
    private SQLWriter db = null;
    private XMLhandler xml = null;
    private editDatabase edit = null;

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
                            String filename=url.split("/")[url.split("/").length-1]+".xml";
                            file = new File("xmlFiles\\"+filename);

                            copyURLtoFile(new URL(url), file);
                        }

                        if (!answer.equals("")&&file.exists()) {
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
                        String dbName = dis.readUTF();
                        String table = dis.readUTF();
                        String xmlName = dis.readUTF();

                        if (db == null || !db.getDbName().equals(dbName)) {
                            dos.writeBoolean(false);
                            dos.writeUTF("Not connected to database.");
                            break;
                        }
                        if (!db.getTableNames().contains(table)) {
                            dos.writeBoolean(false);
                            dos.writeUTF("No such table in database.");
                            break;
                        }
                        if (xml == null || !xml.getXmlFileName().equals(xmlName)) {
                            dos.writeBoolean(false);
                            dos.writeUTF("XML document not opened.");
                            break;
                        }
                        dos.writeBoolean(true);
                        dos.writeUTF("Now in edit mode. Type exit to close, ? for help. Work in progress, so expect bugs...");

                        sendDataToEdit(xml, db, table, dos);
                        boolean inEdit = true;
                        while (inEdit) {
                            String editCommand = dis.readUTF();
                            String function = editCommand.substring(0, editCommand.indexOf(":"));
                            String[] parameters = editCommand.substring(editCommand.indexOf(":")).trim().split(",");

                            if (function.equals("close")) {
                                inEdit = false;
                            } else if (function.equals("add") || function.equals("del")) {
                                List<Integer> fails = editDatabase(function, parameters);
                                if (function.equals("add"))
                                    dos.writeUTF("Added " + (parameters.length - fails.size()) + " columns. Failed to add: " + fails.toString() + " .");
                                else if (function.equals("del"))
                                    dos.writeUTF("Deleted " + (parameters.length - fails.size()) + " columns. Failed to delete: " + fails.toString() + " .");
                                    dos.writeInt(fails.size());
                                if (fails.size() == 0)
                                    sendDataToEdit(xml, db, table, dos);
                            }
                        }

                        break;
                    case "existingfiles":
                        List<String> fileNames=getExistingFiles();
                        dos.writeInt(fileNames.size());
                        for (String fileName : fileNames) {
                            dos.writeUTF(fileName);
                        }

                        if (dis.readBoolean()){
                            try {
                                xml = new XMLhandler(dis.readUTF());
                                xml.openXML();
                                dos.writeUTF("Opened XML document.");
                            } catch (JDOMException e) {
                                e.printStackTrace();
                                dos.writeUTF("Failed to open XML document.");
                            }
                        }
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

    private static void sendDataToEdit(XMLhandler xml, SQLWriter sql, String table, DataOutputStream dos) throws SQLException, IOException {
        dos.writeUTF("XML: " + xml.getXmlFileName() + "\t" + "SQL table: " + table);
        dos.writeInt(Math.max(xml.getColumns().size(), sql.getColumnNames(table).size()));
        for (int i = 0; i < Math.max(xml.getColumns().size(), sql.getColumnNames(table).size()); i++) {
            String line;
            if (sql.getColumnNames(table).size() <= i)
                    line =xml.getColumns().get(i) + "\t" + "";
            else if (xml.getColumns().size() <= i)
                    line = "" + "\t" + sql.getColumnNames(table).get(i);
            else
                line = xml.getColumns().get(i) + "\t" + sql.getColumnNames(table).get(i);
            dos.writeUTF(line);
        }
    }

     private List<Integer> editDatabase(String function, String[] parameters) throws SQLException {
        List<Integer> fails = null;
        for (int i = 0; i < parameters.length; i++) {
            if (!StringUtils.isStrictlyNumeric(parameters[i])) {
                fails.add(i);
                } else {
                    switch (function)   {
                        case "add":
                                edit.addColumnToDatabase(Integer.parseInt(parameters[i]));
                                break;
                        case "del":
                                edit.deleteColumn(Integer.parseInt(parameters[i]));
                                break;
                    }
                }
            }
       return fails;
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

        File file = new File("xmlFiles\\"+fileName);
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

    private static List<String> getExistingFiles(){
        File directory=new File("xmlFiles");
        File[] fileArray=directory.listFiles();
        List<String> fileNames=new ArrayList();
        for (File file : fileArray) {
            fileNames.add(file.getName());
        }
        return fileNames;
    }

    private static void copyURLtoFile(URL url, File file) throws IOException{
        try(InputStream in = url.openStream();
            FileOutputStream out = new FileOutputStream(file))  {
            byte[] buffer = new byte[1024];
                int count;
                while ((count = in.read(buffer)) > 0) {
                    out.write(buffer, 0, count);
                }
                out.flush();
            }
        }
}

