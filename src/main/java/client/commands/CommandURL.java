package client.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class CommandURL extends BaseCommand implements Command {
  CommandURL(DataOutputStream toServer, DataInputStream fromServer, List<String> args) {
    super(toServer, fromServer, args);
  }

  @Override
  public void beforeSend() {
    System.out.println("Sending url...");
  }

  @Override
  public void send() throws IOException {
    sendCommand("url", args);
  }

  @Override
  public void afterSend() throws IOException {
    for (String arg : args) {
      System.out.println(fromServer.readUTF());
    }
  }
}
