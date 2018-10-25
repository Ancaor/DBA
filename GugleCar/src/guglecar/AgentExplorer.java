/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guglecar;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import es.upv.dsic.gti_ia.core.AgentID;
import java.util.ArrayList;
import org.apache.log4j.BasicConfigurator;

/**
 *
 * @author Rubén
 */
public class AgentExplorer extends Agent {
    
    private String GPS_ID;
    private String Car_ID;
    
    private ArrayList<Integer> map = new ArrayList<>();
    private final static int m = 24;
    private final static int n = 24;
    
    private int x;
    private int y;
    private ArrayList<Integer> array_radar = new ArrayList<>(); 
    
    private String msg;
    private String msg2;
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
        for(int i = 0; i < m*2; i+=1)
            map.add(1);
 
        for(int i = 0; i < m-4; i+=1){
           map.add(1);
           map.add(1);
           for (int j = 0; j < m-4; j+=1)
               map.add(-1);
           map.add(1);
           map.add(1);
        }
        
        for(int i = 0; i < m*2; i+=1)
            map.add(1);
    }
    
     private void WAKE_UP(){
    
        state = IDLE;
        msg = "\nExplorer: Wake_up\n";
        
       // this.sendMessage(new AgentID(Car_ID), msg);
    }
    
    /*
        Esperar mensaje 
    */
    private void IDLE(){
    
        msg = this.receiveMessage();
        msg2 = this.receiveMessage();
        
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
        BasicConfigurator.configure();
        String msg_radar;
        String msg_gps;
        
        if(msg.contains("radar")){
            msg_radar = msg;
            msg_gps = msg2;
        }else{
            msg_radar = msg2;
            msg_gps = msg;
        }
       /* 
        System.out.println("Radar: " + msg_radar);
        System.out.println("GPS: " + msg_gps);
        */
      
        JsonObject object = Json.parse(msg_gps).asObject();
        
        
        x = object.get("x").asInt();
        y = object.get("y").asInt();
        
        msg = "Explorer: GPS x = " +x+"\ty = "+y+"\n";
       // this.sendMessage(new AgentID(Car_ID), msg);
        
       
        JsonObject object2 = Json.parse(msg_radar).asObject();
        
        JsonArray ja = object2.get("radar").asArray();
        
        for (int i = 0; i < 25; i+=1){
            array_radar.add(ja.get(i).asInt());
        }
        
        state = UPDATE_MAP;
        
        msg = "Explorer: Radar = " + array_radar.toString();
       // this.sendMessage(new AgentID(Car_ID), msg);
        
    }
    
    
    /*
        Enviar información al agente del mapa.
    */
   
    private void UPDATE_MAP(){
        int index = 0;
        /*
        for(int i = x-2; i <= x+2; i+=1)
            for(int j = y-2; j <= y+2; j+=1){
                map.set(i*m+j, array_radar.get(index));
                index+=1;
            }
        
        map.set(x*m+y, 9);*/
        state = FINISH;
        
        
    }

    
    private void FINISH(){
    
        end = true;
        PrintMap();
        msg = "\nEl Explorer ha finalizado su ejecución.\n";
        //this.sendMessage(new AgentID(Car_ID), msg);
    }
    
    private void PrintMap(){
        for(int i = 0; i < m; i+=1){
            System.out.print("\n");
            for(int j = 0; j < n; j+=1){
                if(map.get(i*m+j) == 1)
                    System.out.print((char)1);
                else{
                    System.out.print("1");
                }
                System.out.print("  ");
            }
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
