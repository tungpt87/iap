/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package massim.behavior;

import com.jme3.ai.navmesh.NavMeshPathfinder;
import com.jme3.ai.navmesh.Path;
import com.jme3.ai.navmesh.Path.Waypoint;
import com.jme3.math.Vector3f;
import java.util.logging.Level;
import massim.Config;
import massim.Main;
import massim.node.AgentNode;
import massim.node.Environment;

/**
 *
 * @author TungPT
 */
public class PathFindingBehavior extends AbstractBehavior{
    private Path path;
    private Path.Waypoint nextTarget=null;
    int wpi;
    boolean isGeneratingPath = false;
    private NavMeshPathfinder pathFinder;
    boolean completed = true;
    
    public PathFindingBehavior(Environment envi, AgentNode agent) {
        super(envi, agent);
        pathFinder = new NavMeshPathfinder(Main.app().getEnv().getNavMesh());
        pathFinder.setEntityRadius(Math.max(agent.getRadius(), Config.AGENT_DEVIATION));
    }

    private boolean didReachTarget(Waypoint wp){
        return didReachTarget(new Vector3f(wp.getPosition().x,agent.getGeometry().getWorldTranslation().y,wp.getPosition().z));
    }

    private void adjustDirectionByTarget(Waypoint wp){
        adjustDirectionByTarget(wp.getPosition());
    }
    public boolean completedPath(){
        return completed;
    }
    /**
     * Remove duplicates from ArrayList
     * @param l
     * @return 
     */
    private void removeDuplicates() {
    // ... the list is already populated
        int i = 0;
        while (i < path.getWaypoints().size()-1){
            Waypoint wp1 = path.getWaypoints().get(i);
            Waypoint wp2 = path.getWaypoints().get(i+1);
            if (wp1.getPosition().equals(wp2.getPosition())){
                path.getWaypoints().remove(i);
            } else {
                i ++;
            }
        }
    }
    public boolean gotoTarget(Vector3f tar){
        pathFinder.setPosition(agent.getGeometry().getWorldTranslation());
        boolean succeed = pathFinder.computePath(tar);
        path = pathFinder.getPath();
        completed = false;
        wpi = 0;
        nextTarget = null;
        return succeed;
    }

    
    @Override
    public void update(float fps) {
        if (path == null || isGeneratingPath) return;
        
        if (nextTarget==null || didReachTarget(nextTarget)){
            wpi ++;

            if (wpi < path.getWaypoints().size()){
                nextTarget = path.getWaypoints().get(wpi);
            }
            else {
//                agent.getBodyPhy().setWalkDirection(Vector3f.NAN);
                path = null;
                nextTarget = null;
                completed = true;

                return;
            }
        }
        if (nextTarget != null){
            this.adjustDirectionByTarget(nextTarget);
        }
    }
    
}
