import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ove on 02.04.2017.
 */
public class SQLWriter {
    private String dbUser;
    private String dbPass;
    private String dbHost;
    private Connection con;
    private Statement stmt;

    /**
     * @param dbUser    andmebaasi kasutajanimi [d54572_xmldata]
     * @param dbPass    kasutaja parool [Xmldata1]
     * @param dbName    andmebaasi nimi [d54572_xmldata]
     * @param dbHost    andmebaasi host [d54572.mysql.zonevs.eu]
     * @param dbPort    andmebaasi port [3306]
     * @param dbType    andmebaasi tüüp [mysql]
     */

    public SQLWriter(String dbUser, String dbPass, String dbName, String dbHost, String dbPort, String dbType) {
        this.dbUser = dbUser;
        this.dbPass = dbPass;
        this.dbHost = "jdbc:" + dbType + "://" + dbHost + ":" + dbPort + "/" + dbName;
    }

    /**
     * Loob ühenduse andmebaasiga
     */
    public void connectToDB() {
        try {
            this.con = DriverManager.getConnection(dbHost, dbUser, dbPass);
            this.stmt = con.createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Koostab ja käivitab SQL käsu andmete vastavasse tabelisse sisestamiseks
     * @param table     tabali nimi
     * @param columns   tabeli veerud (komadega eraldatud nimekiri sõnena)
     * @param data      tabeli sisu (komadega eraldatud nimekiri sõnena)
     */
    public void insertIntoDB(String table, String columns, String data) {
        try {
            stmt.executeQuery("INSERT INTO " + table + " (" + columns + ") VALUES ( " + data + ")");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String getColumnNames(String table) {
        String columnNames = null;
        try {
            ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM " + table);
            if (rs.next()) {
                columnNames = rs.getString(0);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return columnNames;
    }
}
