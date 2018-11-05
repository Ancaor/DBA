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
import static com.hp.hpl.jena.query.vocabulary.TestQuery.data;
import es.upv.dsic.gti_ia.core.AgentID;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.Math.sqrt;
import static java.lang.System.in;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * Agente controlador, es el que lanza a los demas agentes.
 * Además es el que se comunica con el servidor.
 * 
 * @author Antonio José Camarero Ortega.
 * 
 */
public class AgentCar extends Agent{
    
    private static final int AWAKE_AGENTS = 0;
    private static final int LOGIN_AGENTS = 1;
    private static final int WAIT_SERVER_RESPONSE = 2;
    private static final int WAIT_AGENTS = 3;
    private static final int FINISH_MOVEMENT = 4;
    private static final int FINISH = 5;
    private static final int SEND_COMMAND = 6;
    
    private static final String MAPA = "map10";
    
    private static final boolean DEBUG = false;
    
    private int state;
    private boolean finish;
    private String clave;
    private int agentsNum;
    private boolean refuel;
    private String movement;
    private String signal;
    private JsonObject command = new JsonObject();
    
    AgentBattery agentBattery;
    AgentExplorer agentExplorer;
    AgentGPS agentGPS;
    AgentRadar agentRadar;
    AgentScanner agentScanner;
    
    AgentID serverAgent;
    AgentID radarAgent = new AgentID("Radar1");
    AgentID scannerAgent = new AgentID("Scanner1");
    AgentID gpsAgent = new AgentID("Gps1");
    AgentID batteryAgent = new AgentID("Bateria1");
    AgentID explorerAgent = new AgentID("Explorador1");
    
    /**
    *
    * Asigna los nombres del agentCar y del servidor
    * además inicializa algunas variables.
    * 
    * @author Antonio José Camarero Ortega
    */
 
    public AgentCar(AgentID aid, AgentID server_id) throws Exception {
        super(aid);
        this.serverAgent = server_id;
        this.agentsNum = 2;
        this.refuel = false;
    }
    
    /**
    *
    * Inistancia los demás agentes y los inicia.
    * 
    * @author Antonio José Camarero Ortega
    */
    
    public void awakeAgents(){
        try {
            this.agentBattery = new AgentBattery(batteryAgent,this.getAid());
            this.agentGPS = new AgentGPS(gpsAgent, scannerAgent, this.getAid());
            this.agentRadar = new AgentRadar(this.radarAgent,this.explorerAgent, this.getAid());
            this.agentScanner = new AgentScanner(this.scannerAgent,this.getAid(),this.explorerAgent);
            this.agentExplorer = new AgentExplorer(this.explorerAgent,this.gpsAgent,this.getAid(),MAPA);
        } catch (Exception ex) {
            Logger.getLogger(AgentCar.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ANSI_RED+"Error inicializando agentes");
        }
        this.agentBattery.start();
        this.agentGPS.start();
        this.agentRadar.start();
        this.agentScanner.start();
        this.agentExplorer.start();
    }
    
    
    /**
    *
    * Logea a los agentes en el servidor para que cada uno
    * reciba la informacion que necesita.
    * 
    * @author Antonio José Camarero Ortega
    */
    
    public void loginAgentsState(){
       // String response  = this.receiveMessage();
       // JsonObject injson = Json.parse(response).asObject();
         command = Json.object().add("command", "login")
                .add("world", MAPA)
                .add("battery", batteryAgent.getLocalName())
                .add("gps", gpsAgent.getLocalName())
               .add("radar", radarAgent.getLocalName())
                .add("scanner", scannerAgent.getLocalName());
              
               
        if(DEBUG){
            System.out.println(ANSI_RED +"CAR_LOGIN_MESSAGE : " + command.toString());
        }
        
        this.sendMessage(this.serverAgent, command.toString());
        
        this.state = WAIT_SERVER_RESPONSE;
        
        if(DEBUG){
            System.out.println(ANSI_RED +"MENSAJE DE LOGIN ENVIADO");
        }
    }
    
    
    
    /**
    *
    * Espera la respuesta del servidor y la analiza.
    * Si recibe un "result":
    *   a) Si es un BAD o un CRASH va al estado FINISH.
    *   b) Si la clave no esta asignada, el contenido es la clave.
    *   c) Si tiene clave asignada el contenido es un OK.
    * En los casos a) y b) se va a al estado WAIT_AGENTS.
    * 
    * Si recibe un "trace":
    *   Guarda la traza y finaliza. 
    * 
    * @author Antonio José Camarero Ortega
    */
    
    public void waitServerResponse(){
        
        String server_response = this.receiveMessage();
        
        if(DEBUG)
            System.out.println(ANSI_RED +"CAR_SERVER_RESPONSE : " + server_response);
        
            
        if(server_response.contains("result")){
            
            if(DEBUG){
            System.out.println(ANSI_RED +"CONTIENE RESULT");
            }
                
            JsonObject injson = Json.parse(server_response).asObject();
                
            if(injson.get("result").toString().contains("BAD")){
                //this.state = FINISH_MOVEMENT;
                this.state = WAIT_AGENTS;
                System.out.println(ANSI_RED +"ERROR_SERVER : " + server_response);
            }else if(injson.get("result").toString().contains("CRASHED")){
                //this.state = FINISH;
                this.state = FINISH;
                System.out.println(ANSI_RED +"ERROR_SERVER : " + server_response);
            }else{
                if(this.clave.equals("")){
                    this.clave = injson.get("result").asString();
                    System.out.println(ANSI_RED +"Logeado en servidor");
                }
                
                if(DEBUG){
            System.out.println(ANSI_RED +"YA ESTABA REGISTRADO, HA LLEGADO UN OK");
            }
                    
                this.state = WAIT_AGENTS;   
                    
            }
   
        }else if(server_response.contains("trace")){
            
            if(DEBUG){
            System.out.println(ANSI_RED+"CONTIENE TRAZA");
            }
            
            try{
                System.out.println(ANSI_RED+"Recibiendo traza ...");

                JsonObject injson = Json.parse(server_response).asObject();

                JsonArray array = injson.get("trace").asArray();
                byte data[] = new byte[array.size()];
                for(int i = 0; i<data.length; i++)
                    data[i] = (byte) array.get(i).asInt();

                FileOutputStream fos  = new FileOutputStream("mitraza.png");
                fos.write(data);
                fos.close();
                System.out.println(ANSI_RED+"Traza guardada");
            }catch(IOException ex){
                System.out.println(ANSI_RED+"Error procesando traza");
            }
            
            finish = true;
        }
            
        
        
    }
 
    
    
    /**
    *
    * Espera la respuesta de agentBattery y de agentExplorer
    * para determinar que acción realizar.
    * 
    * El agentBattery indica el valor de refuel
    * El agentExplorer indica el valor de movement
    * 
    * Si el comando anteriormente enviado fue "logout" se pasa al estado FINISH
    * Si no, se pasa al estado SEND_COMMAND.
    * 
    * 
    * @author Antonio José Camarero Ortega
    */
    
    public void waitAgents(){
        
        ArrayList<String> messages = new ArrayList();
        
        for(int i = 0; i < this.agentsNum; i++){
            messages.add(this.receiveMessage());
            System.out.println(ANSI_RED+"Contenido mensage " + i + messages.get(messages.size()-1));
        }
        for(int i = 0; i < messages.size(); i++){
            if(messages.get(i).contains("battery")){
                this.refuel = Json.parse(messages.get(i)).asObject().get("battery").asBoolean();
            }else if(messages.get(i).contains("command")){//recibe desde el explorer
                this.movement = Json.parse(messages.get(i)).asObject().get("command").asString();
                this.signal = "";
            }else if(messages.get(i).contains("signal")){//recibe desde el explorer
                this.signal = Json.parse(messages.get(i)).asObject().get("signal").asString();
                this.movement = "";
            }
        }
        
        if(command.toString().contains("logout"))
            state = FINISH;
        else
            state = SEND_COMMAND;
    }
    
    
    
    
    
    
    
    /**
     * 
     * Decide el comando que va a realizar y lo envia al servidor.
     * 
     * 
     * 
     * @author Antonio José Camarero Ortega
     */
    
    
    public void sendCommand(){
        
        System.out.println(ANSI_RED+"Esta en send command");
        
        if(this.refuel){
            System.out.println(ANSI_RED+"-------HAGO REFUEL ---------------------->");
            command = Json.object().add("command", "refuel")
                    .add("key", this.clave);
            this.state = WAIT_SERVER_RESPONSE;
        }else if(this.movement.contains("move")){
            System.out.println(ANSI_RED+"tiene move");
            command = Json.object().add("command", this.movement)
                    .add("key", this.clave);
            this.state = WAIT_SERVER_RESPONSE;
        }else if(this.signal.contains("NO_MOVE")){ //LOGOUT porque no hay movmiento
            System.out.println(ANSI_RED+"no tiene move");
            command = Json.object().add("command", "logout")
                    .add("key", this.clave);
            this.state = FINISH;
           //mando a finish los agentes antes de recibir el OK
            JsonObject outjson = Json.object().add("signal", "FINISH");
        
        this.sendMessage(this.batteryAgent, outjson.toString());
        this.sendMessage(this.gpsAgent, outjson.toString());
        this.sendMessage(this.radarAgent, outjson.toString());
        this.sendMessage(this.scannerAgent, outjson.toString());
        this.sendMessage(this.explorerAgent, outjson.toString());
        }
        
        
        this.sendMessage(this.serverAgent, command.toString());
        
        
        
        //
    }
    
    
    
    /**
     * 
     * Recibe la traza y avisa a los demas agentes de que se ha finalizado.
     * 
     * 
     * 
     * @author Antonio José Camarero Ortega
     */
    
    public void finish(){
        
        if(DEBUG)
        System.out.println(ANSI_RED+"ENVIO LOGOUT : " + command.toString());
        
        
        
        String aux1 = this.receiveMessage(); // OK
        
        String aux2 = this.receiveMessage(); // traza
     
        
        if(DEBUG)
        System.out.println(ANSI_RED+"aux1 : " + aux1);
        
        if(DEBUG)
        System.out.println(ANSI_RED+"aux2 : " + aux2);
        
        JsonObject outjson = Json.object().add("signal", "FINISH");
        
        BufferedImage im = null;
        
        if(aux1.contains("trace")){
            try{
                System.out.println(ANSI_RED+"Recibiendo traza ...");

                JsonObject injson = Json.parse(aux1).asObject();

                JsonArray array = injson.get("trace").asArray();
                byte data[] = new byte[array.size()];
                for(int i = 0; i<data.length; i++)
                    data[i] = (byte) array.get(i).asInt();

                FileOutputStream fos  = new FileOutputStream("mitraza.png");
                fos.write(data);
                fos.close();
                double a  = array.size();
                 im = ImageIO.read(new File("mitraza.png"));
                
                System.out.println(ANSI_RED+"TAMANIO MAPA: " + im.getWidth());
                System.out.println(ANSI_RED+"Traza guardada");
            }catch(IOException ex){
                System.out.println(ANSI_RED+"Error procesando traza");
            }
        }else{
            try{
                System.out.println(ANSI_RED+"Recibiendo traza ...");

                JsonObject injson = Json.parse(aux2).asObject();

                JsonArray array = injson.get("trace").asArray();
                byte data[] = new byte[array.size()];
                for(int i = 0; i<data.length; i++)
                    data[i] = (byte) array.get(i).asInt();

                FileOutputStream fos  = new FileOutputStream("mitraza.png");
                fos.write(data);
                fos.close();
                double a  = array.size();
                 im = ImageIO.read(new File("mitraza.png"));
                
                System.out.println(ANSI_RED+"TAMANIO MAPA: " + im.getWidth());
                System.out.println(ANSI_RED+"Traza guardada");
            }catch(IOException ex){
                System.out.println(ANSI_RED+"Error procesando traza");
            }
            
            
            
        }
        
        
        outjson.add("size", im.getWidth());
        this.sendMessage(this.explorerAgent, outjson.toString());
        
        finish = true;
        
        
        
    }
    
    /**
     * 
     *Inicializa algunas variables y asigna el estado a AWAKE_AGENTS
     * 
     * 
     * @author Antonio José Camarero Ortega
     */
    
    @Override
    public void init(){
        this.finish = false;
        this.clave = "";
        this.state = AWAKE_AGENTS;
        //this.state = WAIT_AGENTS;
    }
    
    /**
     * 
     * Ejecución del agente.
     * Va entrando en cada estado segun el momento de la ejecucion
     * y acaba cuando se le indica con la variable finish.
     * 
     * @author Antonio José Camarero Ortega
     */
    
    @Override
    public void execute(){
        
        //String msg = this.receiveMessage();
        //System.out.print(msg);
      //  if(DEBUG)
       //     System.out.println(ANSI_RED+"ESTADO_CAR : " + state);
        
        while(!finish)
        {
            if(DEBUG)
                System.out.println(ANSI_RED+"ESTADO_CAR : " + state);
             
            switch(state)
            {
                case AWAKE_AGENTS:
                    state = LOGIN_AGENTS;
                    awakeAgents();
                    break;
                case LOGIN_AGENTS:
                    loginAgentsState();
                    break;
                case WAIT_SERVER_RESPONSE:
                    waitServerResponse();
                    break;

                case WAIT_AGENTS:
                    waitAgents();
                    break;

                case FINISH_MOVEMENT:

                break;

                case FINISH:
                    finish();
                break;

                case SEND_COMMAND:
                    sendCommand();
                break;
            }
            
           
        }
       System.out.println(ANSI_RED+"------- CAR FINISHED -------");
    }
    
}
