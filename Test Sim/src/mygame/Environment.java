/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;
import com.jme3.ai.navmesh.NavMesh;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import java.io.File;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.mesh.IndexBuffer;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import jme3tools.optimize.GeometryBatchFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.fabian.csg.*;
import org.critterai.nmgen.NavmeshGenerator;
import org.critterai.nmgen.TriangleMesh;
import org.fabian.csg.scene.CSGNode;
import org.fabian.csg.shapes.CubeBrush;

/**
 *
 * @author TungPT
 */
public class Environment extends BatchNode{
    private NodeList groundList;
    private Material wall_mat;
    private String xmlFilePath;
    
    Mesh mesh;
    
    private ArrayList<Vector3f> vertices;
    private ArrayList<Integer> indexes;

    boolean newWall;
    
    private int[][] links;
    
    NavMesh navMesh;
    
    RigidBodyControl bodyControl;

    public RigidBodyControl getBodyControl() {
        return bodyControl;
    }
    
    public NavMesh getNavMesh() {
        return navMesh;
    }
    ////////////////////////////////////////////////////
    public Mesh getMesh() {
        return mesh;
    }
    
    /**
     * Constructor
     * @param xml : path to xml file
     */
    public Environment(String xml) {
        super("Envi");
        xmlFilePath = xml;
        groundList = loadXML();
        wall_mat = new Material(Main.app().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key = new TextureKey("Textures/Terrain/Pond/Pond.jpg");
	key.setGenerateMips(true);
               
        Texture tex = Main.app().getAssetManager().loadTexture(key);
        tex.setWrap(Texture.WrapMode.Repeat);
	wall_mat.setTexture("ColorMap", tex);
        
        makeBuilding();
        
    }
    
    /**
     * Parses the given XML file
     * @return : a list of node which represent floors data
     */
    private NodeList loadXML(){
        //String str = file.readString();
        Document doc = null;
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder(); 
        //	InputSource is = new InputSource(new StringReader(str));
            File file = new File(xmlFilePath);
            doc = db.parse(file);
        }
        catch(Exception e){
            System.out.printf(e.toString());
        }
        NodeList grounds = doc.getElementsByTagName("Ground");
        
        return grounds;
    }
    
    /**
     * Make the building 3D model
     */
    private void makeBuilding(){
//        This is for building the environment mesh, but no longer used
//        vertices = new ArrayList<>();   //List of all points
//        indexes = new ArrayList<>();    //List of indices
        buildFloorAsBox();
        for (int i = 0; i< groundList.getLength(); i++){	
            Element ground = (Element)groundList.item(i);
            NodeList walls = ground.getElementsByTagName("Wall");
            for(int j = 0; j < walls.getLength(); j++){
                this.initiateWall((Element)walls.item(j));
            }

        }
        
        //Batches all objects within the environment and have the same material
        //to 1 object.
        GeometryBatchFactory.optimize(this);
        
        //Create the collision shape of the environment
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(this.getChild(0));

        //Create environment's body control as RigidBodyControl so it appears and 
        //touchable
        bodyControl = new RigidBodyControl(sceneShape,0f);
        
        this.getChild(0).addControl(bodyControl);
        buildNavMesh();
    }
    
    /**
     * Build Navigation mesh of the environment
     */
    private void buildNavMesh(){
        //Get mesh of the environment which is generated above
        mesh = ((Geometry)this.getChild(0)).getMesh();
        Vector3f[] vectors3f = BufferUtils.getVector3Array(mesh.getFloatBuffer(VertexBuffer.Type.Position));
        IndexBuffer indexBuffer = mesh.getIndexBuffer();
        
        
        int[] indices = new int[indexBuffer.size()];
        for (int i = 0; i < indices.length; i++){
            indices[i] = indexBuffer.get(i);
        }
        float[] vectors = new float[vectors3f.length*3];
        for(int i = 0; i< vectors.length; i=i+3){
            vectors[i]=vectors3f[i/3].x;
            vectors[i+1]=vectors3f[i/3].y;
            vectors[i+2]=vectors3f[i/3].z;
        }
        
        //Initiates Navigation Mesh Generator
        NavmeshGenerator meshGen = new NavmeshGenerator(1f, 2f, 0f, 1f, 45f, false, 1f, 1, true, 2, 1, 20, 1, 100, 1, 10);
        TriangleMesh triMesh = meshGen.build(vectors, indices, null);
        indices = triMesh.indices;
        vectors = triMesh.vertices;
        
        //Initiate Mesh for Navigation
        Mesh mesh4Nav = new Mesh();
        mesh4Nav.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vectors));
        mesh4Nav.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indices));
        mesh4Nav.updateBound();
        Geometry geo = new Geometry("NavGeo", mesh4Nav);
        geo.setMaterial(wall_mat);
        attachChild(geo);
        
        //Set material of the mesh as wire so it appears as a graph made by triangles
        Material wireMaterial = new Material(Main.app().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        wireMaterial.getAdditionalRenderState().setWireframe(true);
        
        geo.setCullHint(Spatial.CullHint.Never);
        geo.setMaterial(wireMaterial);
        
        navMesh = new NavMesh();
        navMesh.loadFromMesh(mesh4Nav);
        
        navMesh.linkCells();
        
    }
    /**
     * Build floor from vertices
     * No longer used
     */
//    private void buildFloorFromVertices(){
//        generateIndexes();
//        generateLinks();
//        for(int i = 0;i<vertices.size();i++){
//            for(int j=0;j<vertices.size();j++)
//                System.out.print(links[i][j]+" ");
//            System.out.print("\n");
//        }
//            
//                
//        for(int i=0;i<vertices.size()-1;i++)
//            for(int j=i;j<vertices.size();j++)
//                for(int k=0;k<vertices.size();k++){
//                    if(links[i][k]==1 && links[j][k]==1 && links[i][j]==1) {
//                        indexes.add(i);
//                        indexes.add(j);
//                        indexes.add(k);
//                    }
//                }
//        Mesh floorMesh = new Mesh();
//        floorMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices.toArray(new Vector3f[vertices.size()])));
//        floorMesh.setBuffer(Type.Index, 3,BufferUtils.createIntBuffer(convertIntegers(indexes)));
//        floorMesh.updateBound();
//        Geometry floorGeo = new Geometry("floor", floorMesh);
//        floorGeo.setMaterial(wall_mat);
//        attachChild(floorGeo);
//    }
    
    /**
     * Build floor as a rectangle box
     */
    private void buildFloorAsBox(){
        CubeBrush floor = new CubeBrush(new Vector3f(0f,0f,0f),new Vector3f(60f,0.05f,60f));
        floor.setType(CSG.BrushType.ADDITIVE);
        CSGNode floor_geo = new CSGNode();
        floor_geo.addBrush(floor);
        floor_geo.setLocalTranslation(new Vector3f(30f, 0f, 30f));
		
	Material floor_mat = new Material(Main.app().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key3 = new TextureKey("Textures/Terrain/Pond/Pond.jpg");
        key3.setGenerateMips(true);
        Texture tex3 = Main.app().getAssetManager().loadTexture(key3);
        tex3.setWrap(Texture.WrapMode.Repeat);
        floor_mat.setTexture("ColorMap", tex3);
        floor_geo.setMaterial(wall_mat);
	floor_geo.regenerate();
        attachChild(floor_geo);
    }
    
    /**
     * Build a continuous wall from given data
     * @param wallData 
     */
    private void initiateWall(Element wallData){
        NodeList points = wallData.getElementsByTagName("Point");
        NodeList xs = wallData.getElementsByTagName("X");
        NodeList ys = wallData.getElementsByTagName("Y");
        Double sX = Double.parseDouble(xs.item(0).getTextContent()),sY = Double.parseDouble(ys.item(0).getTextContent());
        
        newWall = true;
        for (int i = 0; i < points.getLength()-6;i += 3){
            
            Element startPoint = (Element)points.item(i);
            Point p1 = new Point(Double.parseDouble(startPoint.getChildNodes().item(0).getTextContent()),
                    Double.parseDouble(startPoint.getChildNodes().item(1).getTextContent()),
                    Double.parseDouble(startPoint.getChildNodes().item(2).getTextContent()),
                    sX,
                    sY);
            Element point = (Element)points.item(i+3);
            Point p2 = new Point(Double.parseDouble(point.getChildNodes().item(0).getTextContent()),
                    Double.parseDouble(point.getChildNodes().item(1).getTextContent()),
                    Double.parseDouble(point.getChildNodes().item(2).getTextContent()),
                    sX,
                    sY);
            CSGNode line = line(new Vector3f(p1.getX()/5f, 
                    p1.getY()/5f, p1.getZ()/5f), 
                    new Vector3f(p2.getX()/5f, 
                    p2.getY()/5f, 
                    p2.getZ()/5f),1,5);
            attachChild(line);   
        }
    }
    
    /**
     * Build a segment of a wall
     * @param from
     * @param to
     * @param width
     * @param height
     * @return 
     */
    private CSGNode line(Vector3f from, Vector3f to, float width, float height){
        CSGNode l = new CSGNode();
        double dis = Math.sqrt(Math.pow(to.x-from.x, 2)+Math.pow(to.y-from.y, 2));
        Vector3f center = new Vector3f(0,0,0);//new Vector3f((to.x - from.x)/2+from.x, height/2,(to.y-from.y)/2+from.y);
        Vector3f extend = new Vector3f((float)dis/2,height/2,width/2);
        CubeBrush cube = new CubeBrush(center,extend);
        cube.setType(CSG.BrushType.ADDITIVE);
        l.addBrush(cube);
        
        l.setMaterial(wall_mat);
        if (to.y > from.y)
            l.rotate(0,-(float)Math.acos((to.x-from.x)/dis), 0);
        else
            l.rotate(0,(float)Math.acos((to.x-from.x)/dis), 0);
        l.setLocalTranslation((to.x - from.x)/2+from.x, height/2, (to.y-from.y)/2+from.y);
        l.regenerate();
        
        //Collect vertices for building floor but no longer used
//        if (newWall){
//            vertices.add(new Vector3f(from.x, from.z, from.y));
//            newWall = false;
//        }
//        vertices.add(new Vector3f(to.x,to.z,to.y));
        
        return l;
        
    }
    
    /**
     * Generates all links between vertices given by the data
     * No longer used
     */
//    private void generateLinks(){
//        links = new int[vertices.size()][vertices.size()];
//        for (int i = 0; i < vertices.size()-1;i++)
//            for(int j = i+1;j < vertices.size();j++){
//                if (j-i == 1) {
//                    links[i][j] = 1;
//                    links[j][i] = 1;
//                }
//                else {
//                    links[i][j]=0;
//                    links[i][j]=0;
//                }
//            }
//        for (int i = 0; i < vertices.size()-1;i++)
//            for(int j = i+1;j < vertices.size();j++){
//                if (links[i][j]==0)
//                    if (isLinkable(i,j)) links[i][j] = 1;
//            }
//    }
    
    
    /**
     * Check if could there be a link between a and b
     * No longer used
     * @param a
     * @param b
     * @return YES or NO
     */
//    private boolean isLinkable(int a, int b){
//        boolean linkable = true;
//        for(int i = 0;i<vertices.size()-1;i++)
//            for(int j = i+1;j<vertices.size();j++){
//                if (a!=i && b!=j && a!=j && b!=i && links[i][j]==1 && isIntersecting(vertices.get(a),vertices.get(b),vertices.get(i),vertices.get(j))) {
//                    
//                    linkable = false;
//                    break;
//                }
//            }
//        return linkable;
//    }
    
    /**
     * Determines 2 lines are intersecting
     * @param a
     * @param b
     * @param c
     * @param d
     * @return 
     */
    private boolean isIntersecting(Vector3f a, Vector3f b,Vector3f c,Vector3f d){
        
        return java.awt.geom.Line2D.linesIntersect(a.x, a.z, b.x, b.z, c.x, c.z, d.x, d.z);
    }
    
    /**
     * Generates indices for initiating Mesh
     * No longer used
     */
    private void generateIndexes(){
        float threshold = 100;
        int numberOfTrianglePerCouple = 3;
        for(int i = 0; i<vertices.size()-2;i++){
            for(int j = i+1; j<vertices.size()-1;j++){
                ArrayList<Integer> inds = nearByIndexes(vertices.get(i), vertices.get(j), 50, 5);
                indexes.addAll(inds);
            }
        }
    }
    
    /**
     * Get a list of vertices which are nearest to the line from a to b 
     * No longer used
     * @param a
     * @param b
     * @param threshold
     * @param number
     * @return 
     */
    private ArrayList<Integer> nearByIndexes(Vector3f a, Vector3f b,float threshold, int number){
        ArrayList<Vector3f> nearBy = new ArrayList<>();
        float dist[] = new float[vertices.size()];
        int ind[] = new int[vertices.size()];
        for(int i = 0;i<vertices.size();i++){
            dist[i] = distance(a,b,vertices.get(i));
            ind[i] = i;
        }
        
        
        //Sort
        for(int i = 0;i<Array.getLength(dist)-1;i++)
            for(int j = i+1;j<Array.getLength(ind);j++){
                if (dist[i] > dist[j] || dist[i] == 0){
                    float interf = dist[i];
                    dist[i] = dist[j];
                    dist[j] = interf;
                    
                    int interi = ind[i];
                    ind[i] = ind[j];
                    ind[j] = interi;
                }
            }
        
        ArrayList<Integer> pickedVertices = new ArrayList<>();
        for(int i = 0; i <Array.getLength(ind);i++){
            int index = ind[i];
            if (i > number || index == 0) break;
            
            pickedVertices.add(Integer.valueOf(index));
        }
        return pickedVertices;
    }
    
    /**
     * Calculates distance between line (a,b) to c
     * @param a
     * @param b
     * @param c
     * @return Distance
     */
    private float distance(Vector3f a, Vector3f b, Vector3f c){
        Ray ray = new Ray(a, new Vector3f(b.x-a.x,b.y-a.y,b.z-a.z));
        return ray.distanceSquared(c);
        
    }
    
    /**
     * Converts List<Integer> to int[]
     * @param integers
     * @return 
     */
    public static int[] convertIntegers(List<Integer> integers)
        {
            int[] ret = new int[integers.size()];
            for (int i=0; i < ret.length; i++)
            {
                ret[i] = integers.get(i).intValue();
            }
            return ret;
        }
   
}
