/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package massim.element;

import com.jme3.math.Vector2f;
import org.w3c.dom.Element;
/**
 *
 * @author TungPT
 */
public class Door {
    private Vector2f leftPoint, rightPoint;
    private float height, thickness;

    public Vector2f getLeftPoint() {
        return leftPoint;
    }

    public Vector2f getRightPoint() {
        return rightPoint;
    }

    public float getHeight() {
        return height;
    }

    public float getThickness() {
        return thickness;
    }
    public Door(Element doorEl) {
        super();
        Element e = (Element) doorEl.getElementsByTagName("Fix-Position").item(0);
        leftPoint = new Vector2f(Float.parseFloat(e.getElementsByTagName("X").item(0).getTextContent()),
                Float.parseFloat(e.getElementsByTagName("Y").item(0).getTextContent()));
        e = (Element) doorEl.getElementsByTagName("Open-Position").item(0);
        rightPoint = new Vector2f(Float.parseFloat(e.getElementsByTagName("X").item(0).getTextContent()),
                Float.parseFloat(e.getElementsByTagName("Y").item(0).getTextContent()));
        height = Float.parseFloat(doorEl.getElementsByTagName("Height").item(0).getTextContent());
        thickness = Float.parseFloat(doorEl.getElementsByTagName("Thickness").item(0).getTextContent());
    }
    
}
