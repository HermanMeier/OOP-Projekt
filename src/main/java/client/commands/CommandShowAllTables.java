package client.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;

class CommandShowAllTables extends BaseCommand {
    CommandShowAllTables(DataOutputStream toServer, DataInputStream fromServer, String command, List<String> args, String message) {
        super(toServer, fromServer, command, args, message);
    }

}
