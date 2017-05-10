package client.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class CommandHelp extends BaseCommand implements Command {

  CommandHelp(List<String> commands, DataOutputStream toServer, DataInputStream fromServer) {
    super(commands, toServer, fromServer);
  }

  @Override
  public void beforeSend() {
    System.out.println("Valid commands:");
    for (String command : this.commands) {
      System.out.println(command);
    }
  }

  @Override
  public void send() throws IOException {
    sendCommand("?",null);
  }

  @Override
  public void afterSend() {

  }
}
