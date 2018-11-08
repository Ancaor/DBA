package practica2;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import es.upv.dsic.gti_ia.core.AgentID;

/** 
 * @author Ruben Mógica Garrido 
 * @author Antonio José Camarero Ortega 
 *  
 * Clase que controla el agente de la batería. 
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
    
    
     /** 
     * @author Ruben Mógica Garrido 
     *  
     * Constructor con parámetros. 
     *  
     * @param aid Representa el id va a tener el agente. 
     * @param car Representa el id del AgentCar con el que se comunica. 
     * @throws Exception  
     */ 
    public AgentBattery(AgentID aid, AgentID car) throws Exception {
        super(aid);
        Car_ID = car;
    }
    
    
    /** 
     * @author Ruben Mógica Garrido 
     *  
     * Función que inicializa el agente. 
     */ 
    @Override
    public void init(){
        end = false;
        battery = 0;
        state = WAKE_UP;
    }
    
    
    
    
    /** 
     * @author Ruben Mógica Garrido 
     *  
     * Función que ejecuta la función del agente. Mientras no finalice controla 
     * los diferentes estados por los que pasa el agente. 
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
    
    /** 
     * @author Ruben Mógica Garrido 
     *  
     * Funcion que se ejecuta al despertar el agente y modifica el estado a IDLE. 
     */
    private void Wake_up(){
        System.out.println(ANSI_BLUE+"------- BATTERY WAKE UP -------");
        state = IDLE;
    }
    
    /** 
     * @author Ruben Mógica Garrido 
     *  
     * Función que se encarga de recibir un mensaje con la información asociada 
     * a la bateria. 
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
     *  
     * Función que determina el final de la ejecución del agente. 
     */ 
    private void Finish(){
        end = true;
    }
    
    /** 
     * @author Rubén Mogica Garrido 
     *  
     * Función que parsea el mensaje recibido en JSON y almacena la información 
     * en variables. Una vez procesada la información cambia el estado  
     * a SEND_CONF. 
     */
    private void ProcessData(){
        JsonObject object = Json.parse(msg).asObject();
        battery = object.get("battery").asFloat();
               
        state = SEND_CONF;
    }
    
    /** 
     * @author Ruben Mógica Garrido 
     * @author Antonio José Camarero Ortega 
     *  
     * Función que le envía un mensaje al AgentCar indicandole si debe o no 
     * recargar la bateria. 
     *  
     */ 
    private void SendConf(){
        
        JsonObject response = new JsonObject();
        
        if(this.battery < 2.0){        //Valor de prueba
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
