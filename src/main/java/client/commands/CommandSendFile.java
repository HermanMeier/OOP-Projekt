package client.commands;

import java.io.*;
import java.util.List;

public class CommandSendFile extends BaseCommand implements Command {

  CommandSendFile(DataOutputStream toServer, DataInputStream fromServer, List<String> args) {
    super(toServer, fromServer, args);
  }

  @Override
  public void beforeSend() {
    System.out.println("Sending files...");
  }

  @Override
  public void send() throws IOException {
    sendCommand("sendFile", args);
    if (args != null) {
      for (String arg : args) {
        sendFile(arg);
      }
    }
  }

  @Override
  public void afterSend() throws IOException {
    if (args != null) {
      for (int i = 0; i < args.size(); i++) {
        System.out.println(fromServer.readUTF());
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
