package client.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class CommandKill extends BaseCommand implements  Command {
  CommandKill(DataOutputStream toServer, DataInputStream fromServer, List<String> args) {
    super(toServer, fromServer, args);
  }

  @Override
  public void beforeSend() {
    System.out.println("Removeing user...");
  }

  @Override
  public void send() throws IOException {
    sendCommand("kill", args);
  }

  @Override
  public void afterSend() throws IOException {
    System.out.println(fromServer.readUTF());
  }
}
