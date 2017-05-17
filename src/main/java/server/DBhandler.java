package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class DBhandler {
    private String dbUser;
    private String dbPass;
    private String dbName;
    private String connectionString;
    private Connection con;
    private Statement stmt;

    /**
     * @param dbUser    andmebaasi kasutajanimi [d54572_xmldata]
     * @param dbPass    kasutaja parool [Xmldata1]
     * @param dbName    andmebaasi nimi [d54572_xmldata]
     * @param dbHost    andmebaasi host [d54572.mysql.zonevs.eu]
     */

    DBhandler(String dbUser, String dbPass, String dbName, String dbHost) {
        this.dbUser = dbUser;
        this.dbPass = dbPass;
        this.dbName = dbName;
        this.connectionString = "jdbc:mysql://" + dbHost + ":3306/" + dbName + "?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useBLegacyDatetimeCode=fBalse&serverTimezone=Europe/Moscow";
        //TODO connect to sqlAnywhere database. need to add jConnect.jar to classPath

        //for sqlAnywhere
        /*this.dbUser = dbUser;
        this.dbPass = dbPass;
        this.dbName = dbName;
        this.dbHost = dbHost;
        this.connectionString = "jdbc:sqlanywhere:uid="+dbUser+";pwd="+dbPass+";eng="+dbName+";database=edu;links=tcpip(host="+dbHost+")";*/
    }

    /**
     * Loob ühenduse andmebaasiga
     */
    void connectToDB() throws SQLException {
        this.con = DriverManager.getConnection(connectionString, dbUser, dbPass);
        this.stmt = con.createStatement();

        //for sqlAnywhere
        /*this.con = DriverManager.getConnection(connectionString);
        this.stmt = con.createStatement();*/
    }
    /**
     * Lõpetab ühenduse andmebaasiga
     */
    void disconnect() throws SQLException {
        this.con.close();
    }

    String getDbName() {
        return dbName;
    }

    /**
     * Koostab ja käivitab SQL käsu andmete vastavasse tabelisse sisestamiseks
     * @param table     tabeli nimi String
     * @param columns   tabeli veerud String[]
     * @param data      tabeli sisu String[]
     */
    void insertIntoDB(String table, String[] columns, String[] data) throws SQLException {
        StringBuilder sb = new StringBuilder();
        for (String c : columns) {
            sb.append(", ").append(c);
        }
        String cols = sb.substring(2);
        sb = new StringBuilder();
        for (String d : data) {
            sb.append(", ").append(d);
        }
        String values = sb.substring(2);
        PreparedStatement ps = con.prepareStatement("INSERT INTO " + table + " ( " + cols + " ) VALUES ( ? );");
        ps.setString(1, values);
        ps.executeUpdate();
    }

    /**
     * Tagastab nimetatud tabelis olevad tulbad
     * @param table     tabeli nimi
     * @return          List<String> objekt tabeli tulpadega
     * @throws SQLException
     */
    List<String> getColumnNames(String table) throws SQLException {
        List<String> columns = new ArrayList<>();
//        PreparedStatement ps = con.prepareStatement("SHOW COLUMNS FROM ?;");
//        ps.setString(1, table);
//        ResultSet rs = ps.executeQuery();
        ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM " + table + ";");
        while (rs.next())   {
            columns.add(rs.getString(1));
            //System.out.println(rs.getString(1));
        }
        return columns;
    }

    /**
     * Tagastab ühendusesoleva andmebaasi tabelite nimekirja
     * @return          List<String> objekt tabelite nimetustega
     * @throws SQLException
     */
    List<String> getTableNames() throws SQLException {
        List<String> tables = new ArrayList<>();
        ResultSet rs = stmt.executeQuery("SHOW TABLES;");
        while (rs.next()) {
            tables.add(rs.getString(1));
        }
        return tables;
    }

    void createSampleTable(String table) throws SQLException {
        stmt.executeUpdate("CREATE TABLE " + table + " (" +
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

    void createTable(String tablename, Map<String, String> data) throws SQLException {
        StringBuilder sb=new StringBuilder();

        for (Map.Entry<String, String> entry : data.entrySet()) {
            sb.append(entry.getKey());
            sb.append(" ");
            sb.append(entry.getValue());
            sb.append(" DEFAULT NULL,");
        }

        sb.deleteCharAt(sb.length()-1);

        PreparedStatement ps = con.prepareStatement("CREATE TABLE "+tablename+" ("+
                "id int AUTO_INCREMENT PRIMARY KEY NOT NULL, "+sb+");");

        ps.executeUpdate();
    }

    List<List<String>> getTableContent(String tablename) throws SQLException {
        List<List<String>> vastus = new ArrayList<>();
        ResultSet rs = stmt.executeQuery("SELECT * FROM " + tablename + ";");
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsCount = rsmd.getColumnCount();
        while (rs.next()) {
            List<String> rida = new ArrayList<>();
            for (int i = 1; i <= columnsCount; i++) {
                rida.add(rs.getString(i));
            }
            vastus.add(rida);
        }
        return vastus;
    }
}
