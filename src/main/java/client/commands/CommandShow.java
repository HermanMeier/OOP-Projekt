package client.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class CommandShow extends BaseCommand {
  CommandShow(DataOutputStream toServer, DataInputStream fromServer, String command, List<String> args, String message) {
    super(toServer, fromServer, command, args, message);
  }

  @Override
  public void afterSend() throws IOException {
    if (args==null)
      args = Collections.singletonList("");
    for (String arg : args) {
      System.out.println("File "+arg);
      int numberOfColumns = fromServer.readInt();
      if (numberOfColumns==-1)  {
        System.out.println("File not open.");
      }
      else  {
        for (int i = 0; i < numberOfColumns; i++) {
          System.out.println(fromServer.readUTF());
        }
        System.out.println();
    }
    }
  }
}
