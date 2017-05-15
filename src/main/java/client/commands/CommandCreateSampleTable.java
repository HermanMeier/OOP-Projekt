package client.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Ove on 15.05.2017.
 */
public class CommandCreateSampleTable extends BaseCommand implements Command {
    CommandCreateSampleTable(DataOutputStream toServer, DataInputStream fromServer, List<String> args) {
        super(toServer, fromServer, args);
    }

    @Override
    public void beforeSend() {
        System.out.println("Creating sample table...");
    }

    @Override
    public void send() throws IOException {
        sendCommand("createSampleTable", args);
    }

    @Override
    public void afterSend() throws IOException {
        System.out.println(fromServer.readUTF());
    }
}
