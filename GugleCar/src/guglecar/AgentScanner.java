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

/**
 *
 * @author Ruben Marín Asunción
 */
public class AgentScanner extends Agent{
   private String msg;
   private String msg2;
   private int state = 0;
   boolean end = false;
   private ArrayList<Float> array_scanner = new ArrayList<>(); 

   
   AgentID Car_ID;
   AgentID Explorador_ID;
   
   private static final int WAKE_UP = 0;
   private static final int IDLE = 1;
   private static final int PROCESS_DATA = 2;
   private static final int UPDATE_MAP = 3;
   private static final int WAIT_CONFIRM = 4;
   private static final int FINISH = 5;
   
   private static final boolean DEBUG = false;

    public AgentScanner(AgentID aid, AgentID car, AgentID explorador) throws Exception {
        super(aid);
        Car_ID = car;
        Explorador_ID = explorador;
    }
    
    private void WAKE_UP(){
        state = IDLE;
        System.out.println("SCANNER WAKE UP");
    }
    
    
    /**
     * @author Ruben Marín Asunción
     * @author Antonio José Camarero Ortega
     */
    
    private void IDLE(){
        
        msg = this.receiveMessage();
        
        if(DEBUG)
            System.out.println("LO QUE RECIBE EL ESCANER 1: " + msg);
        
        if(!msg.contains("FINISH"))
            msg2 = this.receiveMessage();
        else msg2 = "";
       
        if(DEBUG)
            System.out.println("LO QUE RECIBE EL ESCANER 2: " + msg2);
        
        if(msg.contains("CRASHED") || msg.contains("BAD") || msg.contains("FINISH") || msg2.contains("CRASHED") || msg2.contains("BAD") || msg2.contains("FINISH")){
            state = FINISH;
        }
        else{
            state = PROCESS_DATA;
        }
    }
    
    private void PROCESS_DATA(){
      
        JsonObject object = Json.parse(msg).asObject();
        array_scanner.clear();
                
        JsonArray ja = object.get("scanner").asArray();
        
        for (int i = 0; i < 25; i+=1){
            array_scanner.add(ja.get(i).asFloat());
        }
        
        
        
        //Salida con formato matriz
        if(DEBUG){
            System.out.println("Vision de matriz del escaner");
            for (int i = 0; i < 25; i+=1){
                if(i%5 == 0){
                    System.out.print("\n");
                }
            System.out.print(array_scanner.get(i));
            System.out.print("   ");
            }
            System.out.print("\n");
        }
         
        state = UPDATE_MAP;
    }
    
    /**
     * @author Ruben Marín Asunción
     * @author Antonio José Camarero Ortega
     */
    
    private void UPDATE_MAP(){
        
        JsonObject object = new JsonObject();
        object.add("gps", Json.parse(msg2).asObject().get("gps").asObject())
              .add("scanner",Json.parse(msg).asObject().get("scanner").asArray() );
        
        if(DEBUG)
            System.out.println("LO QUE VA AL EXPLORER : " + object.toString());
        
        this.sendMessage(Explorador_ID, object.toString());
        
        state = WAIT_CONFIRM;
    }
    
    
    private void WAIT_CONFIRM(){
        state = IDLE;
    }
    
    private void FINISH(){
    
        end = true;
    }
    
    @Override
    public void execute(){
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
                    WAIT_CONFIRM();
                    break;
                case FINISH:
                    FINISH();
                    break;
            }
        }
        System.out.println("------- SCANNER FINISHED -------");

    }
}
