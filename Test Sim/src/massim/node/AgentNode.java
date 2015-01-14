/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package massim.node;

import com.jme3.ai.agents.behaviors.npc.SimpleMainBehavior;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.npc.steering.PathFollowBehavior;
import com.jme3.ai.navmesh.NavMeshPathfinder;
import com.jme3.ai.navmesh.Path;
import com.jme3.ai.navmesh.Path.Waypoint;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.ColorRGBA;
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
import massim.Main;


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
    private float radius = 2f;
    
    //
    private float velocity = 3;
    
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
    public AgentNode() {
        super("Agent");
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
        people_geo.setLocalTranslation(0f, 4f, 0f);
        
        bodyPhy = new BetterCharacterControl(1.5f, 4f, 70f);
        
        bodyPhy.setJumpForce(new Vector3f(0,5f,0)); 
        bodyPhy.setGravity(new Vector3f(1,100f,1));
        
        bodyPhy.warp(new Vector3f(10,2,10)); // warp character into landscape at particular location
        people_geo.addControl(bodyPhy);
        
        initBehavior();
    }

   
    /**
     * Init Agent's behavior
     */
    private void initBehavior(){
//        initAgent();
        
        //Init path finder
        if (Main.app().getEnv().getNavMesh() != null){
            pathFinder = new NavMeshPathfinder(Main.app().getEnv().getNavMesh());
            pathFinder.setPosition(people_geo.getWorldTranslation());     //set start position
            pathFinder.computePath(new Vector3f(-50f, 1f, 70f)); //compute path to destination

            //Get path from path finder
            path = pathFinder.getPath();   
            wpi = -1;
        }
        

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
    public ArrayList<Vector3f> removeDuplicates(ArrayList<Vector3f> l) {
    // ... the list is already populated
        Set<Vector3f> s = new TreeSet<>(new Comparator<Vector3f>() {

            @Override
            public int compare(Vector3f o1, Vector3f o2) {

                return ((Vector3f)o1).equals((Vector3f)o2)==true?0:1;
            }
        });
        s.addAll(l);
        Vector3f[] a = new Vector3f[s.size()];
        s.toArray(a);
        l = new ArrayList<>(Arrays.asList(a));
        return (l);
    }
    /**
     * Draw path from points, still hasn't worked
     * @param points 
     */
    private void drawPath(ArrayList<Vector3f> points){
        Mesh mesh = new Mesh();
        mesh.setMode(Mesh.Mode.Lines);
        float[] vectors = new float[points.size()*3];
        for(int i = 0; i< vectors.length; i=i+3){
            vectors[i]=points.get(i/3).x;
            vectors[i+1]=points.get(i/3).y;
            vectors[i+2]=points.get(i/3).z;
        }
        int[] indices = new int[points.size()*2];
        for(int i = 0;i<indices.length;++i)
            indices[i] = i/2+i%2;
        mesh.setBuffer(VertexBuffer.Type.Position, 3, vectors);
        mesh.setBuffer(VertexBuffer.Type.Index, 2, indices);
        mesh.updateBound();
        Material wireMaterial = new Material(Main.app().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        wireMaterial.getAdditionalRenderState().setWireframe(true);
        wireMaterial.setBoolean("UseMaterialColors", true);
        wireMaterial.setColor("Diffuse", ColorRGBA.Blue);
        Geometry geo = new Geometry("Path", mesh);
        geo.setCullHint(Spatial.CullHint.Never);
        geo.setMaterial(wireMaterial);
        attachChild(geo);
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
        double angle = Math.atan((tar.x-cur.x)/(tar.z-cur.z));
        float x = (float) (Math.cos(angle)*velocity);
        float z = (float) (Math.sin(angle)*velocity);
        bodyPhy.setWalkDirection(new Vector3f(z,0f,x));
        bodyPhy.setViewDirection(new Vector3f(z,0f,x));
    }
    public boolean didReachTarget(Waypoint waypoint){
        logger.log(Level.INFO,"Distance to target: {0}", new Object[]{waypoint.getPosition().distance(people_geo.getWorldTranslation())});
        return waypoint.getPosition().distance(people_geo.getWorldTranslation()) <= radius;
    }
    public void update(float fps){
        if (path == null) return;
        if (nextTarget==null || didReachTarget(nextTarget)){
            wpi ++;
            if (wpi < path.getWaypoints().size())
                nextTarget = path.getWaypoints().get(wpi);
            else {
                bodyPhy.setWalkDirection(Vector3f.NAN);
                return;
            }
        }
        if (nextTarget != null){
            this.adjustDirectionByTarget(nextTarget);
            logger.log(Level.INFO, "Direction: {0} Target: {1}", new Object[]{bodyPhy.getWalkDirection().toString(), nextTarget.getPosition().toString()});
        }
    }
}
