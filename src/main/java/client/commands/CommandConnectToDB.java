package client.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class CommandConnectToDB extends BaseCommand implements Command{
  CommandConnectToDB(DataOutputStream toServer, DataInputStream fromServer, List<String> args) {
    super(toServer, fromServer, args);
  }

  @Override
  public void beforeSend() {
    System.out.println("Connecting to database...");
  }

  @Override
  public void send() throws IOException {
    sendCommand("connect", args);
  }

  @Override
  public void afterSend() throws IOException {
    System.out.println(fromServer.readUTF());
  }
}
