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
  private final static List<String> commands = Arrays.asList("?", "connect", "url", "sendFile", "edit", "exit",
          "files", "open", "close", "disconnect", "search", "show", "rename", "def");

  public static void main(String[] args) throws IOException {
    try(Socket sock = new Socket("localhost", 1337);
      DataOutputStream dos=new DataOutputStream(sock.getOutputStream());
      DataInputStream dis=new DataInputStream(sock.getInputStream());
      Scanner sc = new Scanner(System.in)
    ){
      if (args.length > 0)  {
        for (String arg : args) {
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
        UI ui = new UI(sc);
        boolean running = true;
        while (running) {
          String commandString = ui.waitForCommand(commands);

          Command command = new BaseCommand(commands, dos, dis).createCommand(commandString);

          if (command != null) {
            command.beforeSend();
            command.send();
            command.afterSend();
          }

          if (commandString.equals("exit"))
            running = false;
        }
      }
    }
  }
}
