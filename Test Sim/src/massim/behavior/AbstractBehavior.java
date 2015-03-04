/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package massim.behavior;

import com.jme3.ai.navmesh.Cell;
import com.jme3.math.Vector3f;
import java.util.Random;
import java.util.logging.Logger;
import massim.Config;
import massim.node.AgentNode;
import massim.node.Environment;

/**
 *
 * @author TungPT
 */
public abstract class AbstractBehavior {
    protected Environment envi;
    protected AgentNode agent;
    Random rndGenerator = new Random();
    protected static final Logger logger = Logger.getLogger(AgentNode.class.getName());
    abstract public void update(float fps);

    public AbstractBehavior(Environment envi, AgentNode agent) {
        this.envi = envi;
        this.agent = agent;
    }
    
    public void adjustDirectionByTarget( Vector3f tar){
        Vector3f cur = agent.getGeometry().getWorldTranslation();
        
        float dis = cur.distance(tar);
        float x = (tar.x - cur.x)/dis*agent.getVelocity();
        float z = (tar.z - cur.z)/dis*agent.getVelocity();
        agent.getBodyPhy().setWalkDirection(new Vector3f(x,0f,z));
        agent.getBodyPhy().setViewDirection(new Vector3f(x,0f,z));
         
    }
    
    public boolean didReachTarget(Vector3f tar){
        return tar.distance(agent.getGeometry().getWorldTranslation()) <= Config.AGENT_DEVIATION;
    }
    
}
