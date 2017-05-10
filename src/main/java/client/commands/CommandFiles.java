package client.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class CommandFiles extends BaseCommand implements Command {
  CommandFiles(DataOutputStream toServer, DataInputStream fromServer, List<String> args) {
    super(toServer, fromServer, args);
  }

  @Override
  public void beforeSend() {

  }

  @Override
  public void send() throws IOException {
    sendCommand("files",null);
  }

  @Override
  public void afterSend() throws IOException {
    int numberOfFiles = fromServer.readInt();
    for (int i = 0; i < numberOfFiles; i++) {
      System.out.println(fromServer.readUTF());
    }
  }
}
