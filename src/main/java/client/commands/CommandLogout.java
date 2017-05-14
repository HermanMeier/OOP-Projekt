package client.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class CommandLogout extends BaseCommand implements Command{
  CommandLogout(DataOutputStream toServer, DataInputStream fromServer, List<String> args) {
    super(toServer, fromServer, args);
  }

  @Override
  public void beforeSend() {
    System.out.println("Logging out...");
  }

  @Override
  public void send() throws IOException {
    sendCommand("logout", null);
  }

  @Override
  public void afterSend() throws IOException {

  }
}
