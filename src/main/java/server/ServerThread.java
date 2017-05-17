package server;

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
          case "kill":
            handleKill(dos, arguments);
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

          case "showTables":
            handleShowTables(dos, arguments);
            break;

          case "showAllTables":
            handleShowAllTables(dos);
            break;

          case "createSampleTable":
            handleCreateSampleTable(dos, arguments);
            break;

          case "open":
            handleOpen(dos, arguments);
            break;

          case "close":
            handleClose(dos, arguments);
            break;
          case "insert":
            handleInsert(dos, arguments);
            break;
          case "delete":
            handleDelete(dos, arguments);
            break;
          case "exit":
            accountManager.saveUsers();
            running = false;
            break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  private void handleKill(DataOutputStream toClient, List<String> arguments) throws IOException {
    if (arguments != null && arguments.size() == 1) {
      if (arguments.get(0).equals("admin"))  {
        toClient.writeUTF("You can't remove admin account");
      }
      else if (accountManager.getUsers().containsKey(arguments.get(0)))  {
        accountManager.getUsers().remove(arguments.get(0));
        toClient.writeUTF("User "+arguments.get(0)+" removed");
      }
      else  {
        toClient.writeUTF("No such user");
      }
    }
    else
      toClient.writeUTF("Wrong number of arguments");
  }

  private void handleDelete(DataOutputStream toClient, List<String> arguments) throws IOException {
    if (arguments!= null && arguments.size()>0) {
      for (String argument : arguments) {
        if (getExistingFiles().contains(argument)) {
          File fileToDelete = new File("xmlFiles"+File.separatorChar+argument);
          if (fileToDelete.delete())  {
            toClient.writeUTF("File "+argument+" deleted");
            openedXMLfiles.remove(argument);
          }
          else  {
            toClient.writeUTF("Failed to delete file "+argument);
          }
        }
        else {
          toClient.writeUTF("No such file "+argument);
        }
      }
    }
    else  {
      toClient.writeUTF("Wrong number of arguments");
    }
  }

  private void handleInsert(DataOutputStream dos, List<String> arguments) throws IOException, SQLException {
      //xmlfilename, table name, xmlColumnnames, dbColumnnames
      if (arguments.size()<4 || arguments.size()%2!=0){
          dos.writeUTF("Invalid amount of arguments, correct syntax is xmlfilename, tablename, xmlcolumnnames, dbcolumnnames");
      }

      if (openedXMLfiles.isEmpty() || !openedXMLfiles.containsKey(arguments.get(0))){
          dos.writeUTF("No such xml file opened");
          return;
      }
      
      XMLhandler xml=openedXMLfiles.get(arguments.remove(0));
      String tableName=arguments.remove(0);
      List<String> xmlColumnNames=arguments.subList(0, arguments.size()/2);
      String[] dbColumnNames = arguments.subList(arguments.size()/2, arguments.size()).toArray(new String[0]);
      
      if (!db.getTableNames().contains(tableName)){
          dos.writeUTF("No such table in database");
          return;
      }
      for (String xmlColumnName : xmlColumnNames) {
        if (!xml.getColumns().contains(xmlColumnName)){
            dos.writeUTF("No such column in given xml file.");
            return;}
      }
      List<String> existingColumns=db.getColumnNames(tableName);
      for (String dbColumnName : dbColumnNames) {
          if(!existingColumns.contains(dbColumnName)){
              dos.writeUTF("No such column in given table");
              return;
          }
      }

      for (int i = 0; i < xml.getNumberOfRows(); i++) {
          List<String> data=new ArrayList<>();
          for (String xmlColumnName : xmlColumnNames) {
              data.add(xml.getValue(xmlColumnName, i));
          }
          db.insertIntoDB(tableName, dbColumnNames, data.toArray(new String[0]));
      }
      dos.writeUTF("Inserting successful");
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
          dos.writeUTF("Guest account created");
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
    XMLscanner scan = new XMLscanner();
      if (arguments.size()==0)  {
          dos.writeUTF("Wrong number of arguments");
      }
      else if (arguments.get(0).equals("*"))  {
          for (String file : getExistingFiles()) {
              if (file.endsWith(".xml") && !openedXMLfiles.containsKey(file))  {
                //TODO kas scannimine tehakse siin või siis kui tabelit tehakse?
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

                Map<String, String> test = scan.startScan(openedXMLfiles.get(argument));
                System.out.println(test.size());
                test.forEach((key,value) -> System.out.println("Key: "+key+"  Value: "+value));
                dos.writeUTF("File opened.");
              }
              else
                  dos.writeUTF("No such file.");
          }
      }
  }

  private void handleDisconnect(DataOutputStream dos, List<String> arguments) throws IOException, SQLException {
    if (db == null) {
      dos.writeUTF("Not connected to database.");
    }
    else if (db.getDbName().equals(arguments.get(0))) {
      db.disconnect();
      db = null;
      dos.writeUTF("Disconnected from database.");
    }
  }

    private void handleShowTables(DataOutputStream dos, List<String> arguments) throws IOException, SQLException {
        if (arguments.size() == 0)  {
            dos.writeUTF("You must specify a table name.");
        } else if (db == null) {
            dos.writeUTF("Not connected to database.");
        } else {
            for (String argument : arguments) {
                List<String> columns = db.getColumnNames(argument);
                dos.writeUTF("Table " + argument + " contains columns: " + String.join(", ", columns));
            }
        }
    }

    private void handleShowAllTables(DataOutputStream dos) throws IOException, SQLException {
        if (db == null) {
            dos.writeUTF("Not connected to database.");
        } else {
            List<String> tables = db.getTableNames();
            dos.writeUTF(String.join(", ", tables));
        }
    }

    private void handleCreateSampleTable(DataOutputStream dos, List<String> arguments) throws IOException, SQLException {
      if (db == null) {
          dos.writeUTF("Not connected to database.");
      } else if (arguments.size() < 1) {
          dos.writeUTF("Please specify a name for sample table!");
      } else {
          db.createSampleTable(arguments.get(0));
          dos.writeUTF("Sample database " + arguments.get(0) + " created!");
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

  private void handleSearch(DataOutputStream dos, List<String> arguments) throws IOException, SQLException {
    List<String> result = new ArrayList<>();

    for (String argument : arguments) {
      for (Map.Entry<String, XMLhandler> entry : openedXMLfiles.entrySet()) {
        if (entry.getValue().containsWord(argument)) {
          result.add(entry.getKey());
        }
      }

      if (db!=null) {
        for (String table : db.getTableNames()) {
          for (String column : db.getColumnNames(table)) {
            if (column.contains(argument))  {
              result.add(table+"->"+column);
            }
          }
        }
      }
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
    File dir=new File("xmlFiles");
    if (!dir.exists()||!dir.isDirectory())
          dir.mkdir();
    File file = new File("xmlFiles"+File.separatorChar+fileName);
    try(InputStream in = url.openStream();
      FileOutputStream out = new FileOutputStream(file))  {
      byte[] buffer = new byte[BUFFER_SIZE];
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
      File dir=new File("xmlFiles");
      if (!dir.exists()||!dir.isDirectory())
          dir.mkdir();
      File file = new File("xmlFiles"+File.separatorChar+fileName);
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

