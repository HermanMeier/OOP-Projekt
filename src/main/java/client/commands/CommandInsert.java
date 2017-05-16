package client.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class CommandInsert extends BaseCommand implements Command {
    CommandInsert(DataOutputStream toServer, DataInputStream fromServer, List<String> args) {
        super(toServer, fromServer, args);
    }

    @Override
    public void beforeSend() {

    }

    @Override
    public void send() throws IOException {
        sendCommand("insert", args);
    }

    @Override
    public void afterSend() throws IOException {
        System.out.println(fromServer.readUTF());
    }
}
