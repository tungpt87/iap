/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package massim.element;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author TungPT
 */
public class Building {
    private List<Level> levels;
    private String des;

    public List<Level> getLevels() {
        return levels;
    }

    public String getDes() {
        return des;
    }
    public Building(String xml) {
        super();
        NodeList levelNodes = loadXML(xml);
        levels = new ArrayList<>(levelNodes.getLength());
        for (int i = 0; i<levelNodes.getLength();i++){
            Level level = new Level((Element) levelNodes.item(i));
            levels.add(level);
        }
    }
    private NodeList loadXML(String xml){
        //String str = file.readString();
        Document doc = null;
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder(); 
        //	InputSource is = new InputSource(new StringReader(str));
            File file = new File(xml);
            doc = db.parse(file);
        }
        catch(Exception e){
            System.out.printf(e.toString());
        }
        NodeList grounds = doc.getElementsByTagName("Level");
        return grounds;
    }
    public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc), 
         new StreamResult(new OutputStreamWriter(out, "UTF-8")));
    }
}
