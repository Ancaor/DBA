/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guglecar;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import es.upv.dsic.gti_ia.core.AgentID;
import java.util.ArrayList;

/**
 *
 * @author Rubén
 */
public class AgentExplorer extends Agent {
    
    private String GPS_ID;
    private String Car_ID;
    
    private ArrayList<Integer> map = new ArrayList<>();
    private final static int m = 500;
    private final static int n = 500;
    
    private int x;
    private int y;
    
    private String msg;
    private JsonObject msgJson;
    private int state = 0;
    Boolean end = false;
    private final static int WAKE_UP = 0;
    private final static int IDLE = 1;
    private final static int PROCESS_DATA = 2;
    private final static int UPDATE_MAP = 3;
    private final static int FINISH = 4;
    
    
    public AgentExplorer(AgentID aid, String gps, String car) throws Exception {
        super(aid);
        GPS_ID = gps;
        Car_ID = car;
        for(int i = 0; i < m; i+=1)
            for(int j = 0; j < n; j+=1)
                map.add(-1);
    }
    
     private void WAKE_UP(){
    
        state = IDLE;
        msg = "\nExplorer: Wake_up\n";
        
        this.sendMessage(new AgentID(Car_ID), msg);
    }
    
    /*
        Esperar mensaje 
    */
    private void IDLE(){
    
        msg = this.receiveMessage();
        
        if(msg.contains("CRASHED") || msg.contains("BAD") || msg.contains("FINISH")){
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
        msg = "Explorer: x = " +x+"\ty = "+y+"\n";
        this.sendMessage(new AgentID(Car_ID), msg);
        
    }
    
    
    /*
        Enviar información al agente del mapa.
    */
   
    private void UPDATE_MAP(){
        int index = x*m + y;
        
        map.set(index, 0);
    
        state = IDLE;
        
        
    }

    
    private void FINISH(){
    
        end = true;
        //PrintMap();
        msg = "\nEl Explorer ha finalizado su ejecución.\n";
        this.sendMessage(new AgentID(Car_ID), msg);
    }
    
    private void PrintMap(){
        for(int i = 0; i < m; i+=1){
            System.out.print("\n");
            for(int j = 0; j < n; j+=1)
                System.out.print(map.get(i*m+j));
            
        }
    }
    
    @Override
    public void execute(){
        
        //PrintMap();
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
                case FINISH:
                    FINISH();
                    break;
            }
        }   
        
    }
}
