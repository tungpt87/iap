/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package massim;

import com.jme3.ai.navmesh.Path;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import massim.node.AgentNode;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author TungPT
 */
public class Utility {
    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
    
    public static void writeStringToFile(String path, String str) throws IOException{
        String content = str;
 
        File file = new File(path);

        // if file doesnt exists, then create it
        if (!file.exists()) {
                file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content);
        bw.close();
    }
    public static void showPath(AgentNode agent, ColorRGBA color){
        Material mat = new Material(Main.app().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        Path path = agent.getPath();
        Node node = new Node(agent.getName()+"_Path");
        for (Path.Waypoint wp : path.getWaypoints()){
            Box b = new Box(2, 1, 2);
            Geometry geo = new Geometry("WayPoint",b);
            geo.setMaterial(mat);
            geo.setLocalTranslation(wp.getPosition());
            node.attachChild(geo);
        }
        Main.app().getRootNode().detachChildNamed(agent.getName()+"_Path");
        Main.app().getRootNode().attachChild(node);
    }
}
