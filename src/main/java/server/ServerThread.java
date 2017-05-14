package server;

import editor.DBhandler;
import editor.XMLhandler;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ServerThread implements Runnable {
  private final Socket sock;
  private static final int BUFFER_SIZE=1024;
  private DBhandler db = null;
  private Map<String, XMLhandler> openedXMLfiles = new HashMap<>();
  private final File users = new File(String.valueOf(Paths.get("src", "main", "resources", "users.dat")));
  private final AccountManager accountManager = new AccountManager(users);

  ServerThread(Socket sock) throws Exception {
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
          case "logout":
            accountManager.saveUsers();
            System.out.println("Logout at "+ LocalDateTime.now());
            break;
          case "signup":
            handleSignup(dos, arguments);
            break;
          case "login":
            handleLogin(dos, arguments);
            break;

          case "rename":
            rename(dos, arguments);
            break;

          case "show":
            if (arguments.size()==0)  {
              dos.writeInt(-1);
            }
            for (String argument : arguments) {
              if (openedXMLfiles.containsKey(argument)) {
                showXML(openedXMLfiles.get(argument), dos);
              }
              else dos.writeInt(-1);
            }
            break;

          case "search":
            handleSearch(dos, arguments);
            break;

          case "sendFile":
            handleSendFile(dos, dis, arguments);
            break;

          case "files":
            List<String> fileNames = getExistingFiles();
            dos.writeInt(fileNames.size());
            for (String fileName : fileNames) {
              dos.writeUTF(fileName);
            }
            break;

          case "connect":
            handleConnect(dos, arguments);
            break;

          case "url":
            handleUrl(dos, arguments);
            break;

          case "disconnect":
            handleDisconnect(dos, arguments);
            break;

          case "open":
            handleOpen(dos, arguments);
            break;

          case "close":
            handleClose(dos, arguments);
            break;

          case "exit":
            accountManager.saveUsers();
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
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void handleLogin(DataOutputStream dos, List<String> arguments) throws Exception {
      if (arguments.size() != 2)  {
          dos.writeUTF("Wrong number of arguments");
      }
      else if (accountManager.logIn(arguments.get(0), arguments.get(1)))  {
          dos.writeUTF("Login successful");
          dos.writeBoolean(arguments.get(0).equals("admin"));
          accountManager.saveUsers();
      }
      else  {
          dos.writeUTF("Failed to login");
      }
  }

  private void handleSignup(DataOutputStream dos, List<String> arguments) throws Exception {
      if (arguments.size() != 2)  {
          dos.writeUTF("Wrong number of arguments");
      }
      else if (accountManager.signUp(arguments.get(0), arguments.get(1)))  {
          dos.writeUTF("Quest account created");
      }
      else  {
          dos.writeUTF("Username taken");
      }
  }

  private void handleClose(DataOutputStream dos, List<String> arguments) throws IOException {
      if (arguments.size()==0)  {
          dos.writeUTF("Wrong number of arguments");
      }
      else if (arguments.get(0).equals("*")) {
          openedXMLfiles.clear();
          dos.writeUTF("All files closed.");
      }
      else {
          for (String argument : arguments) {
              if (!openedXMLfiles.containsKey(argument)) {
                  dos.writeUTF("File "+argument+" not open.");
              }
              else {
                  openedXMLfiles.remove(argument);
                  dos.writeUTF("File "+argument+" closed.");
              }
          }
      }
  }

  private void handleOpen(DataOutputStream dos, List<String> arguments) throws Exception {
      if (arguments.size()==0)  {
          dos.writeUTF("Wrong number of arguments");
      }
      else if (arguments.get(0).equals("*"))  {
          for (String file : getExistingFiles()) {
              if (file.endsWith(".xml") && !openedXMLfiles.containsKey(file))  {
                  openedXMLfiles.put(file, new XMLhandler(file));
                  openedXMLfiles.get(file).openXML();
                  openedXMLfiles.get(file).saveWords();
              }
          }
          dos.writeUTF("All files opened.");
      }
      else {
          for (String argument : arguments) {
              if (openedXMLfiles.containsKey(argument))  {
                  dos.writeUTF("File already opened.");
              }
              else if (getExistingFiles().contains(argument)) {
                  openedXMLfiles.put(argument, new XMLhandler(argument));
                  openedXMLfiles.get(argument).openXML();
                  openedXMLfiles.get(argument).saveWords();
                  dos.writeUTF("File opened.");
              }
              else
                  dos.writeUTF("No such file.");
          }
      }
  }

  private void handleDisconnect(DataOutputStream dos, List<String> arguments) throws IOException {
    if (db == null) {
      dos.writeUTF("Not connected to database.");
    }
    else if (db.getDbName().equals(arguments.get(0))) {
      db = null;
      dos.writeUTF("Disconnected from database.");
    }
  }

  private  void handleUrl(DataOutputStream dos, List<String> arguments) throws IOException {
    if (arguments.size() == 0)  {
      dos.writeUTF("Wrong number of arguments.");
    }
    else  {
      for (String argument : arguments) {
        copyURLtoFile(argument);
        dos.writeUTF("File "+argument+" downloaded.");
      }
    }
  }

  private void handleConnect(DataOutputStream dos, List<String> arguments) throws SQLException, IOException {
    if (arguments.size()==4)  {
      db = new DBhandler(arguments.get(0), arguments.get(1), arguments.get(2), arguments.get(3));
      db.connectToDB();
      dos.writeUTF("Connected to database.");
    }
    else
      dos.writeUTF("Wrong number of arguments.");
  }

  private void handleSendFile(DataOutputStream dos, DataInputStream dis, List<String> arguments) throws IOException {
    if (arguments.size() == 0) {
      dos.writeUTF("Wrong number of arguments.");
    }
    else {
      for (String argument : arguments) {
        boolean done = saveFile(dis);
        if (done) {
          dos.writeUTF("File " + argument + " saved to server.");
        } else {
          dos.writeUTF("Failed to save file " + argument + ".");
        }
      }
    }
  }

  private void handleSearch(DataOutputStream dos, List<String> arguments) throws IOException {
    List<String> result = new ArrayList<>();
    for (Map.Entry<String, XMLhandler> stringXMLhandlerEntry : openedXMLfiles.entrySet()) {
      boolean containsAll = true;
      for (String argument : arguments) {
        if (!stringXMLhandlerEntry.getValue().containsWord(argument)) {
          containsAll = false;
          break;
        }
      }
      if (containsAll)
        result.add(stringXMLhandlerEntry.getKey());
    }
    dos.writeInt(result.size());
    for (String s : result) {
      dos.writeUTF(s);
    }
  }

  private void rename(DataOutputStream dos, List<String> arguments) throws IOException {
    if (arguments.size() != 2)  {
      dos.writeUTF("Wrong number of arguments");
    }
    else if (openedXMLfiles.containsKey(arguments.get(0)))
      dos.writeUTF("Close file before renaming");
    else if (getExistingFiles().contains(arguments.get(0)))  {
      Path oldPath = Paths.get("xmlFiles", arguments.get(0));
      Path newPath = Paths.get("xmlFiles", arguments.get(1));
      File old = new File(String.valueOf(oldPath));
      File _new = new File(String.valueOf(newPath));
      old.renameTo(_new);
      dos.writeUTF("Renamed to "+arguments.get(1));
    }
    else  {
      dos.writeUTF("No such file.");
    }
  }

  private void showXML(XMLhandler xml, DataOutputStream toClient) throws IOException {
    if (xml.getColumns()==null) {
      toClient.writeInt(0);
    }
    else
      toClient.writeInt(xml.getColumns().size());
    for (String c : xml.getColumns()) {
      toClient.writeUTF(c);
    }
  }

  private void sendDataToEdit(XMLhandler xml, DBhandler sql, String table, DataOutputStream dos) throws SQLException, IOException {
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
  }

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
    String fileName=urlName.split("/")[urlName.split("/").length-1]+".xml";
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

