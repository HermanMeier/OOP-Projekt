import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Herman on 30.03.2017.
 */
public class XMLhandler {
    private final String xmlFileName;
    private Document xmlDocument;
    private Element root;

    public XMLhandler(String xmlFileName) {
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
    public void openXML() throws JDOMException, IOException {
        //create document builder
        SAXBuilder saxBuilder = new SAXBuilder();

        //create document from xml file
        File inputFile = new File(xmlFileName);
        Document document = saxBuilder.build(inputFile);

        xmlDocument = document;

        Element rootElement = xmlDocument.getRootElement();
        root =rootElement;
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
     * @return      List kõikidest tupadest
     */
    public List<String> getColumns()    {
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
    public int getNumberOfRows()   {
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

    /**
     * Selle meetodiga saab küsida kindla välja väärtust tabelis.
     *
     * @param column    Milline tulp(kasuta getColumns() tagastatud väärtusi)
     * @param row       Mitmes rida
     */
    public void getValue(String column, int row)  {
        Element current = root.getChildren().get(row);

        System.out.println(current.getChild(column).getText());
        // TODO: 30.03.2017 Mida teha siis kui sellist veergu pole? Mingi om exception? Kehtib ka teiste meetodite kohta.
    }

    /**
     * Kustutab terve tulba, mitte ei tühejenda lahtreid
     *
     * @param column
     */
    public void delColumn(String column)    {
        for (Element element : root.getChildren()) {
            element.removeChildren(column);
        }
    }

    /**
     * Tühjendab ühe tabeli välja
     *
     * @param column
     * @param row
     */
    public void removeValueFrom(String column, int row)    {
        Element current = root.getChildren().get(row);
        current.getChild(column).removeContent();
    }

    /**
     * Kustutab ühe rea.
     *
     * @param row
     */
    public void delRow(int row) {
        root.removeChild(root.getChildren().get(row).getName());
    }
}
