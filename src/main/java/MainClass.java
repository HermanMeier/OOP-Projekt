import org.jdom2.JDOMException;
import server.UI;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Herman on 30.03.2017.
 */
public class MainClass {
    public static void main(String[] args) throws JDOMException, IOException, SQLException {
        List<String> commands = Arrays.asList("?", "db", "xml", "edit", "exit");

/*        String dbUser = "xmltosql";
        String dbPass = "xmltosql";
        String dbName = "xmltosql";
        String dbHost = "db4free.net";

        DBStuff.SQLWriter sql = new DBStuff.SQLWriter(dbUser, dbPass, dbName, dbHost);
        sql.connectToDB();
        List<String> dbTables = sql.getTableNames();
//        HashMap<String, List<String>> dbColumns = new HashMap<>();
//        for (String dbTable : dbTables) {
//            dbColumns.put(dbTable, sql.getColumnNames(dbTable));
//        }
        sql.disconnect();*/


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
                        ui.selectDB();
                        break;
                    case "xml":
                        ui.selectXML();
                        break;
                    case "edit":
                        ui.edit();
                        break;
                    case "exit":
                        return;
                }
            }
        }
    }
}
