/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guglecar;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import es.upv.dsic.gti_ia.core.AgentID;
import java.util.ArrayList;
/**
 *
 * @author Rubén
 */

public class AgentRadar extends Agent{
    
    private AgentID Car_ID;
    private AgentID Explorer_ID;
    private ArrayList<Integer> array_radar = new ArrayList<>(); 
    
    private int x;
    private int y;
    
    JsonArray msgJsonArray;
    JsonObject msgJsonObject;
    private String msg;
    
    private int state = 0;
    Boolean end = false;
    private final static int WAKE_UP = 0;
    private final static int IDLE = 1;
    private final static int PROCESS_DATA = 2;
    private final static int UPDATE_MAP = 3;
    private final static int WAIT_CONFIRM = 4;
    private final static int FINISH = 5;
    
    
    
    
    
    public AgentRadar(AgentID aid, AgentID explorer, AgentID car) throws Exception {
        super(aid);
        Car_ID = car;
        Explorer_ID = explorer;
    }
    
    
    /*
        Levanta al agente que crea el mapa.
    */
    private void WAKE_UP(){

        System.out.println(ANSI_PURPLE+"Radar: Wake_up\n");
        state = IDLE;
    }
    
    /*
        Esperar mensaje 
    */
    private void IDLE(){
        
        msg = this.receiveMessage();
       
       // System.out.println(ANSI_PURPLE+"LO QUE RECIBE EL RADAR : " + msg);

        if(msg.contains("CRASHED") || msg.contains("BAD") || msg.contains("FINISH")){
            state = FINISH;
        }
        else{
            state = PROCESS_DATA;
        }
    }
    
    
    private void PROCESS_DATA(){
        
        JsonObject object = Json.parse(msg).asObject();
        array_radar.clear();
                
        JsonArray ja = object.get("radar").asArray();
        
        for (int i = 0; i < 25; i+=1){
            array_radar.add(ja.get(i).asInt());
           // System.out.println(ANSI_PURPLE + "posicion " + i + " : " + ja.get(i).asInt());
        }
           
      //  System.out.println(ANSI_PURPLE+"Vision de matriz del radar");
      
        
        state = UPDATE_MAP;
    }
    
   
    private void UPDATE_MAP(){
    
        state = IDLE;
        
        
        this.sendMessage(Explorer_ID, msg);
        
    }

    
    private void FINISH(){
    
        end = true;
        
    }
    
    @Override
    public void execute(){
        String msg = "";
        while(!end){
            switch(state){
                case WAKE_UP:
                    WAKE_UP();
                    break;
                case IDLE: 
                    IDLE();
                    break;
                case PROCESS_DATA: 
                    PROCESS_DATA();
                    break;
                case UPDATE_MAP: 
                    UPDATE_MAP();
                    break;
                case WAIT_CONFIRM: 
                    //WAIT_CONFIRM();
                    break;
                case FINISH:
                    FINISH();
                    break;
            }
        }
        System.out.println(ANSI_PURPLE+"------- RADAR FINISHED -------");
    }
}

