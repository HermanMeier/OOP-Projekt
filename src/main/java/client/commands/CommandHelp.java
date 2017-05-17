package client.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;

public class CommandHelp extends BaseCommand {

  CommandHelp(List<String> commands, DataOutputStream toServer, DataInputStream fromServer, String command, List<String> args) {
    super(commands, toServer, fromServer, command, args);
  }

  @Override
  public void beforeSend() {
    System.out.println("Syntax: <commandName> <param1> <param2> ...");
    System.out.println("Valid commands:");
    commands.forEach(System.out::println);
  }

  @Override
  public void afterSend() {

  }
}
