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
 * @author Rubén Marín Asunción
 * 
 * Clase que representa al agente que maneja el GPS. Hereda de la clase Agent.
 */

public class AgentGPS extends Agent{
    
    AgentID Car_ID;
    AgentID Scanner_ID;
    
    private int x;
    private int y;
    
    private String msg;
    
    private static final boolean DEBUG = false;
    
    private int state = 0;
    Boolean end = false;
    private final static int WAKE_UP = 0;
    private final static int IDLE = 1;
    private final static int PROCESS_DATA = 2;
    private final static int SEND_CONFIRM = 3;
    private final static int FINISH = 4;
    
    
    
    /**
     * @author Rubén Marín Asunción
     * 
     * Constructor con parámetros de la clase AgentGPS
     * 
     * @param aid Representa el id del agente creado.
     * @param scanner Representa el ID del agente Scanner con el que se comunica.
     * @param car Representa el ID del agente Car con el que se comunica.
     * @throws Exception 
     */
    
    public AgentGPS(AgentID aid, AgentID scanner, AgentID car) throws Exception{
        super(aid);
        Car_ID = car;
        Scanner_ID = scanner;
    }
    
    
    /**
     * @author Rubén Marín Asunción
     * 
     * Funcion que se ejecuta al despertar el agente y modifica el estado a IDLE.
     */
    private void WAKE_UP(){
         
        state = IDLE;
        
        System.out.println(ANSI_GREEN+"------- GPS WAKE UP -------");
        

    }
    
    /**
     * @author Rubén Marín Asunción
     * 
     * Función que se encarga de recibir un mensaje con la información asociada
     * al GPS. Si el mendaje recibido contiene la cadena CRASHED, FINISH o BAD
     * significa que ha ocurrido un error y cambia el estado a FINISH. Sino cambia
     * el estado a PROCESS_DATA.
     */
    private void IDLE(){
    
        msg = this.receiveMessage();
        
        if(msg.contains("CRASHED") || msg.contains("FINISH") || msg.contains("BAD"))
            state = FINISH;
        else
            state = PROCESS_DATA;
        
        if(DEBUG)
            System.out.println(ANSI_GREEN+"LO QUE RECIVE EL GPS : " + msg);
    }
    
    /**
     * @author Rubén Marín Asunción
     * 
     * Función que parsea el mensaje recibido en JSON y almacena la información
     * en variables. Una vez procesada la información cambia el estado 
     * a SEND_CONFIRM.
     */
    private void PROCESS_DATA(){
        
        if(DEBUG)
            System.out.println(ANSI_GREEN+"PROCESS DATA");

        JsonObject object = Json.parse(msg).asObject();
                
        
        x = object.get("gps").asObject().get("x").asInt();
        y = object.get("gps").asObject().get("y").asInt();
        
        state = SEND_CONFIRM;
    }
    
    
    /**
     * @author Rubén Marín Asunción
     * 
     * Función que envía una confirmación al AgentScanner para que pueda 
     * comenzar su ejecución. Cambia el estado a IDLE a la espera de un 
     * nuevo mensaje.
     */
    private void SEND_CONFIRM(){

        if(DEBUG)
            System.out.println(ANSI_GREEN+"CONFIRMACION GPS : " + msg );

        this.sendMessage(Scanner_ID, msg);
        
        state = IDLE;
    }

    /**
     * @author Rubén Marín Asunción
     * 
     * Función que determina el final de la ejecución del agente.
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
                case SEND_CONFIRM: 
                    SEND_CONFIRM();
                    break;
                case FINISH:
                    FINISH();
                    break;
            }
        }
        
        System.out.println(ANSI_GREEN+"------- GPS FINISHED -------");
    }
}

