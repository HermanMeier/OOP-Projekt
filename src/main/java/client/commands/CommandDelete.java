package client.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class CommandDelete extends BaseCommand implements Command {
  CommandDelete(DataOutputStream toServer, DataInputStream fromServer, List<String> args) {
    super(toServer, fromServer, args);
  }

  @Override
  public void beforeSend() {
    System.out.println("Deleting file(s)...");
  }

  @Override
  public void send() throws IOException {
    sendCommand("delete", args);
  }

  @Override
  public void afterSend() throws IOException {
    System.out.println(fromServer.readUTF());
  }
}
