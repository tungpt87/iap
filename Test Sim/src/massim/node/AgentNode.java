/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package massim.node;

import com.jme3.ai.agents.behaviors.npc.SimpleMainBehavior;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.ai.agents.Agent;
import com.jme3.ai.navmesh.DebugInfo;
import com.jme3.ai.navmesh.NavMeshPathfinder;
import com.jme3.ai.navmesh.Path;
import com.jme3.ai.navmesh.Path.Waypoint;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import massim.Config;
import massim.DataManager;
import massim.Main;
import java.util.Random;
import massim.Utility;

/**
 *
 * @author TungPT
 */
public class AgentNode extends Node implements AnimEventListener{
    private static final Logger logger = Logger.getLogger(AgentNode.class.getName());
    private BetterCharacterControl bodyPhy;
    private SimpleMainBehavior behavior;
    private Agent agent;
    private Geometry people_geo;
    private boolean beh;
    private NavMeshPathfinder pathFinder;
    private Path path;
    private Waypoint nextTarget=null;
    private int wpi;
    
    //Characteristics
    private int age;
    private byte gender;
    private float radius = Config.AGENT_RADIUS;
    //
    private float velocity = 10;
    private int agentId = 0;
    private static int idCounter = 0;
    
    private boolean isGeneratingPath;
    
    public Path getPath() {
        return path;
    }

    public int getAgentId() {
        return agentId;
    }
    
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public byte getGender() {
        return gender;
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }
    
    public Vector3f getPosition(){
        return people_geo.getWorldTranslation();
    }        
            
    public SimpleMainBehavior getBehavior() {
        return behavior;
    }

    /**
     * Refers to the agent's brain
     * @return 
     */
    public Agent getAgent() {
        return agent;
    }
    
    /**
     * Agent's physics body
     * @return 
     */
    public BetterCharacterControl getBodyPhy() {
        return bodyPhy;
    }
    /**
     * Constructor
     */
    public AgentNode(String name) {
        super(name);
        //Init agent in shape of a box
        Box people = new Box(1f,2f,1f);
        
        people_geo = new Geometry("Agent", people);
        
        
        Material people_mat = new Material(Main.app().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key2 = new TextureKey("Textures/Terrain/Rock/Rock.PNG");
        key2.setGenerateMips(true);
        Texture tex2 = Main.app().getAssetManager().loadTexture(key2);
        people_mat.setTexture("ColorMap", tex2);
	people_geo.setMaterial(people_mat);
        attachChild(people_geo);
        people_geo.setLocalTranslation(0f, 20f, 0f);
        
        bodyPhy = new BetterCharacterControl(1.5f, 4f, 70f);
        
        
        bodyPhy.setJumpForce(new Vector3f(0,5f,0)); 
        bodyPhy.setGravity(new Vector3f(1,100f,1));
        
        bodyPhy.warp(new Vector3f(0,20,0)); // warp character into landscape at particular location
        people_geo.addControl(bodyPhy);
        
        initBehavior();
        
        agentId = idCounter;
        idCounter++;
    }

   
    /**
     * Init Agent's behavior
     */
    private void initBehavior(){
//        initAgent();
        
        //Init path finder
        if (Main.app().getEnv().getNavMesh() != null){
            pathFinder = new NavMeshPathfinder(Main.app().getEnv().getNavMesh());
            pathFinder.setEntityRadius(Math.max(radius, Config.AGENT_DEVIATION));
            
            randomWalk();
        }
        

    }
    
    private synchronized void randomWalk(){
        isGeneratingPath = true;
        logger.log(Level.INFO,"Generate random path");
        Vector3f tar = people_geo.getWorldTranslation();
        Random rndGenerator = new Random();
        
        ArrayList<GhostControl> dcs = Main.app().getEnv().getDoorControls();
        GhostControl gc = dcs.get(rndGenerator.nextInt(dcs.size()));
        
        if (gc.getPhysicsLocation().distance(people_geo.getWorldTranslation())<40){
            randomWalk();
            return;
        }
                
                
                
        tar = gc.getPhysicsLocation();
        tar.setY(1f);
        pathFinder.setPosition(people_geo.getWorldTranslation());     //set start position
        DebugInfo di = new DebugInfo();
        boolean success = pathFinder.computePath(new Vector3f(tar),di); //compute path to destination
        logger.log(Level.INFO,"PATH FINDING DEBUG INFO: {0}",new Object[]{di.toString()});
//        if (!success) {
//            randomWalk();
//            return;
//        }
        //Get path from path finder
        path = pathFinder.getPath();
        removeDuplicates();
        wpi = -1;
        
        Utility.showPath(this, ColorRGBA.Blue);
        isGeneratingPath = false;
    }
    
    private void initRays(){
        Ray r;
    }
    
    /**
     * Init agent's brain with attributes
     */
    private void initAgent(){
        agent = new Agent("people", people_geo);
        agent.setMoveSpeed(30); 
        agent.setRotationSpeed(30);
        //used for steering behaviors in com.jme3.ai.agents.behaviors.npc.steering
        agent.setMass(70f);
        agent.setMaxForce(3);
        
        
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
    
    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public void adjustDirectionByTarget(Waypoint waypoint){
        Vector3f cur = people_geo.getWorldTranslation();
        Vector3f tar = waypoint.getPosition();
        float dis = cur.distance(tar);
        float x = (tar.x - cur.x)/dis*velocity;
        float z = (tar.z - cur.z)/dis*velocity;
        bodyPhy.setWalkDirection(new Vector3f(x,0f,z));
        bodyPhy.setViewDirection(new Vector3f(x,0f,z));
        
    }
    public boolean didReachTarget(Waypoint waypoint){
        logger.log(Level.INFO,"Distance to target: {0}", new Object[]{waypoint.getPosition().distance(people_geo.getWorldTranslation())});
        return waypoint.getPosition().distance(people_geo.getWorldTranslation()) <= Config.AGENT_DEVIATION;
    }
    public void update(float fps){
        if (path == null || isGeneratingPath) return;
        
        if (nextTarget==null || didReachTarget(nextTarget)){
            wpi ++;
            
            if (wpi < path.getWaypoints().size()){
                nextTarget = path.getWaypoints().get(wpi);
            }
            else {
                bodyPhy.setWalkDirection(Vector3f.NAN);
                path = null;
                nextTarget = null;
                randomWalk();
                return;
            }
        }
        if (nextTarget != null){
            this.adjustDirectionByTarget(nextTarget);
            logger.log(Level.INFO,"Agent Position: {0}",new Object[]{people_geo.getWorldTranslation()});
            logger.log(Level.INFO, "Direction: {0} Target: {1}", new Object[]{bodyPhy.getWalkDirection().toString(), nextTarget.getPosition().toString()});
        }
        DataManager.logByAgent(this);
    }
}
