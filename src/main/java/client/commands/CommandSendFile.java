package client.commands;

import java.io.*;
import java.util.List;

public class CommandSendFile extends BaseCommand {
  CommandSendFile(DataOutputStream toServer, DataInputStream fromServer, String command, List<String> args, String message) {
    super(toServer, fromServer, command, args, message);
  }

  @Override
  public void send() throws IOException {
    sendCommand(command, args);
    if (args != null) {
      for (String arg : args) {
        sendFile(arg);
      }
    }
  }

  private void sendFile(String path) throws IOException {
    File file = new File(path);
    try(InputStream fis= new FileInputStream(file)){
      toServer.writeUTF(file.getName());
      toServer.writeLong(file.length());

      ByteArrayOutputStream fileData=new ByteArrayOutputStream(1024);
      int nRead;
      byte[] data= new byte[1024];
      while((nRead= fis.read(data, 0, data.length))!=-1){
        fileData.write(data, 0, nRead);
      }
      fileData.writeTo(toServer);

    }
  }
}
