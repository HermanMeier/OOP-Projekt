package client;

import java.util.Scanner;


class UI {
  private final Scanner input;

  UI(Scanner input) {
      this.input = input;
  }

    /*public String[] selectDB()  {
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
        if (XMLinfo[0] != null) {
            switch (XMLinfo[0]) {
                case "http":
                    System.out.print("Enter url: ");
                    XMLinfo[1] = input.nextLine();
                    break;
                case "file":
                    System.out.print("Enter file with path: ");
                    XMLinfo[1] = input.nextLine();
                    break;
                case "existing":

                default:
                    XMLinfo = null;
                    break;
            }
        }
        return XMLinfo;
    }

    public void receiveDataToEdit(DataInputStream dis) throws IOException {
        String header = dis.readUTF();
        String leftAlignFormat = "|%-4d|%-25s|%-25s|\n";
        System.out.format("+----+-------------------------+-------------------------+\n");
        System.out.format(leftAlignFormat, 0, header.split(";")[0],header.split(";")[1]);
        System.out.format("+----+-------------------------+-------------------------+\n");

        int numberOfLines = dis.readInt();
        for (int i = 0; i < numberOfLines; i++) {
            String line = dis.readUTF();
            System.out.format(leftAlignFormat, i+1, line.split(";")[0], line.split(";")[1]);
        }

        System.out.format("+----+-------------------------+-------------------------+\n");
    }

    public String[] edit(String tables)  {
        String[] info = new String[3];

        System.out.print("Enter db to edit: ");
        info[0] = input.nextLine();

        System.out.println("Tables in database:");
        System.out.println(tables);
        System.out.print("Enter db table to edit: ");
        info[1] = input.nextLine();

        System.out.print("Enter xml to edit: ");
        info[2] = input.nextLine();

        return info;
    }*/
}
