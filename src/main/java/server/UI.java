package server;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;


public class UI {
    private final List<String> commands;
    private final Scanner input;


    public UI(List<String> commands, Scanner input) {
        this.commands = commands;
        this.input = input;
    }

    public String waitForCommand()  {
        System.out.print(">> ");
        while (input.hasNextLine())    {
            String com = input.nextLine();
            if (commands.contains(com)) {
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

        //See info tuleb saata serverile
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
            case "existing":

            default:
                XMLinfo = null;
                break;
        }

        //See info tuleb saata serverile
        return XMLinfo;
    }

    //Seda saab kasutada et vaadet refreshida pärast muutuste tegemist
    private void printColumns(String table) throws SQLException {
/*        System.out.println("XML: " + xml.getXmlFileName() + "\t" + "SQL table: " + table);
        for (int i = 0; i < Math.max(xml.getColumns().size(), sql.getColumnNames(table).size()); i++) {
            if (sql.getColumnNames(table).size() <= i)
                System.out.println(xml.getColumns().get(i) + "\t" + "");
            else if (xml.getColumns().size() <= i)
                System.out.println("" + "\t" + sql.getColumnNames(table).get(i));
            else
                System.out.println(xml.getColumns().get(i) + "\t" + sql.getColumnNames(table).get(i));
        }
        System.out.println();*/
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

        /*if (xml == null || sql == null) {
            System.out.println("XML file not opened or not connected to database.");
            return;
        }

        System.out.println("Select table to edit.");
        for (int i = 0; i < sql.getTableNames().size(); i++) {
            System.out.println(i + "-" + sql.getTableNames().get(i));
        }
        String table;

        System.out.print("Table number: ");
        int index = input.nextInt();

        printColumns(sql.getTableNames().get(index));*/

    }
}
