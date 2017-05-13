package client.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class CommandShow extends BaseCommand implements Command{
  CommandShow(DataOutputStream toServer, DataInputStream fromServer, List<String> args) {
    super(toServer, fromServer, args);
  }

  @Override
  public void beforeSend() {

  }

  @Override
  public void send() throws IOException {
    sendCommand("show", args);
  }

  @Override
  public void afterSend() throws IOException {
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
