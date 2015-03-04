/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package massim.behavior;

import com.jme3.bullet.control.GhostControl;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import massim.Main;
import massim.node.AgentNode;
import massim.node.Environment;
import massim.Tree;
import massim.Tree.Node;
/**
 *
 * @author TungPT
 */
public class BlindlyEvacuateBehavior extends AbstractBehavior{
    Tree positionTree = new Tree();
    Tree.Node currentNode;
    Tree.Node targetNode = null;
    ExploreBehavior explore;
    PathFindingBehavior pathFinding;
    ArrayList<Spatial> seenDoors;
    GhostControl target;
    
    public BlindlyEvacuateBehavior(Environment envi, AgentNode agent) {
        super(envi, agent);
        currentNode = positionTree.getRoot();
        explore = new ExploreBehavior(envi,agent);
        pathFinding = new PathFindingBehavior(envi, agent);
        positionTree.getRoot().setCoordinate(agent.getWorldTranslation());
    }
    
    private boolean escapableDoor(Spatial door){
        Vector3f src = door.getWorldTranslation();
        float min = 0;
        Spatial bound = null;
        for (Spatial s : envi.getBoundsNode().getChildren()){
            if (min == 0 || s.getWorldTranslation().distance(src) < min){
                min = s.getWorldTranslation().distance(src);
                bound = s;
            }
        }
        Vector3f desc = bound.getWorldTranslation();
        Ray r = new Ray(src, new Vector3f(desc.x - src.x,desc.y - src.y, desc.z - src.z));
        CollisionResults results = new CollisionResults();
        r.collideWith(bound.getWorldBound(), results);
        return true;
    }
    
    private boolean canSeeDoor(Spatial door){
        Vector3f ap = agent.getWorldTranslation();
        Vector3f dp = door.getWorldTranslation();
        Ray ray = new Ray(ap, new Vector3f(dp.x - ap.x,dp.y - ap.y,dp.z-ap.z));
        
        
        CollisionResults results = new CollisionResults();
        ray.collideWith(door.getWorldBound(), results);
        logger.log(Level.INFO, results.toString());
        return true;
    }
    
    private void seekForDoorControl(){
        seenDoors = new ArrayList<>();
        for (Spatial s : envi.getDoorsNode().getChildren()){
            if (agent.getWorldTranslation().distance(s.getWorldTranslation()) <= agent.getVision()&& canSeeDoor(s)) {
                seenDoors.add(s);
            }
        }
    }
    
    private void adjustDirectionByTarget(Spatial s){
        super.adjustDirectionByTarget(s.getWorldTranslation());
    }
    
    
    
    @Override
    public void update(float fps) {
        if (seenDoors == null || seenDoors.isEmpty()){
            explore.update(fps);
        } else {
            if (pathFinding.completedPath()){
                Node node = null;
                Spatial c = null;
                float min = 100;
                for (Spatial s : seenDoors){
                    escapableDoor(s);
                }
            }
        }
        seekForDoorControl();
        
        
    }
    
}
