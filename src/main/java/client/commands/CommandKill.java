package client.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;

class CommandKill extends BaseCommand {
  CommandKill(DataOutputStream toServer, DataInputStream fromServer, String command, List<String> args, String message) {
    super(toServer, fromServer, command, args, message);
  }

}
