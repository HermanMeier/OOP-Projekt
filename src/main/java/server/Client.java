package server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Client {
    private final static int BUFFER_SIZE=1024;
    private final static List<String> commands = Arrays.asList("?", "db", "xml", "edit", "exit", "files","def");

    public static void main(String[] args) throws IOException {
        try(Socket sock = new Socket("localhost", 1337);
            DataOutputStream dos=new DataOutputStream(sock.getOutputStream());
            DataInputStream dis=new DataInputStream(sock.getInputStream());
            Scanner sc = new Scanner(System.in)
        ){
            UI ui = new UI(sc);

            while (true) {
                String command = ui.waitForCommand(commands);
                sendCommand(command, dos);

                switch (command) {
                    case "?":
                        for (String com : commands) {
                            System.out.println(com);
                        }
                        break;
                    case "db":
                        String[] DBinfo = ui.selectDB();

                        for (String s : DBinfo) {
                            if (s == null)
                                dos.writeUTF("");
                            else
                                dos.writeUTF(s);
                        }

                        System.out.println(dis.readUTF());
                        break;
                    case "xml":
                        String[] XMLinfo = ui.selectXML();

                        if (XMLinfo == null)    {
                            dos.writeUTF("");
                            System.out.println("Invalid command.");
                        }
                        else if (XMLinfo[0].equals("http")){
                            dos.writeUTF("sending URL");
                            dos.writeUTF(XMLinfo[1]);
                            System.out.println(dis.readUTF());
                            break;
                        }
                        else if(XMLinfo[0].equals("file")){
                            dos.writeUTF("file");
                            File file = new File(XMLinfo[1]);

                            if (file.exists() && XMLinfo[1].substring(XMLinfo[1].length()-4, XMLinfo[1].length()).equals(".xml"))  {
                                dos.writeBoolean(true);
                                sendFile(file, dos);
                                System.out.println(dis.readUTF());
                            }
                            else    {
                                dos.writeBoolean(false);
                                System.out.println("No such xml file found.");
                            }
                        }
                        break;
                    case "def":
                        System.out.println("Loading default setup for testing...");
                        System.out.println(dis.readUTF());
                        System.out.println(dis.readUTF());
                        break;
                    case "edit":
                        String tables = dis.readUTF();
                        String[] info = ui.edit(tables);

                        for (String s : info) {
                            dos.writeUTF(s);
                        }

                        if (!dis.readBoolean()) {
                            System.out.println(dis.readUTF());
                            break;
                        }


                        System.out.println(dis.readUTF());
                        ui.receiveDataToEdit(dis);

                        List<String> editCommands = Arrays.asList("close", "?", "add", "del", "merge");
                        while (true) {
                            String editCommand = ui.waitForCommand(editCommands);
                            if (!editCommand.contains(":") && !editCommand.equals("?") && !editCommand.equals("close")) {
                                dos.writeBoolean(false);
                                System.out.println("Syntax error in command, no \":\".");
                            }
                            else    {
                                dos.writeBoolean(true);
                                sendCommand(editCommand, dos);
                                if (editCommand.equals("?"))    {
                                    for (String com : editCommands) {
                                        System.out.println(com);
                                    }
                                }
                                else if (editCommand.equals("close"))    {
                                    break;
                                }
                                //TODO pärast close muud asjad ei tööta, kuskil keegi ootab infot
                                else if (editCommand.substring(0,editCommand.indexOf(":")).equals("add") || editCommand.substring(0,editCommand.indexOf(":")).equals("del")) {
                                    switch (editCommand) {
                                        case "add":
                                            System.out.println(dis.readUTF());
                                            ui.receiveDataToEdit(dis);
                                            break;
                                        case "del":
                                            System.out.println(dis.readUTF());
                                            ui.receiveDataToEdit(dis);
                                            break;
                                    }
                                }
                            }
                        }

                        break;
                    case "files":
                        //TODO küsimised printimised UI klassi meetodiks
                        int amount= dis.readInt();
                        if (amount==0) {
                                System.out.println("Server has no files");
                                break;
                            }
                        System.out.println("Server has files: ");
                        List<String> fileNames=new ArrayList<>();
                        for (int i = 0; i < amount; i++) {
                                String fileName=dis.readUTF();
                                System.out.println(fileName);
                                fileNames.add(fileName);
                        }

                        boolean running=true;
                        while (running){
                            System.out.println("Do you wish to use one of those files? (y/n)");
                            String answer=sc.nextLine();
                                if (answer.equals("y")||answer.equals("yes")){
                                    dos.writeBoolean(true);
                                    System.out.println("Which file would you like to use?");
                                    while(true){
                                        String filename=sc.nextLine();
                                        if (fileNames.contains(filename)){
                                            dos.writeUTF(filename);
                                            break;
                                        }
                                        else {
                                            System.out.println("No such file");
                                        }
                                    }
                                    System.out.println(dis.readUTF());
                                    running=false;
                                }
                                else if(answer.equals("no") || answer.equals("n")){
                                    dos.writeBoolean(false);
                                    running =false;
                                }
                                else{
                                    System.out.println("Incorrect input");
                                }
                            }

                        break;
                    case "exit":
                        return;
                }
            }
        }
    }

    private static boolean sendCommand(String command, DataOutputStream dos){
        try {
            dos.writeUTF("sending command");
            dos.writeUTF(command);
        } catch (IOException e) {
            System.out.println("Couldn't send command: "+command);
        }

        return true;
    }

     private static boolean sendFile(File file, DataOutputStream dos){
         try(InputStream fis= new FileInputStream(file)){
                dos.writeUTF(file.getName());
                dos.writeLong(file.length());

                ByteArrayOutputStream fileData=new ByteArrayOutputStream(BUFFER_SIZE);
                int nRead;
                byte[] data= new byte[BUFFER_SIZE];
                while((nRead= fis.read(data, 0, data.length))!=-1){
                    fileData.write(data, 0, nRead);
                }
                fileData.writeTo(dos);

        }
        catch (Exception e){
            System.out.println("Error in sending file");
            e.printStackTrace();
            return false;
        }


        return true;
     }
}
