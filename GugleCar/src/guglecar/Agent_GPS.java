/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guglecar;

import es.upv.dsic.gti_ia.core.AgentID;
/**
 *
 * @author Rubén
 */

public class Agent_GPS extends Agent{
    
    String Car_ID;
    
    private int state = 0;
    Boolean FINISH = false;
    private final static int WAKE_UP = 0;
    private final static int IDLE = 1;
    private final static int PROCESS_DATA = 2;
    private final static int UPDATE_MAP = 3;
    private final static int WAIT_CONFIRM = 4;
    private final static int WARN_RADAR = 5;
    
    
    
    public Agent_GPS(AgentID aid, String car) throws Exception {
        super(aid);
        Car_ID = car;
    }
    
    
    /*
        Levanta al agente que crea el mapa.
    */
    public String WAKE_UP(){
    
        state = IDLE;
        return "\nGPS: Wake_up\n";
    }
    
    /*
        Esperar mensaje 
    */
    public String IDLE(){
    
        state = PROCESS_DATA;
        return "GPS: Idle\n";
    }
    
    /*
        Parseo de mensaje.
    */
    
    public String PROCESS_DATA(){
    
        state = UPDATE_MAP;
        return "GPS: Process_Data\n";
    }
    
    
    /*
        Enviar información al agente del mapa.
    */
   
    public String UPDATE_MAP(){
    
        state = WAIT_CONFIRM;
        return "GPS: Update_Map\n";
    }
    
    /*
        Espero respuesta del agente del mapa.
    */
    public String WAIT_CONFIRM(){
    
        state = WARN_RADAR;
        return "GPS: Wait_Confirm\n";
    }
    
    /*
        Enviar mensaje al agente del radar.
    */
    public String WARN_RADAR(){
    
        state = IDLE;
        return "GPS: Warn_Radar\n";
    }
    
    @Override
    public void execute(){
        String msg = "";
        while(!FINISH){
            switch(state){
                case WAKE_UP:
                    msg = WAKE_UP();
                    break;
                case IDLE: 
                    msg = IDLE();
                    break;
                case PROCESS_DATA: 
                    msg = PROCESS_DATA();
                    break;
                case UPDATE_MAP: 
                    msg = UPDATE_MAP();
                    break;
                case WAIT_CONFIRM: 
                    msg = WAIT_CONFIRM();
                    break;
                case WARN_RADAR: 
                    msg = WARN_RADAR();
                    //FINISH = true;
                    break;
            }
            this.sendMessage(new AgentID(Car_ID), msg);
        }
        msg = "\nEl GPS ha finalizado su ejecución.\n";
        this.sendMessage(new AgentID(Car_ID), msg);
        //System.out.print(msg);
    }
}

