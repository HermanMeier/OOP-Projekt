import java.sql.SQLException;
import java.util.List;

/**
 * Halva nimega klass. Siia võiks tulla kõik need funktsioonid mida saab avatud xml ja db teha.
 * Iga funktsioon võiks olla oma meetod.
 *
 * Created by Herman on 20.04.2017.
 */
public class XMLtoSQL {
    private final XMLhandler xml;
    private final SQLWriter sql;
    private final String table;

    public XMLtoSQL(XMLhandler xml, SQLWriter sql, String table) {
        this.xml = xml;
        this.sql = sql;
        this.table = table;
    }

    public void addColumnToDatabase(int columnIndex) throws SQLException {
        String columnName = xml.getColumnName(columnIndex);
        //List<String> data = xml.getColumnData(columnName);       //vt SQLWriter meetodit
        //sql.insertIntoDB(table, columnName, data);
    }

    public void deleteColumn(int columnIndex)   {

    }

    public void addMergeColumns(String newName, int... columns) {
        List<String> columnNames = null;
        List<String> data = null;
        for (int column : columns) {
            columnNames.add(xml.getColumnName(column));
        }
        for (int i = 0; i < xml.getNumberOfRows(); i++) {
            for (String columnName : columnNames) {
                data.add(xml.getValue(columnName, i));
            }
        }

        //sql.insertIntoDB(table, newName, data);
    }
}
