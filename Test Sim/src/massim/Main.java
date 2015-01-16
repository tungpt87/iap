package massim;

import massim.node.AgentNode;
import massim.node.Environment;
import com.jme3.ai.agents.util.control.MonkeyBrainsAppState;
//import com.jme3.ai.navigation.detour.NavMesh;
import com.jme3.ai.navigation.detour.NavMeshQuery;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import org.fabian.csg.scene.CSGNode;
import com.jme3.ai.navmesh.*;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.bullet.control.GhostControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.debug.Arrow;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
/**
 * test
 * @author TungPT
 */
public class Main extends SimpleApplication implements AnimEventListener, ActionListener{
    private static Main app;
    private BulletAppState bulletAppState;
	
    private RigidBodyControl wall_phy;
    private CSGNode wall;
    private RigidBodyControl floor_phy;
    private Box floor;
    private RigidBodyControl people_phy;
    private Box people;
    private RigidBodyControl wall_blank_phy;
    private Box wall_blank;

    private AgentNode agentNode;
    ArrayList<RigidBodyControl> envi;
    private MonkeyBrainsAppState brainsAppState = MonkeyBrainsAppState.getInstance(); 
    private AnimControl animControl;
    private AnimChannel animChannel;
    /**
     * Getter for brainsAppState instance
     * @return 
     */
    public MonkeyBrainsAppState getBrainsAppState() {
        return brainsAppState;
    }

    private NavMeshQuery query;
    
    NavMesh navMesh;

    Environment env;
    
    /**
     * Environment instance
     * @return 
     */
    public Environment getEnv() {
        return env;
    }
    
    /**
     * NavMesh instance. 
     * For each building model, NavMesh can be generated from the beginning 
     * and reused
     * @return 
     */
    public NavMesh getNavMesh() {
        return navMesh;
    }
    
    
    public static void main(String[] args) {
        app = new Main();
        app.start();
    }

    
   /**
    * Main instance
    * @return 
    */
    public static Main app(){
        return app;
    }
    
    
    /**
     * Base function of jME program, always be called when the program starts
     */
    @Override
    public void simpleInitApp() {
        initEnvironment();
        initAgents();
        
        if (Config.DEBUG==true){
            //Enable physics space debug, will be disable before release
            PhysicsSpace.getPhysicsSpace().enableDebug(this.assetManager);
            //Enable Axises for debuging
            attachCoordinateAxes(Vector3f.ZERO);
        }
        //Camera config
        flyCam.setMoveSpeed(500);
        cam.setLocation(new Vector3f(0,200,0));
        bulletAppState.startPhysics();
        
        brainsAppState.start();
        
        //Setup timer
        Timer updateTimer = new Timer(Config.UPDATE_CYCLE, this);
        updateTimer.setDelay(Config.UPDATE_CYCLE);
        updateTimer.start();
    }
    
    private void clearOldModel(){
        rootNode.detachAllChildren();
        bulletAppState.getPhysicsSpace().destroy();
        
    }
    
    private void updateModel(){
        try {
            String flag = Utility.readFile(Config.FLAGS_FILE, StandardCharsets.UTF_8);
            if (flag.equals("1")){
                Utility.writeStringToFile(Config.FLAGS_FILE, "0");
                clearOldModel();
                initEnvironment();
                initAgents();
            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void initEnvironment(){
        //BrainsAppState, handles agents' behavior
        brainsAppState.setApp(this);
        stateManager.attach(brainsAppState);
        
        //BulletAppState, handles physics
        bulletAppState = new BulletAppState();
	bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
	bulletAppState.setDebugEnabled(true);
	stateManager.attach(bulletAppState);
        		
        //InputManager, handles user's input
        inputManager.addMapping("CharLeft", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("CharRight", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("CharGo", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("CharBack", new KeyTrigger(KeyInput.KEY_S));
        
        //Initiate environment from xml file. This file is generated 
        //by IFCParser which is done by WenFeng
        env = new Environment(Config.MODEL_FILE);
        rootNode.attachChild(env);
        
        //Add environment's body control into physics space
        bulletAppState.getPhysicsSpace().add(env.getBodyControl());
        
        for (GhostControl gc : env.getDoorControls()){
            bulletAppState.getPhysicsSpace().add(gc);
        }
    }
    
    private void initAgents(){
        //Initiate intelligent agent
        agentNode = new AgentNode();
        if (agentNode.getBodyPhy() != null)
            bulletAppState.getPhysicsSpace().add(agentNode.getBodyPhy());  
        
        rootNode.attachChild(agentNode);
        
        if(agentNode.getAgent() != null){
            brainsAppState.addAgent(agentNode.getAgent());
        }
    }
    /**
     * This function is called by the game's main loop. 
     * @param tpf 
     */
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        super.simpleUpdate(tpf);
        
//        if (agentNode.getBodyPhy() != null)
//            agentNode.getBodyPhy().activate();
        brainsAppState.update(tpf);
        agentNode.update(tpf);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void attachCoordinateAxes(Vector3f pos){
        Arrow arrow = new Arrow(new Vector3f(500f, 0f, 0f));
        arrow.setLineWidth(20); // make arrow thicker
        putShape(arrow, ColorRGBA.Red).setLocalTranslation(pos);
        
        arrow = new Arrow(new Vector3f(0f, 500f, 0f));
        arrow.setLineWidth(20); // make arrow thicker
        putShape(arrow, ColorRGBA.Green).setLocalTranslation(pos);

        arrow = new Arrow(new Vector3f(0f, 0f,500f));
        arrow.setLineWidth(20); // make arrow thicker
        putShape(arrow, ColorRGBA.Blue).setLocalTranslation(pos);
    }   
 
    private Geometry putShape(Mesh shape, ColorRGBA color){
        Geometry g = new Geometry("coordinate axis", shape);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        rootNode.attachChild(g);
        
        return g;
    }
    
    public void actionPerformed(ActionEvent e){
        updateModel();
    }
    
}
