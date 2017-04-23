import org.jdom2.JDOMException;
import server.UI;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Sample database credentials:
 * User: xmltosql
 * Password: xmltosql
 * Database name: xmltosql
 * Database host: db4free.net
 */


public class MainClass {
    public static void main(String[] args) throws JDOMException, IOException, SQLException {
        List<String> commands = Arrays.asList("?", "db", "xml", "edit", "exit");
        DBStuff.SQLWriter sql;
        List<String> dbTables;
        HashMap<String, List<String>> dbColumns = new HashMap<>();

        try (Scanner sc = new Scanner(System.in))   {
            UI ui = new UI(commands, sc);

            while (true) {
                String command = ui.waitForCommand();

                switch (command) {
                    case "?":
                        for (String com : commands) {
                            System.out.println(com);
                        }
                        break;
                    case "db":
                        String[] dbLoginData = ui.selectDB();
                        sql = new DBStuff.SQLWriter(dbLoginData[0], dbLoginData[1], dbLoginData[2], dbLoginData[3]);
                        sql.connectToDB();
                        dbTables = sql.getTableNames();
                        for (String dbTable : dbTables) {
                            dbColumns.put(dbTable, sql.getColumnNames(dbTable));
                        }
                        System.out.println("Successfully connected to database!");
                        break;
                    case "xml":
                        ui.selectXML();
                        break;
                    case "edit":
                        ui.edit();
                        break;
                    case "exit":
/*                        if (sql != null) {
                            sql.disconnect();
                        }*/
                        return;
                }
            }
        }
    }
}
