import org.jdom2.JDOMException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

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

 //       SQLWriter sql = new SQLWriter(dbUser, dbPass, dbName, dbHost);
 //       sql.connectToDB();
 //       String dbColumns = sql.getColumnNames("trips");
 //       System.out.println(dbColumns);
 //       sql.disconnect();

        List<String> columns=xml.getColumns();
        System.out.println("Etteantud .xml failis on tulbad: ");
        for (int i = 0; i < columns.size(); i++) {
            System.out.println(i+1+" - "+columns.get(i));
        }
        Scanner sc = new Scanner(System.in);


        while(true){
            System.out.print("Sisesta, mis numbriga tulpa tahad kustutada või 0, kui enam kustutada ei taha: ");
            if (sc.hasNextInt()) {
                int input = sc.nextInt();
                if (input==0) break;
                xml.delColumn(columns.get(input-1));
                System.out.println("Tulp " + columns.get(input-1)+" kustutatud.");
            }
            else{
                sc.next();
                System.out.println("Vigane sisend");
            }
        }
        sc.close();
        columns=xml.getColumns();
        System.out.println("Etteantud .xml failis on tulbad: ");
        for (int i = 0; i < columns.size(); i++) {
            System.out.println(i+1+" - "+columns.get(i));
        }

    }
}
