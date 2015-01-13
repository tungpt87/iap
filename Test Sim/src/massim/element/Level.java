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
public class Level {
    private List<Wall> walls;
    private List<Door> doors;
    private List<Window> windows;
    private Vector2f boundary_cor_1;

    public List<Wall> getWalls() {
        return walls;
    }

    public List<Door> getDoors() {
        return doors;
    }

    public List<Window> getWindows() {
        return windows;
    }

    public Vector2f getBoundary_cor_1() {
        return boundary_cor_1;
    }

    public Vector2f getBoundary_cor_2() {
        return boundary_cor_2;
    }

    public float getZcoor() {
        return zcoor;
    }
    private Vector2f boundary_cor_2;
    private float zcoor;
    
    /**
     * Constructor
     * @param levelEl : XML Element contains all information of the level
     */
    public Level(Element levelEl) {
        super();
        //Parse and import walls
        NodeList wallNodes = levelEl.getElementsByTagName("Wall");
        walls = new ArrayList<>(wallNodes.getLength());
        for (int i = 0; i<wallNodes.getLength();++i){
            Wall w = new Wall((Element) wallNodes.item(i));
            walls.add(w);
        }
        
        //Parse and import doors
        NodeList doorNodes = levelEl.getElementsByTagName("Door");
        doors = new ArrayList<>(doorNodes.getLength());
        for (int i = 0; i<doorNodes.getLength();++i){
            Door d = new Door((Element) doorNodes.item(i));
            doors.add(d);
        }
        
        //Parse and import windows
        NodeList windowNodes = levelEl.getElementsByTagName("Window");
        windows = new ArrayList<>(windowNodes.getLength());
        for (int i = 0; i<windowNodes.getLength();++i){
            Window wi = new Window((Element) windowNodes.item(i));
            windows.add(wi);
        }
        
        //Parse and import boundary
        
        boundary_cor_1 = new Vector2f(Float.parseFloat(levelEl.getElementsByTagName("Bound-maxX").item(0).getTextContent()), Float.parseFloat(levelEl.getElementsByTagName("Bound-maxY").item(0).getTextContent()));
        boundary_cor_2 = new Vector2f(Float.parseFloat(levelEl.getElementsByTagName("Bound-minX").item(0).getTextContent()), Float.parseFloat(levelEl.getElementsByTagName("Bound-minY").item(0).getTextContent()));
        
        //z coordination of the level
        zcoor = Float.parseFloat(levelEl.getElementsByTagName("ZLevel").item(0).getTextContent());
                
    }
    
}
