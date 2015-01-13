/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package massim.element;

import com.jme3.math.Vector2f;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author TungPT
 */
public class Wall {
    private List<Vector2f> points;

    public List<Vector2f> getPoints() {
        return points;
    }

    public float getHeight() {
        return height;
    }

    public float getThickness() {
        return thickness;
    }
    private float height, thickness;
    
    public Wall(Element wallEl) {
        super();
        NodeList pointNodes = wallEl.getElementsByTagName("Point");
        points = new ArrayList<>(pointNodes.getLength());
        for(int i=0; i<pointNodes.getLength();i++){
            Element e = (Element) pointNodes.item(i);
            Vector2f point = new Vector2f(Float.parseFloat(e.getElementsByTagName("X").item(0).getTextContent()),Float.parseFloat(e.getElementsByTagName("Y").item(0).getTextContent()));
            points.add(point);
        }
        height = Float.parseFloat(wallEl.getElementsByTagName("Height").item(0).getTextContent());
        thickness = Float.parseFloat(wallEl.getElementsByTagName("Thickness").item(0).getTextContent());
    }
    
}
