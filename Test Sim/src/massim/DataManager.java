/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package massim;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import massim.node.AgentNode;

/**
 *
 * @author TungPT
 */
public class DataManager {
    private static ArrayList<String> transactions;
    private static Date startTime;
    
    public static void resetManager(){
        transactions = null;
        startTime = null;
    }
    
    public static void logByAgent(AgentNode agent){
        if (transactions == null){
            transactions = new ArrayList<>();
        }
        if (startTime == null){
            startTime = new Date();
        }
        
        StringBuilder strbldr = new StringBuilder();
        Date d = new Date();
        
        strbldr.append(d.getTime() - startTime.getTime());
        strbldr.append(",");
        strbldr.append(agent.getAgentId());
        strbldr.append(",");
        strbldr.append(agent.getPosition().x+","+agent.getPosition().y+","+agent.getPosition().z+",");
        strbldr.append(agent.getBodyPhy().getViewDirection().x+","+agent.getBodyPhy().getViewDirection().y+","+agent.getBodyPhy().getViewDirection().z);
        transactions.add(strbldr.toString());
        if (transactions.size() >= Config.TRANSACTIONS_PER_SUBMIT){
            writeTransactionsToFile();
        }
    }
    private static void writeTransactionsToFile(){
        File f = new File(Config.AGENT_LOG_CSV);
        if (!f.exists()){
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //Combine array of transactions into one string
        StringBuilder strbldr = new StringBuilder();
        for(String trans:transactions){
            strbldr.append(trans);
            strbldr.append("\n");
        }
        
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(Config.AGENT_LOG_CSV, true)))) {
           out.println(strbldr.toString());
           transactions = null;
        }catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }
}
