package client.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class CommandExit extends BaseCommand implements Command {

  CommandExit(DataOutputStream toServer, DataInputStream fromServer, List<String> args) {
    super(toServer, fromServer, args);
  }

  @Override
  public void beforeSend() {
    System.out.println("Closing connection...");

  }

  @Override
  public void send() throws IOException {
    sendCommand("exit",null);
  }

  @Override
  public void afterSend() {

  }
}
