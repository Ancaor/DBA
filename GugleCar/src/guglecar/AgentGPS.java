/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guglecar;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import es.upv.dsic.gti_ia.core.AgentID;
/**
 *
 * @author Rubén
 */

public class AgentGPS extends Agent{
    
    AgentID Car_ID;
    AgentID Explorer_ID;
    
    private int x;
    private int y;
    
    JsonObject msgJson;
    private String msg;
    
    private int state = 0;
    Boolean end = false;
    private final static int WAKE_UP = 0;
    private final static int IDLE = 1;
    private final static int PROCESS_DATA = 2;
    private final static int UPDATE_MAP = 3;
    private final static int SEND_CONFIRM = 4;
    private final static int FINISH = 5;
    
    
    
    
    
    public AgentGPS(AgentID aid, AgentID explorer, AgentID car) throws Exception {
        super(aid);
        Car_ID = car;
        Explorer_ID = explorer;
    }
    
    
    /*
        Levanta al agente que crea el mapa.
    */
    private void WAKE_UP(){
    
        state = IDLE;

    }
    
    /*
        Esperar mensaje 
    */
    private void IDLE(){
    
        msg = this.receiveMessage();
        /*
        int x_random = (int) (Math.random() * 15) + 3;
        int y_random = (int) (Math.random() * 15) + 3;
        
        msgJson = new JsonObject();
        msgJson.add("x", x_random);
        msgJson.add("y", y_random);
        
        msg = msgJson.toString();
        */
        if(msg.contains("CRASHED") || msg.contains("FINISH")){
            state = FINISH;
        }
        else{
            state = PROCESS_DATA;
        }
       
    }
    
    /*
        Parseo de mensaje.
    */
    
    private void PROCESS_DATA(){
        JsonObject object = Json.parse(msg).asObject();
        
        x = object.get("x").asInt();
        y = object.get("y").asInt();
        
        
        state = UPDATE_MAP;
        
        //this.sendMessage(new AgentID(Car_ID), msg);
    }
    
    
    /*
        Enviar información al agente del mapa.
    */
   
    private void UPDATE_MAP(){
    
        state = SEND_CONFIRM;   
    }
    
     private void SEND_CONFIRM(){
        
        JsonObject response = new JsonObject();
        
        response.add("gps", true);
        //this.sendMessage(Car_ID, response.toString());
        
        state = IDLE;
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
                case SEND_CONFIRM: 
                    SEND_CONFIRM();
                    break;
                case FINISH:
                    FINISH();
                    break;
            }
        }
        System.out.println("------- GPS FINISHED -------");
    }
}

