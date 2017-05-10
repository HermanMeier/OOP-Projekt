package Editor;

import java.sql.SQLException;
import java.util.List;

public class Editor {
    private final XMLhandler xml;
    private final DBhandler sql;
    private final String table;

    public Editor(XMLhandler xml, DBhandler sql, String table) {
        this.xml = xml;
        this.sql = sql;
        this.table = table;
    }

    public void addColumnToDatabase(int columnIndex) throws SQLException {
        String[] columnName = {xml.getColumnName(columnIndex)};
        String[] data = xml.getColumnData(columnName[0]).toArray( new String[0]);
        sql.insertIntoDB(table, columnName, data);
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
