import java.sql.*;


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
     *
     */
    public void disconnect() throws SQLException {
        this.con.close();
    }
    /**
     * Koostab ja käivitab SQL käsu andmete vastavasse tabelisse sisestamiseks
     * @param table     tabali nimi
     * @param columns   tabeli veerud (komadega eraldatud nimekiri sõnena)
     * @param data      tabeli sisu (komadega eraldatud nimekiri sõnena)
     */
    public void insertIntoDB(String table, String columns, String data) throws SQLException {
        stmt.executeQuery("INSERT INTO " + table + " (" + columns + ") VALUES ( " + data + ")");
    }

    public String getColumnNames(String table) throws SQLException {
        String columnNames = null;
        ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM " + table);
        if (rs.next()) {
            columnNames = rs.getString(0);
        }
        return columnNames;
    }
}
