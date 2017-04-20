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
        String user;
        String password;

        System.out.println("Connect to database.");

        System.out.print("User: ");
        user = input.nextLine();

        System.out.print("Password: ");
        password = input.nextLine();

        try {
            sql = new SQLWriter(user, password, "", "");
            sql.connectToDB();
            System.out.println("Connected to database.");
        } catch (SQLException e) {
            System.out.println("Failed to connect to database.");
        }
    }

    public void selectXML() throws JDOMException, IOException {
        /* TODO
        * Küsib failinime või urli
        * Ajutiselt avab selle näite faili
        * */
        xml = new XMLhandler("src/main/resources/biginfo.xml");
        xml.openXML();
        System.out.println("Opened xml document.");
    }

    //Seda saab kasutada et vaadet refreshida pärast muutuste tegemist
    private void printColumns(String table) throws SQLException {
        System.out.println("XML: " + xml.getXmlFileName() + "\t" + "SQL table: " + table);
        for (int i = 0; i < Math.max(xml.getColumns().size(), sql.getColumnNamesAsList(table).size()); i++) {
            if (sql.getColumnNamesAsList(table).size() <= i)
                System.out.println(xml.getColumns().get(i) + "\t" + "");
            else if (xml.getColumns().size() <= i)
                System.out.println("" + "\t" + sql.getColumnNamesAsList(table).get(i));
            else
                System.out.println(xml.getColumns().get(i) + "\t" + sql.getColumnNamesAsList(table).get(i));
        }
        System.out.println();
    }

    public void edit() throws SQLException {
        if (xml == null || sql == null) {
            System.out.println("XML file not opened or not connected to database.");
            return;
        }

        System.out.println("Select table to edit.");
        /* TODO vaja sql.getTables() meetodit
        * for (int i = 0; i < sql.getTables().size(); i++) {
            System.out.println(i + "-" + sql.gettables().get(i));
        }
        * */
        System.out.print("Table number: ");
        String table = input.nextLine();

        printColumns(table);

        //TODO igast vahetamised, kustutamised, liitmise jms.
    }
}
