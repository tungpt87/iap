/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.ai.agents.behaviors.npc.SimpleMainBehavior;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.npc.SimpleMoveBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.PathFollowBehavior;
import com.jme3.ai.navmesh.NavMeshPathfinder;
import com.jme3.ai.navmesh.Path;
import com.jme3.ai.navmesh.Path.Waypoint;
import com.jme3.math.Vector3f;
import java.util.ArrayList;


/**
 *
 * @author TungPT
 */
public class AgentNode extends Node{
    private RigidBodyControl bodyPhy;
    private SimpleMainBehavior behavior;
    private Agent agent;
    Geometry people_geo;
    
    NavMeshPathfinder pathFinder;
    public SimpleMainBehavior getBehavior() {
        return behavior;
    }

    public Agent getAgent() {
        return agent;
    }
    public RigidBodyControl getBodyPhy() {
        return bodyPhy;
    }
    public AgentNode() {
        super("Agent");
        Box people = new Box(2f,2f,2f);
        
        people_geo = new Geometry("Agent", people);
        
        
        Material people_mat = new Material(Main.app().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key2 = new TextureKey("Textures/Terrain/Rock/Rock.PNG");
        key2.setGenerateMips(true);
        Texture tex2 = Main.app().getAssetManager().loadTexture(key2);
        people_mat.setTexture("ColorMap", tex2);
	people_geo.setMaterial(people_mat);
        attachChild(people_geo);
        
        
        bodyPhy = new RigidBodyControl(0.1f);
        
        
        people_geo.addControl(bodyPhy);
        bodyPhy.setPhysicsLocation(new Vector3f(10f,10f,10f));
        initBehavior();
    }

   
    
    private void initBehavior(){
        agent = new Agent("Agent", people_geo);
        
        agent.setMoveSpeed(5); 
        agent.setRotationSpeed(30);
        //used for steering behaviors in com.jme3.ai.agents.behaviors.npc.steering
        agent.setMass(40);
        agent.setMaxForce(3);
        behavior = new SimpleMainBehavior(agent);
        SimpleMoveBehavior moveBeh = new SimpleMoveBehavior(agent);
        
        moveBeh.setTargetPosition(new Vector3f(50f,0f,50f));
        pathFinder = new NavMeshPathfinder(Main.app().getEnv().getNavMesh());
        pathFinder.setPosition(this.getWorldTranslation());
        pathFinder.computePath(new Vector3f(25f, .5f, 45f));
        
        Path path = pathFinder.getPath();
        
        ArrayList<Waypoint> wayPoints = path.getWaypoints();
        ArrayList<Vector3f> pos = new ArrayList<>();
        
        for(Waypoint wp : wayPoints){
            pos.add(wp.getPosition().setY(0f));
            System.out.println(wp.getPosition().toString());
        }
        
        PathFollowBehavior pathFollow = new PathFollowBehavior(agent, pos, 0.5f);
        pathFollow.setVelocity(new Vector3f(10f,0f,10f));
        
//        pathFollow.setActive(true);
        behavior.addBehavior(pathFollow);
        
        agent.setMainBehavior(behavior);
        
        
        
        
    }
}
