package client.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class CommandLogout extends BaseCommand {
  CommandLogout(DataOutputStream toServer, DataInputStream fromServer, String command, List<String> args, String message) {
    super(toServer, fromServer, command, args, message);
  }

  @Override
  public void afterSend() throws IOException {

  }
}
