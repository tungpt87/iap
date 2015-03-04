/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package massim.behavior;

import com.jme3.ai.navmesh.Cell;
import com.jme3.ai.navmesh.NavMesh;
import ifcGeometry.Level;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import massim.Config;
import massim.Main;
import massim.node.AgentNode;
import massim.node.Environment;

/**
 *
 * @author TungPT
 */
public class ExploreBehavior extends AbstractBehavior{
    public enum CellMark{
        EXPLORED, VISITED
    }
    
    Cell targetedCell;
    HashMap<String, CellMark> cellsDict;
    
    
    public ExploreBehavior(Environment envi, AgentNode agent) {
        super(envi, agent);
    }

    public void setCellAsExplored(Cell cell){
        cellsDict.put(cell.toString(), CellMark.EXPLORED);
    }
    
    private void explore(){
        Level fLevel = envi.getBuilding().getLevels().get(0);
        if (cellsDict == null){
            cellsDict = new HashMap<>();
        }
        NavMesh navMesh = envi.getNavMesh();
        
        if (targetedCell == null){
            targetedCell = navMesh.findClosestCell(agent.getGeometry().getWorldTranslation());
            setCellAsExplored(targetedCell);
        } else {
            
            Cell[] adjaCells = targetedCell.getLinks();
            int len = Array.getLength(adjaCells);
            boolean foundUnexploredCell = false;
            ArrayList<Cell> unexplored = new ArrayList<>();
            for (int i = 0; i < len; i++){
                Cell cell = adjaCells[i];
                if (cell != null && !cellsDict.containsKey(cell.toString())){
                    unexplored.add(cell);
                    foundUnexploredCell = true;
                }
            }
            if (!foundUnexploredCell){
                cellsDict.put(targetedCell.toString(), CellMark.VISITED);
                targetedCell = null;
                while (targetedCell == null){
                    targetedCell = adjaCells[rndGenerator.nextInt(len)];
                }
            } else {
                targetedCell = unexplored.get(rndGenerator.nextInt(unexplored.size()));
                cellsDict.put(targetedCell.toString(), CellMark.EXPLORED);
            }
        }
    }
    
    public boolean didReachTarget(Cell cell){
        return didReachTarget(cell.getCenter());
    }
    public void adjustDirectionByTarget( Cell cell){
        adjustDirectionByTarget(cell.getCenter());
    }
    @Override
    public void update(float fps) {
        if (targetedCell == null || didReachTarget(targetedCell)){
                explore();
            }
        if (targetedCell != null){
            adjustDirectionByTarget(targetedCell);
        }
    }
    
}
