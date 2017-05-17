package server;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XMLhandler {
    private final String xmlFileName;
    private Document xmlDocument;
    private Element root;
    private final List<String> words = new ArrayList<>();

    XMLhandler(String xmlFileName) {
        this.xmlFileName = xmlFileName;
    }

    public String getXmlFileName() {
        return xmlFileName;
    }

    /**
     * Turns the xml file into a Document object which can be used for info gathering
     * Creates a root element which is the first tag in the xml file
     * This method must be called for all other methods in this class to work.
     *
     * @throws JDOMException
     * @throws IOException
     */
    void openXML() throws JDOMException, IOException {
        //create document builder
        SAXBuilder saxBuilder = new SAXBuilder();


        File inputFile = new File("xmlFiles"+File.separatorChar+xmlFileName);
        xmlDocument = saxBuilder.build(inputFile);
        root = xmlDocument.getRootElement();
    }

    void saveWords() {
      getColumns().forEach(words::add);
    }

    boolean containsWord(String word)  {
      return words.contains(word);
    }

  /**
     * Used by printContent
     *
     * @param parent    Element which contains other elements
     * @param level     Depth for recursion
     */
    private void printChildren(Element parent, int level)    {
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }
        System.out.print(parent.getName());
        if (parent.hasAttributes()) {
            System.out.println(": " + parent.getAttributes());
        }
        else
            System.out.println();
        for (Element child : parent.getChildren()) {
            if (!child.getChildren().isEmpty()) {
                printChildren(child, level+1);
            }
            else    {
                for (int i = 0; i < level+1; i++) {
                    System.out.print("  ");
                }
                System.out.print(child.getName() + ": ");
                System.out.println(child.getText());
            }

        }
    }

    /**
     * Utilises recursion to display the contens of the xml file
     */
    public void printContent()  {
        List<Element> children = root.getChildren();

        for (Element child : children) {
            printChildren(child, 0);
            System.out.println();
        }
    }

    /**
     * Finds all unique column names
     *
     * @return      List of all column names
     */
    List<String> getColumns()    {
        List<String> columns = new ArrayList<>();

        for (Element child : root.getChildren()) {
            for (Element element : child.getChildren()) {
                if (!columns.contains(element.getName()))    {
                    columns.add(element.getName());
                }
            }
        }

        return columns;
    }

    /**
     * @return      Number of rows in xml file
     */
    int getNumberOfRows()   {
        return root.getChildren().size();
    }

    /**
     * @param row   Row number
     * @return      Values of all columns from given row
     */
    public List<String> getRow(int row) {
        List<String> content = new ArrayList<>();
        for (Element element : root.getChildren().get(row).getChildren()) {
            content.add(element.getText());
        }
        return content;
    }

    public List<String> getColumnData(String column)    {
        List<String> content = new ArrayList<>();
        for (int i = 0; i < getNumberOfRows(); i++) {
          if (root.getChildren().get(i).getChild(column)!=null) {
            content.add(root.getChildren().get(i).getChild(column).getText());
          }
        }
        return content;
    }

    public String getColumnName(int index)  {
        return root.getChildren().get(index).getText();
    }

    /**
     * Returns the value at given row in the given column
     *
     * @param column    String columnname
     * @param row       int row number
     */
    String getValue(String column, int row)  {
        Element current = root.getChildren().get(row);
        return current.getChild(column).getText();
    }

    public void delColumn(String column)    {
        for (Element element : root.getChildren()) {
            element.removeChildren(column);
        }
    }


    public void removeValueFrom(String column, int row)    {
        Element current = root.getChildren().get(row);
        current.getChild(column).removeContent();
    }

    public void delRow(int row) {
        root.removeChild(root.getChildren().get(row).getName());
    }

    public String dataTypeofColumn(String columnname){
        boolean possibleInt=true;
        boolean possibleDouble=true;
        int maxlength=0;

        List<String> columnData=this.getColumnData(columnname);

        for (String data : columnData) {
            maxlength=Integer.max(maxlength, data.length());
            if (possibleInt){
                if(!isInt(data))
                    possibleInt=false;
            }
            if(possibleDouble){
                if(!isDouble(data))
                    possibleDouble=false;
            }
        }

        if (maxlength>255)
            return "BLOB";
        if (possibleInt)
            return "INT";
        if (possibleDouble)
            return "DOUBLE";
        return "VARCHAR(255)";
    }


    private boolean isInt(String value){
        try{
            Integer.parseInt(value);
        }catch(NumberFormatException e){
            return false;}
        return true;
    }

    private boolean isDouble(String value){
        try{
            Double.parseDouble(value);
        }catch (NumberFormatException e){
            return false;
        }
        return true;
    }

}
