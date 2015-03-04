/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package massim;

import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author TungPT
 */
public class Tree {
    private Node root;

    public Tree() {
        root = new Node("RootNode");
        root.children = new ArrayList<>();
    }

    public Node getRoot() {
        return root;
    }
    
    public static class Node {
        private String data;
        private Node parent;
        private List<Node> children;
        private Vector3f coordinate;

        public Vector3f getCoordinate() {
            return coordinate;
        }

        public void setCoordinate(Vector3f coordinate) {
            this.coordinate = coordinate;
        }
        
        public Node(String data) {
            this.data = data;
        }
        
        public boolean compareTo(Node other){
            return data.compareTo(other.data) == 0?true:false;
        }
        
        public void addChild(Node child){
            children.add(child);
            child.parent = this;
        }
    }
    
    public Node nodeWithData(String data){
        return nodeWithData(data,root);
    }
    
    private Node nodeWithData(String data, Node parent){
        for (Node n : parent.children){
            if (n.data.compareTo(data) == 0) return n;
            return nodeWithData(data,n);
        }
        return null;
    }
}
