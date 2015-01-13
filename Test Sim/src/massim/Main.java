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
/**
 * test
 * @author TungPT
 */
public class Main extends SimpleApplication implements AnimEventListener{
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
        
//        inputManager.addListener(new AnalogListener(){
//
//			@Override
//			public void onAnalog(String binding, float value, float arg2) {
//				// TODO Auto-generated method stub
//				Vector3f pos = agentNode.getBodyPhy().getPhysicsLocation();
//				if (binding.equals("CharLeft")) {
//					pos.x -= 0.1f;
//		        } else if (binding.equals("CharRight")) {
//		        	pos.x += 0.1f;
//		        } else if (binding.equals("CharGo")) {
//		        	pos.z -= 0.1f;
//		        } else if (binding.equals("CharBack")) {
//		        	pos.z += 0.1f;
//		        }
//				agentNode.getBodyPhy().setPhysicsLocation(pos);       
//			}
//        },"CharLeft", "CharRight", "CharGo","CharBack");
        
        //Initiate environment from xml file. This file is generated 
        //by IFCParser which is done by WenFeng
        env = new Environment("assets/TestResult.xml");
        rootNode.attachChild(env);
        
        //Add environment's body control into physics space
//        bulletAppState.getPhysicsSpace().add(env.getBodyControl());
        
        //Initiate intelligent agent
        agentNode = new AgentNode();
        if (agentNode.getBodyPhy() != null)
            bulletAppState.getPhysicsSpace().add(agentNode.getBodyPhy());  
        
        rootNode.attachChild(agentNode);
        
//        animControl = agentNode.getControl(AnimControl.class);
//        animControl.addListener(this);
//        animChannel = animControl.createChannel();
        if(agentNode.getAgent() != null){
            brainsAppState.addAgent(agentNode.getAgent());
        }
        
        //Camera config
        flyCam.setMoveSpeed(0);
        cam.setLocation(new Vector3f(0,200,0));
        bulletAppState.startPhysics();
        
        brainsAppState.start();
        
        //Enable physics space debug, will be disable before release
        PhysicsSpace.getPhysicsSpace().enableDebug(this.assetManager);
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
}
