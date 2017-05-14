package client.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class CommandLogin extends  BaseCommand implements  Command{
  CommandLogin(DataOutputStream toServer, DataInputStream fromServer, List<String> args) {
    super(toServer, fromServer, args);
  }

  @Override
  public void beforeSend() {
    System.out.println("Logging in...");
  }

  @Override
  public void send() throws IOException {
    sendCommand("login", args);
  }

  @Override
  public void afterSend() throws IOException {
  }
}
