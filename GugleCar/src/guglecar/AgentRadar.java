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
    
    private String Car_ID;
    private String Explorer_ID;
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
    
    
    
    
    
    public AgentRadar(AgentID aid, String car, String explorer) throws Exception {
        super(aid);
        Car_ID = car;
        Explorer_ID = explorer;
    }
    
    
    /*
        Levanta al agente que crea el mapa.
    */
    private void WAKE_UP(){
    
        state = IDLE;
        msg = "\nRadar: Wake_up\n";
        //this.sendMessage(new AgentID(Car_ID), msg);

    }
    
    /*
        Esperar mensaje 
    */
    private void IDLE(){
        
        //msg = this.receiveMessage();
       

        for (int i = 0; i < 25; i+=1)
            array_radar.add((int) (Math.random() * 3) + 0);
        
        
        msgJsonArray = new JsonArray();
        for (int i = 0; i < 25; i+=1)
            msgJsonArray.add(array_radar.get(i));
        
        //msgJsonObject.add("radar", msgJsonArray);
        
        msg = msgJsonArray.toString();
        
        msgJsonObject = new JsonObject();
        msgJsonObject.add("radar", msgJsonArray);
        msg = msgJsonObject.toString();
        //this.sendMessage(new AgentID(Car_ID), msg);

        
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
            array_radar.add(ja.get(0).asInt());
        }
                
        state = UPDATE_MAP;
    }
    
   
    private void UPDATE_MAP(){
    
        state = FINISH;
        //this.sendMessage(new AgentID(Car_ID), msg);
        this.sendMessage(new AgentID(Explorer_ID), msg);
        
    }

    
    private void FINISH(){
    
        end = true;
        msg = "\nEl radar ha finalizado su ejecución.\n";
   //     this.sendMessage(new AgentID(Car_ID), msg);
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
    }
}

