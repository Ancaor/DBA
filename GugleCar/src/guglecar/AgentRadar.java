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
 * @author Rubén Marín Asunción
 * 
 * Clase que representa al agente que maneja el Radar. Hereda de la clase Agent.
 */

public class AgentRadar extends Agent{
    
    private AgentID Car_ID;
    private AgentID Explorer_ID;
    private ArrayList<Integer> array_radar = new ArrayList<>(); 
    
    private static final boolean DEBUG = false;

    
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
    private final static int FINISH = 4;
    
    
    
    
    /**
     * @author Rubén Marín Asunción
     * @param aid Representa el id del agente creado.
     * @param explorer Representa el id del AgentExplorer con el que se comunica.
     * @param car Representa el id del AgentCar con el que se comunica.
     * @throws Exception 
     */
    public AgentRadar(AgentID aid, AgentID explorer, AgentID car) throws Exception {
        super(aid);
        Car_ID = car;
        Explorer_ID = explorer;
    }
    
    
    /**-
     * @author Rubén Marín Asunción
     * 
     * Función que se ejecuta al despertar el agente y modifica el estado a IDLE.
     */
    private void WAKE_UP(){

        if(DEBUG)
            System.out.println(ANSI_PURPLE+"Radar: Wake_up\n");
        
        state = IDLE;
    }
    
    /**
     * @author Rubén Marín Asunción
     * 
     * Función que se encarga de recibir un mensaje con la información asociada
     * al Radar. Si el mendaje recibido contiene la cadena CRASHED, FINISH o BAD
     * significa que ha ocurrido un error y cambia el estado a FINISH. Sino cambia
     * el estado a PROCESS_DATA.
     */
    private void IDLE(){
        
        msg = this.receiveMessage();
       
        if(DEBUG)
            System.out.println(ANSI_PURPLE+"LO QUE RECIBE EL RADAR : " + msg);

        if(msg.contains("CRASHED") || msg.contains("BAD") || msg.contains("FINISH")){
            state = FINISH;
        }
        else{
            state = PROCESS_DATA;
        }
    }
    
    /**
     * @author Rubén Marín Asunción
     * 
     * Función que parsea los datos recibidos en JSON y los almacena en variables.
     * Una vez finaliza cambia el estado a UPDATE_MAP.
     * 
     */
    private void PROCESS_DATA(){
        
        JsonObject object = Json.parse(msg).asObject();
        array_radar.clear();
                
        JsonArray ja = object.get("radar").asArray();
        
        for (int i = 0; i < 25; i+=1){
            array_radar.add(ja.get(i).asInt());
            if(DEBUG)
                System.out.println(ANSI_PURPLE + "posicion " + i + " : " + ja.get(i).asInt());
        }
           
        if(DEBUG)
            System.out.println(ANSI_PURPLE+"Vision de matriz del radar");
      
        
        state = UPDATE_MAP;
    }
    
   /**
     * @author Rubén Marín Asunción
     * 
     * Función que envía los datos del radar al explorer para que pueda manejar 
     * los datos del radar.
     * 
     */
    private void UPDATE_MAP(){
    
        state = IDLE;
        
        if(DEBUG)
            System.out.println(ANSI_PURPLE+"Envío del mensaje al explorer");
        
        this.sendMessage(Explorer_ID, msg);
        
    }

    /**
     * @author Rubén Marín Asunción
     * 
     * Función que determina el final de la ejecución del agente.
     * 
     */
    private void FINISH(){
    
        end = true;
        
    }
    
    
    /**
     * @author Rubén Marín Asunción
     * 
     * Función que ejecuta la función del agente. Mientras no finalice controla
     * los diferentes estados por los que pasa el agente.
     */
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
                case FINISH:
                    FINISH();
                    break;
            }
        }
        
        if(DEBUG)
            System.out.println(ANSI_PURPLE+"------- RADAR FINISHED -------");
    }
}

