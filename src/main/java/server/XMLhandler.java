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
     * Teeb xml failist klassi Document isendi, mida saab kasutada failist info saamiseks.
     * Loob ka root elemendi, mis on xml failis kõige esimene tag.
     * Seda meetodit peab kasutama, et teised selles klassis olevad meetodid töötaksid.
     *
     * @throws JDOMException
     * @throws IOException
     */
    void openXML() throws JDOMException, IOException {
        //create document builder
        SAXBuilder saxBuilder = new SAXBuilder();


        File inputFile = new File("xmlFiles\\"+xmlFileName);
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
     * Abimeetod meetodile printContent.
     *
     * @param parent    Element mis sisaldab mitut elementi
     * @param level     Sügavus rekursiooniks
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
     * Kasutab rekursiooni, et kuvada kogu xml faili sisu.
     */
    public void printContent()  {
        List<Element> children = root.getChildren();

        for (Element child : children) {
            printChildren(child, 0);
            System.out.println();
        }
    }

    /**
     * Leiab kõik unikaalsed tulpade pealkirjad.
     *
     * @return      List kõikide tulpae nimedest
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
     * @return      Tagastab ridade arvu.
     */
    int getNumberOfRows()   {
        return root.getChildren().size();
    }

    /**
     * @param row   Rea number
     * @return      Tagastab ühe rea kõikide tulpade väärtused. Ei sisalda tulba pealkirju.
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
            content.add(root.getChildren().get(i).getChild(column).getText());
        }
        return content;
    }

    public String getColumnName(int index)  {
        return root.getChildren().get(index).getText();
    }

    /**
     * Selle meetodiga saab küsida kindla välja väärtust tabelis.
     *
     * @param column    Milline tulp(kasuta getColumns() tagastatud väärtusi)
     * @param row       Mitmes rida
     */
    String getValue(String column, int row)  {
        Element current = root.getChildren().get(row);
        return current.getChild(column).getText();
    }

    /**
     * Kustutab terve tulba, mitte ei tühejenda lahtreid
     */
    public void delColumn(String column)    {
        for (Element element : root.getChildren()) {
            element.removeChildren(column);
        }
    }

    /**
     * Tühjendab ühe tabeli välja
     *
     */
    public void removeValueFrom(String column, int row)    {
        Element current = root.getChildren().get(row);
        current.getChild(column).removeContent();
    }

    /**
     * Kustutab ühe rea.
     *
     */
    public void delRow(int row) {
        root.removeChild(root.getChildren().get(row).getName());
    }

    public String dataTypeofColumn(String columnname, List<String> columnData){
        boolean possibleInt=true;
        boolean possibleDouble=true;
        int maxlength=0;

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
        return "VARCHAR("+maxlength+")";
    }

    private boolean isBoolean(String value){
        if (value.toLowerCase().equals("true")||value.toLowerCase().equals("false"))
            return true;
        return false;
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
