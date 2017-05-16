package client.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class CommandSearch extends BaseCommand implements Command {
  CommandSearch(DataOutputStream toServer, DataInputStream fromServer, List<String> args) {
    super(toServer, fromServer, args);
  }

  @Override
  public void beforeSend() {
    System.out.println("Searching...");
  }

  @Override
  public void send() throws IOException {
    sendCommand("search", args);
  }

  @Override
  public void afterSend() throws IOException {
    int numberOfResults = fromServer.readInt();
    if (numberOfResults==0)
      System.out.println("No results");
    else
      System.out.println("Matching words in xml files or database:");
    for (int i = 0; i < numberOfResults; i++) {
      System.out.println(fromServer.readUTF());
    }
  }
}
