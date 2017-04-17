import org.jdom2.JDOMException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Created by Herman on 30.03.2017.
 */
public class MainClass {
    public static void main(String[] args) throws JDOMException, IOException, SQLException {
        XMLhandler xml = new XMLhandler("src/main/resources/biginfo.xml");
        File output = new File("output.txt");

        xml.openXML();

        String dbUser = "d54572_xmldata";
        String dbPass = "Xmldata1";
        String dbName = "d54572_xmldata";
        String dbHost = "d54572.mysql.zonevs.eu";

        SQLWriter sql = new SQLWriter(dbUser, dbPass, dbName, dbHost);
        sql.connectToDB();

        try (PrintWriter pw = new PrintWriter(output, "UTF-8")) {

            for (int i = 1; i < xml.getNumberOfRows(); i++) {

                for (int j = 0; j < 9; j++) {
                    if (xml.getRow(i).get(j).toString().equals("")) {
                        pw.print("NULL"+";");
                    }
                    else
                        pw.print(xml.getRow(i).get(j).toString().replace(" ", "_")+";");
                }
                pw.println();
            }
        }
    }
}
