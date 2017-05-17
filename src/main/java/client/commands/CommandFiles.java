package client.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class CommandFiles extends BaseCommand {
  CommandFiles(DataOutputStream toServer, DataInputStream fromServer, String command, List<String> args, String message) {
    super(toServer, fromServer, command, args, message);
  }

  @Override
  public void afterSend() throws IOException {
    int numberOfFiles = fromServer.readInt();
    for (int i = 0; i < numberOfFiles; i++) {
      System.out.println(fromServer.readUTF());
    }
  }
}
