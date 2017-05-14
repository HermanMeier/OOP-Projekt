package client;

import client.commands.BaseCommand;
import client.commands.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Client {
  private List<String> commands; //= Arrays.asList("?", "connect", "url", "sendFile", "edit", "exit",
          //"files", "open", "close", "disconnect", "search", "show", "rename", "def");

  Client(List<String> commands) {
    this.commands = commands;
  }

  public void setCommands(List<String> commands) {
    this.commands = commands;
  }

  private String waitForCommand(List<String> commands, Scanner input)  {
    System.out.print(">> ");
    while (input.hasNextLine())    {
      String com = input.nextLine();
      if (com.trim().length() > 0 && commands.contains(com.split(" ")[0])) {
        return com;
      }
      else  {
        System.out.println("No such command. Try ? for help.");
        System.out.print(">> ");
      }
    }
    return null;
  }

  void run(String[] args) throws IOException {
    try(Socket sock = new Socket("localhost", 1337);
      DataOutputStream dos=new DataOutputStream(sock.getOutputStream());
      DataInputStream dis=new DataInputStream(sock.getInputStream());
      Scanner sc = new Scanner(System.in)
    ){
      if (args.length > 0)  {
        for (String arg : args) {
          List<String> commands = Arrays.asList("?", "connect", "url", "sendFile", "edit", "exit",
                        "files", "open", "close", "disconnect", "search", "show", "rename", "def");
          String commandString = arg.replace(";", " ");
          Command command = new BaseCommand(commands,dos,dis).createCommand(commandString);

          if (command != null)  {
            command.beforeSend();
            command.send();
            command.afterSend();
          }
        }
      }
      else {
        boolean running = true;
        while (running) {
          String commandString = waitForCommand(commands, sc);

          Command command = new BaseCommand(commands, dos, dis).createCommand(commandString);

          if (command != null) {
            command.beforeSend();
            command.send();
            command.afterSend();
          }

          if (commandString.split(" ")[0].equals("login")) {
            String ans = dis.readUTF();
            System.out.println(ans);
            if (ans.equals("Login successful")) {
              if (dis.readBoolean()) {
                //admin
                setCommands(Arrays.asList("?", "connect", "url", "sendFile",
                        "files", "open", "close", "disconnect", "search", "show", "rename", "logout"));
              } else {
                //quest
                setCommands(Arrays.asList("?", "url", "sendFile",
                        "files", "open", "close", "search", "show", "logout"));
              }
            }
          }

          if (commandString.equals("logout")) {
            setCommands(Arrays.asList("login", "signup", "exit", "?"));
          }

          if (commandString.equals("exit")) {
            running = false;
          }
        }
      }
    }
  }
}
