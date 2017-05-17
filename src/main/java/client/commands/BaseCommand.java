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
  List<String> args;

  public BaseCommand(List<String> commands, DataOutputStream toServer, DataInputStream fromServer) {
    this.commands = commands;
    this.toServer = toServer;
    this.fromServer = fromServer;
    args = null;
  }

  BaseCommand(DataOutputStream toServer, DataInputStream fromServer, List<String> args) {
    this.toServer = toServer;
    this.fromServer = fromServer;
    this.args = args;
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

  public Command createCommand(String commandString) {
    String[] input = commandString.split(" ");
    if (input.length > 1) {
      args = new ArrayList<>(Arrays.asList(input));
      args.remove(0);
    }
    switch (input[0])  {
      case "?":
        return new CommandHelp(commands, toServer, fromServer);
      case "exit":
        return new CommandExit(toServer, fromServer, args);
      case "sendFile":
        return new CommandSendFile(toServer, fromServer, args);
      case "files":
        return new CommandFiles(toServer, fromServer, args);
      case "connect":
        return new CommandConnectToDB(toServer, fromServer, args);
      case "url":
        return new CommandURL(toServer, fromServer, args);
      case "close":
        return new CommandClose(toServer, fromServer, args);
      case "disconnect":
        return new CommandDisconnect(toServer, fromServer, args);
      case "showTables":
        return new CommandShowTables(toServer, fromServer, args);
      case "showAllTables":
        return new CommandShowAllTables(toServer, fromServer, args);
      case "createSampleTable":
        return new CommandCreateSampleTable(toServer, fromServer, args);
      case "open":
        return new CommandOpen(toServer, fromServer, args);
      case "search":
        return new CommandSearch(toServer, fromServer, args);
      case "show":
        return new CommandShow(toServer, fromServer, args);
      case "rename":
        return new CommandRename(toServer, fromServer, args);
      case "login":
        return new CommandLogin(toServer, fromServer, args);
      case "logout":
        return new CommandLogout(toServer, fromServer, args);
      case "signup":
        return new CommandSignup(toServer, fromServer, args);
      case "insert":
        return new CommandInsert(toServer, fromServer, args);
      case "delete":
        return new CommandDelete(toServer, fromServer, args);
      case "kill":
        return new CommandKill(toServer, fromServer, args);
      default:
        return null;
    }
  }
}
