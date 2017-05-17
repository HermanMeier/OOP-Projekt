package client.commands;

/**
 * Created by Ove on 17.05.2017.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class CommandShowTable extends BaseCommand implements Command {
    CommandShowTable(DataOutputStream toServer, DataInputStream fromServer, List<String> args) {
        super(toServer, fromServer, args);
    }

    @Override
    public void beforeSend() {
        System.out.println("Showing entire table...");
    }

    @Override
    public void send() throws IOException {
        sendCommand("showTable", args);
    }

    @Override
    public void afterSend() throws IOException {
        System.out.println(fromServer.readUTF());
    }
}

