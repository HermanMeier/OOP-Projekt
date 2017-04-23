package server;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class UI {
    private final Scanner input;


    public UI(Scanner input) {
        this.input = input;
    }

    public String waitForCommand(List<String> commands)  {
        System.out.print(">> ");
        while (input.hasNextLine())    {
            String com = input.nextLine();
            if (commands.contains(com) || commands.contains(com.substring(0, com.indexOf(":")))) {
                return com;
            }
            else    {
                System.out.println("No such command. Try ? for help.");
                System.out.print(">> ");
            }
        }
        return null;
    }

    public String[] selectDB()  {
        String[] DBinfo = new String[4];
        System.out.println("Connect to database.");

        System.out.print("User: ");
        DBinfo[0] = input.nextLine();

        System.out.print("Password: ");
        DBinfo[1] = input.nextLine();

        System.out.print("DB name: ");
        DBinfo[2] = input.nextLine();

        System.out.print("Host: ");
        DBinfo[3] = input.nextLine();

        return DBinfo;
    }

    public String[] selectXML() {
        String[] XMLinfo = new String[2];
        System.out.println("Type \"http\" to send url to server or \"file\" to select file from your own computer");
        XMLinfo[0] = input.nextLine();

        switch (XMLinfo[0]) {
            case "http":
                System.out.print("Enter url: ");
                XMLinfo[1] = input.nextLine();
                break;
            case "file":
                System.out.print("Enter file with path: ");
                XMLinfo[1] = input.nextLine();
                break;
            default:
                XMLinfo = null;
                break;
        }

        return XMLinfo;
    }

    public void receiveDataToEdit(DataInputStream dis) throws IOException {
        System.out.println(dis.readUTF());
        int numberOfLines = dis.readInt();

        for (int i = 0; i < numberOfLines; i++) {
            System.out.println(dis.readUTF());
        }
        System.out.println();
    }

    public String[] edit()  {
        String[] info = new String[3];

        System.out.print("Enter db to edit: ");
        info[0] = input.nextLine();

        System.out.print("Enter db table to edit: ");
        info[1] = input.nextLine();

        System.out.print("Enter xml to edit: ");
        info[2] = input.nextLine();

        return info;
    }
}
