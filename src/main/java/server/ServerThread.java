package server;

import Editor.DBhandler;
import Editor.Editor;
import Editor.XMLhandler;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//import com.mysql.jdbc.StringUtils;

public class ServerThread implements Runnable {
  private final Socket sock;
  private static final int BUFFER_SIZE=1024;
  private DBhandler db = null;
  private XMLhandler xml = null;
  private Editor edit = null;

  ServerThread(Socket sock) {
      this.sock = sock;
  }

  @Override
  public void run() {

    try (DataInputStream dis = new DataInputStream(sock.getInputStream());
       DataOutputStream dos = new DataOutputStream(sock.getOutputStream())) {
      boolean running = true;
      while (running) {
        String command = dis.readUTF();
        int numberOfArguments = dis.readInt();
        List<String> arguments = new ArrayList<>();
        if (numberOfArguments > 0) {
          for (int i = 0; i < numberOfArguments; i++) {
            arguments.add(dis.readUTF());
          }
        }


        switch (command) {
          case "?":
            break;
          case "saveFile":
            //TODO debug, file sending freezes server and client
            for (String argument : arguments) {
              boolean done = saveFile(dis);
              if (done) {
                dos.writeUTF("File " + argument + " saved to server.");
              } else {
                dos.writeUTF("Failed to save file " + argument + ".");
              }
            }
            break;
          case "files":
            List<String> fileNames = getExistingFiles();
            dos.writeInt(fileNames.size());
            for (String fileName : fileNames) {
              dos.writeUTF(fileName);
            }
            break;
          case "connect":
            //TODO debug
            System.out.println(arguments.size());
            if (arguments.size()==4)  {
              db = new DBhandler(arguments.get(0), arguments.get(1), arguments.get(2), arguments.get(3));
              db.connectToDB();
              dos.writeUTF("Connected to database.");
            }
            else
              dos.writeUTF("Wrong number of arguments.");
            break;
          case "url":
            for (String argument : arguments) {
              copyURLtoFile(argument);
            }
            break;
          case "exit":
            running = false;
            break;
        }
              /*switch (command) {

                  case "def":
                      try {
                          db = new DBhandler("xmltosql","xmltosql","xmltosql","db4free.net");
                          db.connectToDB();
                          dos.writeUTF("Connected to database.");
                      } catch (SQLException e) {
                          dos.writeUTF("Failed to connect to database.");
                      }

                      try {
                          xml = new XMLhandler("biginfo.xml");
                          xml.saveFile();
                          dos.writeUTF("Opened XML document.");
                      } catch (JDOMException e) {
                          dos.writeUTF("Failed to open XML document. Invalid path or url.");
                      }
                      break;
                  case "edit":
                      dos.writeUTF(db.getTableNames().toString());
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
                      dos.writeUTF("Now in edit mode. Syntax <command>:<p1>;<p2>... . Type exit to close, ? for help. Work in progress, so expect bugs...");

                      sendDataToEdit(xml, db, table, dos);
                      while (true) {
                          if (dis.readBoolean()) {
                              String editCommand = dis.readUTF();
                              if (editCommand.equals("close"))    {
                                  break;
                              }
                              else if (!editCommand.equals("?") && editCommand.contains(";"))  {
                                  String function = editCommand.substring(0, editCommand.indexOf(":"));
                                  String[] parameters = editCommand.substring(editCommand.indexOf(":")).trim().split(";");

                                  if (function.equals("add") || function.equals("del")) {
                                      List<Integer> fails = editDatabase(function, parameters);
                                      if (function.equals("add"))
                                          dos.writeUTF("Added " + (parameters.length - fails.size()) + " columns. Failed to add: " + fails.toString() + " .");
                                      else if (function.equals("del"))
                                          dos.writeUTF("Deleted " + (parameters.length - fails.size()) + " columns. Failed to delete: " + fails.toString() + " .");
                                      sendDataToEdit(xml, db, table, dos);
                                  }
                              }
                          }

                      }

                      break;
              }*/
      }
    } catch (IOException | SQLException e) {
      e.printStackTrace();
    }
  }

/*  private void sendDataToEdit(XMLhandler xml, DBhandler sql, String table, DataOutputStream dos) throws SQLException, IOException {
    String header = xml.getXmlFileName() + ";" + table;
    dos.writeUTF(header);
    dos.writeInt(Math.max(xml.getColumns().size(), sql.getColumnNames(table).size()));
    for (int i = 0; i < Math.max(xml.getColumns().size(), sql.getColumnNames(table).size()); i++) {
      String line;
      if (sql.getColumnNames(table).size() <= i)
        line = xml.getColumns().get(i) + "; ";
      else if (xml.getColumns().size() <= i)
        line = " ;" + sql.getColumnNames(table).get(i);
      else
        line = xml.getColumns().get(i) + ";" + sql.getColumnNames(table).get(i);
      dos.writeUTF(line);
    }
  }*/

/*   private List<Integer> editDatabase(String function, String[] parameters) throws SQLException {
      //TODO test
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
  }*/

  private static List<String> getExistingFiles(){
    File directory=new File("xmlFiles");
    File[] fileArray=directory.listFiles();
    List<String> fileNames = new ArrayList<>();
    if (fileArray != null) {
      for (File file : fileArray) {
        fileNames.add(file.getName());
      }
    }
    return fileNames;
  }

  private static void copyURLtoFile(String urlName) throws IOException {
    String fileName=urlName.split("/")[urlName.split("/").length-1];
    URL url = new URL(urlName);
    File file = new File("xmlFiles\\"+fileName);
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

  private boolean saveFile(DataInputStream fromClient) throws IOException {
    String fileName = fromClient.readUTF();
    long fileSize = fromClient.readLong();

    if (fileSize>0) {
      File file = new File("xmlFiles\\"+fileName);
      file.createNewFile();
      try (FileOutputStream fos = new FileOutputStream(file)) {
        byte[] buffer = new byte[BUFFER_SIZE];
        int count;
        while ((count = fromClient.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1 && fileSize > 0) {
          fos.write(buffer, 0, count);
          fileSize -= count;
        }
      }
      return true;
    }
    return false;
  }
}

