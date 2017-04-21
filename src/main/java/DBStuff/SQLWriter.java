package DBStuff;

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
     */

    public SQLWriter(String dbUser, String dbPass, String dbName, String dbHost) {
        this.dbUser = dbUser;
        this.dbPass = dbPass;
        this.dbHost = "jdbc:mysql://" + dbHost + ":3306/" + dbName;
    }

    /**
     * Loob ühenduse andmebaasiga
     */
    public void connectToDB() throws SQLException {
        this.con = DriverManager.getConnection(dbHost, dbUser, dbPass);
        this.stmt = con.createStatement();
    }
    /**
     * Lõpetab ühenduse andmebaasiga
     */
    public void disconnect() throws SQLException {
        this.con.close();
    }

    //TODO data võiks olla list ja siin saaks selle sõneks teha
    /**
     * Koostab ja käivitab SQL käsu andmete vastavasse tabelisse sisestamiseks
     * @param table     tabeli nimi
     * @param columns   tabeli veerud (komadega eraldatud nimekiri sõnena)
     * @param data      tabeli sisu (komadega eraldatud nimekiri sõnena)
     */
    public void insertIntoDB(String table, String columns, String data) throws SQLException {
        PreparedStatement ps = con.prepareStatement("INSERT INTO ? ( ? ) VALUES ( ? );");
        ps.setString(1, table);
        ps.setString(2, columns);
        ps.setString(3, data);
        ps.executeUpdate();
    }

    /**
     * Tagastab nimetatud tabelis olevad tulbad
     * @param table     tabeli nimi
     * @return          List<String> objekt tabeli tulpadega
     * @throws SQLException
     */
    public List<String> getColumnNames(String table) throws SQLException {
        List<String> columns = new ArrayList<>();
        PreparedStatement ps = con.prepareStatement("SHOW COLUMNS FROM ?;");
        ps.setString(1, table);
        ResultSet rs = ps.executeQuery();
        while (rs.next())   {
            columns.add(rs.getString(1));
        }
        return columns;
    }

    /**
     * Tagastab ühendusesoleva andmebaasi tabelite nimekirja
     * @return          List<String> objekt tabelite nimetustega
     * @throws SQLException
     */
    public List<String> getTableNames() throws SQLException {
        List<String> tables = new ArrayList<>();
        ResultSet rs = stmt.executeQuery("SHOW TABLES;");
        while (rs.next()) {
            tables.add(rs.getString(1));
        }
        return tables;
    }

    public void createSampleTable() throws SQLException {
        stmt.executeUpdate("CREATE TABLE trips (" +
                "id int(11) AUTO_INCREMENT PRIMARY KEY NOT NULL," +
                "agency int(11) DEFAULT NULL," +
                "country int(11) DEFAULT NULL," +
                "destination int(11) DEFAULT NULL," +
                "url varchar(255) DEFAULT NULL," +
                "hotel varchar(255) DEFAULT NULL," +
                "rating int(11) DEFAULT NULL," +
                "hotel_url varchar(255) DEFAULT NULL" +
                ");");
    }
}
