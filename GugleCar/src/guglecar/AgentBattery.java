package guglecar;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import es.upv.dsic.gti_ia.core.AgentID;

/**
 *
 * @author Ruben Mógica Garrido
 */
public class AgentBattery extends Agent{
    AgentID Car_ID;
    
    private boolean end;
    private int state;
    private float battery;
    private String msg;
    private static final int WAKE_UP = 0;
    private static final int IDLE = 1;
    private static final int FINISH = 2;
    private static final int PROCESS_DATA = 3;
    private static final int SEND_CONF = 4;
    
    
    public AgentBattery(AgentID aid, AgentID car) throws Exception {
        super(aid);
        Car_ID = car;
    }
    
    
    /**
     * @author Ruben Mógica Garrido
     */
    @Override
    public void init(){
        end = false;
        battery = 0;
        state = WAKE_UP;
    }
    
    
    
    
    /**
     * @author Ruben Mógica Garrido
     */
    @Override
    public void execute(){
        while (!end){    
            switch(state){
                case WAKE_UP:
                    Wake_up();
                    break;
                case IDLE:
                    Idle();
                    break;
                case FINISH:
                    Finish();
                    break;
                case PROCESS_DATA:
                    ProcessData();
                    break;
                case SEND_CONF:
                    SendConf();
                    break;
            }    
        }
        
        System.out.println(ANSI_BLUE+"------- BATTERY FINISHED -------");
        
    }
    
    
    private void Wake_up(){
        System.out.println(ANSI_BLUE+"------- BATTERY WAKE UP -------");
        state = IDLE;
    }
    
    /**
     * @author Ruben Mógica Garrido
     */
    private void Idle(){
        msg = this.receiveMessage();
        
        if(msg.contains("CRASHED") || msg.contains("FINISH")){
            state = FINISH;
        }
        else{
            state = PROCESS_DATA;
        }
    }
    
    
    /**
     * @author Ruben Mógica Garrido
     */
    private void Finish(){
        end = true;
    }
    
    private void ProcessData(){
        JsonObject object = Json.parse(msg).asObject();
        battery = object.get("battery").asFloat();
               
        state = SEND_CONF;
    }
    
    /**
     * @author Ruben Mógica Garrido
     * @author Antonio José Camarero Ortega
     */
    private void SendConf(){
        
        JsonObject response = new JsonObject();
        
        if(this.battery < 10.0){        //Valor de prueba
            response.add("battery", true);
            this.sendMessage(Car_ID, response.toString());
        }
        else{
            response.add("battery", false);
            this.sendMessage(Car_ID, response.toString());
        }
        
        state = IDLE;
    }
}
