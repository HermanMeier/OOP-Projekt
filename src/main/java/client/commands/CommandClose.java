package client.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class CommandClose extends BaseCommand implements Command {
  CommandClose(DataOutputStream toServer, DataInputStream fromServer, List<String> args) {
    super(toServer, fromServer, args);
  }

  @Override
  public void beforeSend() {
    System.out.println("Closing file(s)...");
  }

  @Override
  public void send() throws IOException {
    sendCommand("close", args);
  }

  @Override
  public void afterSend() throws IOException {
    for (String arg : args) {
      System.out.println(fromServer.readUTF());
    }
  }
}
