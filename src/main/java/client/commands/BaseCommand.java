package client.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseCommand {
  List<String> commands;
  final DataOutputStream toServer;
  final DataInputStream fromServer;
  String command;
  List<String> args;
  private String beforeMessage;

  public BaseCommand(List<String> commands, DataOutputStream toServer, DataInputStream fromServer, String commandString) {
    this.toServer = toServer;
    this.fromServer = fromServer;
    this.commands = commands;
    String[] input = commandString.split(" ");
    if (input.length > 1) {
      args = new ArrayList<>(Arrays.asList(input));
      args.remove(0);
    }
    else {
      args = new ArrayList<>();
    }
    this.command = input[0];
  }

  public BaseCommand(List<String> commands, DataOutputStream toServer, DataInputStream fromServer, String command, List<String> args) {
    this.commands = commands;
    this.toServer = toServer;
    this.fromServer = fromServer;
    this.command = command;
    this.args = args;
  }

  public BaseCommand(DataOutputStream toServer, DataInputStream fromServer, String command, List<String> args, String message) {
    this.toServer = toServer;
    this.fromServer = fromServer;
    this.command = command;
    this.args = args;
    this.beforeMessage = message;
  }

  public void beforeSend() {
    System.out.println(beforeMessage);
  }

  public void send() throws IOException {
    sendCommand(command, args);
  }

  public void afterSend() throws IOException {
    System.out.println(fromServer.readUTF());
  }

  void sendCommand(String command, List<String> args) throws IOException {
    toServer.writeUTF(command);

    if (args != null) {
      toServer.writeInt(args.size());
      for (String arg : args) {
        toServer.writeUTF(arg);
      }
    }
    else
      toServer.writeInt(0);
  }

  public BaseCommand createCommand() {
    switch (command)  {
      case "?":
        return new CommandHelp(commands, toServer, fromServer, command, args);
      case "exit":
        return new CommandExit(toServer, fromServer, command, args, "Closing connection...");
      case "sendFile":
        return new CommandSendFile(toServer, fromServer, command, args, "Sending file(s)...");
      case "files":
        return new CommandFiles(toServer, fromServer, command, args, "Loading files...");
      case "connect":
        return new CommandConnectToDB(toServer, fromServer, command, args, "Connecting to database...");
      case "url":
        return new CommandURL(toServer, fromServer, command, args, "Downloading file...");
      case "close":
        return new CommandClose(toServer, fromServer, command, args, "Closing file(s)...");
      case "disconnect":
        return new CommandDisconnect(toServer, fromServer, command, args, "Disconnecting from database...");
      case "showTables":
        return new CommandShowTables(toServer, fromServer, command, args, "Showing columns from tables...");
      case "showTable":
        return new CommandShowTable(toServer, fromServer, command, args, "Showing all data from tables...");
      case "showAllTables":
        return new CommandShowAllTables(toServer, fromServer, command, args, "Showing all tables...");
      case "createSampleTable":
        return new CommandCreateSampleTable(toServer, fromServer, command, args, "Creating sample table...");
      case "open":
        return new CommandOpen(toServer, fromServer, command, args, "Opening file(s)...");
      case "search":
        return new CommandSearch(toServer, fromServer, command, args, "Searching...");
      case "show":
        return new CommandShow(toServer, fromServer, command, args, "Reading xml file...");
      case "rename":
        return new CommandRename(toServer, fromServer, command, args, "Renameing file...");
      case "login":
        return new CommandLogin(toServer, fromServer, command, args, "Logging in...");
      case "logout":
        return new CommandLogout(toServer, fromServer, command, args, "Logging out...");
      case "signup":
        return new CommandSignup(toServer, fromServer, command, args, "Creating account...");
      case "insert":
        return new CommandInsert(toServer, fromServer, command, args, "Inserting data...");
      case "delete":
        return new CommandDelete(toServer, fromServer, command, args, "Deleting file(s)...");
      case "kill":
        return new CommandKill(toServer, fromServer, command, args, "Removing user...");
      case "createTable":
        return new CommandCreateTableFromXML(toServer, fromServer, command, args, "Creating table...");
      default:
        return null;
    }
  }
}
