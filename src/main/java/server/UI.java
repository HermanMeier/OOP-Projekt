package server;

import DBStuff.SQLWriter;
import DBStuff.XMLhandler;
import org.jdom2.JDOMException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Herman on 19.04.2017.
 */
public class UI {
    private final List<String> commands;
    private final Scanner input;
    private SQLWriter sql = null;
    private XMLhandler xml = null;


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

    public void selectDB()  {
        System.out.println("Connect to database.");

        System.out.print("User: ");
        String user = input.nextLine();

        System.out.print("Password: ");
        String password = input.nextLine();

        System.out.print("DB name: ");
        String DBname = input.nextLine();

        System.out.print("Host: ");
        String host = input.nextLine();

        try {
            sql = new SQLWriter(user, password, DBname, host);
            sql.connectToDB();
            System.out.println("Connected to database.");
        } catch (SQLException e) {
            System.out.println("Failed to connect to database.");
        }
    }

    public void selectXML() {
        System.out.println("Enter url or path: ");
        String source = input.nextLine();

        try {
            xml = new XMLhandler(source);
            xml.openXML();
            System.out.println("Opened xml document.");
        } catch (JDOMException | IOException e) {
            System.out.println("Failed to open xml file.");
        }
    }

    //Seda saab kasutada et vaadet refreshida pärast muutuste tegemist
    private void printColumns(String table) throws SQLException {
        System.out.println("XML: " + xml.getXmlFileName() + "\t" + "SQL table: " + table);
        for (int i = 0; i < Math.max(xml.getColumns().size(), sql.getColumnNames(table).size()); i++) {
            if (sql.getColumnNames(table).size() <= i)
                System.out.println(xml.getColumns().get(i) + "\t" + "");
            else if (xml.getColumns().size() <= i)
                System.out.println("" + "\t" + sql.getColumnNames(table).get(i));
            else
                System.out.println(xml.getColumns().get(i) + "\t" + sql.getColumnNames(table).get(i));
        }
        System.out.println();
    }

    public void edit() throws SQLException {
        if (xml == null || sql == null) {
            System.out.println("XML file not opened or not connected to database.");
            return;
        }

        System.out.println("Select table to edit.");
        for (int i = 0; i < sql.getTableNames().size(); i++) {
            System.out.println(i + "-" + sql.getTableNames().get(i));
        }
        String table;

        //TODO siia oleks vaja mingit while küsimist. ootab kuni saab legit integeri
        System.out.print("Table number: ");
        int index = input.nextInt();

        printColumns(sql.getTableNames().get(index));

        //TODO igast vahetamised, kustutamised, liitmise jms. uus DBStuff.XMLtoSQL klass
    }
}
