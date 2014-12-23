package mygame;

import com.jme3.ai.agents.util.control.MonkeyBrainsAppState;
import com.jme3.ai.navigation.detour.DetourBuilder;
//import com.jme3.ai.navigation.detour.NavMesh;
import com.jme3.ai.navigation.detour.NavMeshCreateParams;
import com.jme3.ai.navigation.detour.NavMeshQuery;
import com.jme3.ai.navigation.detour.Status;
import com.jme3.ai.navigation.recast.CompactHeightfield;
import com.jme3.ai.navigation.recast.Config;
import com.jme3.ai.navigation.recast.Context;
import com.jme3.ai.navigation.recast.ContourSet;
import com.jme3.ai.navigation.recast.Heightfield;
import com.jme3.ai.navigation.recast.PolyMesh;
import com.jme3.ai.navigation.recast.PolyMeshDetail;
import com.jme3.ai.navigation.recast.RecastBuilder;
import com.jme3.ai.navigation.tilecache.TileFlags;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.SimpleBatchNode;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import java.io.File;
import java.util.ArrayList;
import jme3tools.optimize.GeometryBatchFactory;

import mygame.Environment;
import org.fabian.csg.scene.CSGNode;

import mygame.AgentNode;

import com.jme3.ai.navmesh.*;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.mesh.IndexBuffer;
import com.jme3.util.BufferUtils;
import org.critterai.nmgen.*;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {
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

    private NavMeshQuery query;
    
    NavMesh navMesh;

    Environment env;

    public Environment getEnv() {
        return env;
    }
    public NavMesh getNavMesh() {
        return navMesh;
    }
    
    public static void main(String[] args) {
        app = new Main();
        app.start();
    }

    public static Main app(){
        return app;
    }
    
    @Override
    public void simpleInitApp() {
        brainsAppState.setApp(this);
        
        bulletAppState = new BulletAppState();
	bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
	bulletAppState.setDebugEnabled(true);
	stateManager.attach(bulletAppState);
        		
        inputManager.addMapping("CharLeft", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("CharRight", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("CharGo", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("CharBack", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addListener(new AnalogListener(){

			@Override
			public void onAnalog(String binding, float value, float arg2) {
				// TODO Auto-generated method stub
				Vector3f pos = agentNode.getBodyPhy().getPhysicsLocation();
				if (binding.equals("CharLeft")) {
					pos.x -= 0.1f;
		        } else if (binding.equals("CharRight")) {
		        	pos.x += 0.1f;
		        } else if (binding.equals("CharGo")) {
		        	pos.z -= 0.1f;
		        } else if (binding.equals("CharBack")) {
		        	pos.z += 0.1f;
		        }
				agentNode.getBodyPhy().setPhysicsLocation(pos);
				//cam.setLocation(people_phy.getPhysicsLocation());
                                System.out.println("Agent Body: "+agentNode.getBodyPhy().isActive());
			}
                        
        	
        },"CharLeft", "CharRight", "CharGo","CharBack");
        
       
        
        env = new Environment("/Users/TungPT/Google Drive/IAP Works/TestGeo.xml");
        rootNode.attachChild(env);
        bulletAppState.getPhysicsSpace().add(env.getBodyControl());
        
        
        
        agentNode = new AgentNode();
//        agentNode.getBodyPhy().setPhysicsLocation(new Vector3f(0f,0.1f,0f));
//        agentNode.setLocalTranslation(0f, 100f, 0f);
        bulletAppState.getPhysicsSpace().add(agentNode.getBodyPhy());  
        rootNode.attachChild(agentNode);
        
        if(agentNode.getAgent() != null){
            brainsAppState.addAgent(agentNode.getAgent());
        }
        flyCam.setMoveSpeed(0);
        cam.setLocation(new Vector3f(10,35,10));
        bulletAppState.startPhysics();
        PhysicsSpace.getPhysicsSpace().enableDebug(this.assetManager);
//        brainsAppState.start(); 
        
//        agentNode.getBodyPhy().activate();
//        env.getBodyControl().activate();
    }

    
    
    
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        super.simpleUpdate(tpf);

        
//        brainsAppState.update(tpf);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
