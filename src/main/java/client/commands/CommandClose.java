package client.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

class CommandClose extends BaseCommand {
  CommandClose(DataOutputStream toServer, DataInputStream fromServer, String command, List<String> args, String message) {
    super(toServer, fromServer, command, args, message);
  }

  @Override
  public void afterSend() throws IOException {
    if (args.size() == 0) {
      System.out.println(fromServer.readUTF());
    }
    else  {
      for (int i = 0; i < args.size(); i++) {
        System.out.println(fromServer.readUTF());
      }
    }
  }
}
