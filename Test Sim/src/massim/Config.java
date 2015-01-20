/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package massim;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
/**
 *
 * @author TungPT
 */
public class Config {
    private static boolean    USE_EXTERNAL_CONFIG = false;
    public static float       MODEL_SCALE = 100f;
    public static boolean     DEBUG = false;
    public static String      MODEL_FILE = "assets/TestResult.xml";
    public static String      FLAGS_FILE = "assets/flags.txt";
    public static int         UPDATE_CYCLE = 1000;
    public static String      AGENT_LOG_CSV = "agentlog.csv";
    public static int         TRANSACTIONS_PER_SUBMIT = 10;
    public static float       AGENT_RADIUS = 1.5f;
    public static float       AGENT_DEVIATION = 5f;
    
    public static void loadConfig(){
        if (!USE_EXTERNAL_CONFIG) return;
        File f = new File("config.json");
        if (f.exists()){
            try {
                String jsonStr = Utility.readFile("config.json", StandardCharsets.UTF_8);
                JSONObject jobj = new JSONObject(jsonStr);
                MODEL_SCALE = (float)jobj.getDouble("MODEL_SCALE");
                DEBUG = jobj.getBoolean("DEBUG");
                MODEL_FILE = jobj.getString("MODEL_FILE");
                FLAGS_FILE = jobj.getString("FLAGS_FILE");
                UPDATE_CYCLE = jobj.getInt("UPDATE_CYCLE");
                AGENT_LOG_CSV = jobj.getString("AGENT_LOG_CSV");
                TRANSACTIONS_PER_SUBMIT = jobj.getInt("TRANSACTIONS_PER_SUBMIT");
                AGENT_RADIUS = (float)jobj.getDouble("AGENT_RADIUS");
                AGENT_DEVIATION = (float)jobj.getDouble("AGENT_DEVIATION");
                System.out.printf("UPDATE_CYCLE" + UPDATE_CYCLE);
            } catch (IOException ex) {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
}
