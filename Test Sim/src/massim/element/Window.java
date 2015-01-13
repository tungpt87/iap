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
public class Window {
    private Vector2f leftPoint, rightPoint;

    public Vector2f getLeftPoint() {
        return leftPoint;
    }

    public Vector2f getRightPoint() {
        return rightPoint;
    }

    public float getElevation() {
        return elevation;
    }

    public float getHeight() {
        return height;
    }

    public float getThickness() {
        return thickness;
    }
    private float elevation, height, thickness;
    
    public Window(Element windowEl) {
        Element e = (Element) windowEl.getElementsByTagName("Left-Point").item(0);
        leftPoint = new Vector2f(Float.parseFloat(e.getElementsByTagName("X").item(0).getTextContent()),
                Float.parseFloat(e.getElementsByTagName("Y").item(0).getTextContent()));
        e = (Element) windowEl.getElementsByTagName("Right-Point").item(0);
        rightPoint = new Vector2f(Float.parseFloat(e.getElementsByTagName("X").item(0).getTextContent()),
                Float.parseFloat(e.getElementsByTagName("Y").item(0).getTextContent()));
        elevation = Float.parseFloat(windowEl.getElementsByTagName("Elevation").item(0).getTextContent());
        height = Float.parseFloat(windowEl.getElementsByTagName("Height").item(0).getTextContent());
        thickness = Float.parseFloat(windowEl.getElementsByTagName("Thickness").item(0).getTextContent());
    }
    
}
