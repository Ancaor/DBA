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
 * @author Anton
 */
public class AgentCar extends Agent{
    
    private static final int AWAKE_AGENTS = 0;
    private static final int LOGIN_AGENTS = 1;
    private static final int WAIT_SERVER_RESPONSE = 2;
    private static final int WAIT_AGENTS = 3;
    private static final int FINISH_MOVEMENT = 4;
    private static final int FINISH = 5;
    private static final int SEND_COMMAND = 6;
    
    private static final boolean DEBUG = true;
    
    private int state;
    private boolean finish;
    private String clave;
    private int agentsNum;
    private boolean refuel;
    private JsonObject command;
    
    AgentBattery agentBattery;
    AgentExplorer agentExplorer;
    AgentGPS agentGPS;
    AgentRadar agentRadar;
    
    AgentID serverAgent;
    AgentID radarAgent = new AgentID("Radar7");
    AgentID scannerAgent;
    AgentID gpsAgent = new AgentID("gps6");
    AgentID batteryAgent = new AgentID("bateriCoche");
    AgentID explorerAgent = new AgentID("Explorador7");
    
    
    
    
    public AgentCar(AgentID aid, AgentID server_id) throws Exception {
        super(aid);
        this.serverAgent = server_id;
        this.agentsNum = 1;
        this.refuel = false;
    }
    
    public void awakeAgents(){
        try {
            this.agentBattery = new AgentBattery(batteryAgent,this.getAid());
            //this.agentGPS = new AgentGPS(gpsAgent, explorerAgent, this.getAid());
            //this.agentRadar = new AgentRadar()
           
        } catch (Exception ex) {
            Logger.getLogger(AgentCar.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error inicializando agentes");
        }
        this.agentBattery.start();
       // this.agentGPS.start();
    }
    
    public void loginAgentsState(){
       // String response  = this.receiveMessage();
       // JsonObject injson = Json.parse(response).asObject();
        JsonObject outjson = Json.object().add("command", "login")
                .add("world", "map1")
                .add("battery", batteryAgent.getLocalName());
                //.add("gps", gpsAgent.getLocalName());
               //.add("radar", radarAgent.getLocalName());
               // .add("scanner", radarAgent.getLocalName());
               // .add("battery", batteryAgent.getLocalName());
               
        if(DEBUG){
            System.out.println("CAR_LOGIN_MESSAGE : " + outjson.toString());
        }
        
        this.sendMessage(this.serverAgent, outjson.toString());
        
        this.state = WAIT_SERVER_RESPONSE;
        
        if(DEBUG){
            System.out.println("MENSAJE DE LOGIN ENVIADO");
        }
    }
    
    public void waitServerResponse(){
        
        String server_response = this.receiveMessage();
        
        if(DEBUG)
            System.out.println("CAR_SERVER_RESPONSE : " + server_response);
        
            
        if(server_response.contains("result")){
            
            if(DEBUG){
            System.out.println("CONTIENE RESULT");
            }
                
            JsonObject injson = Json.parse(server_response).asObject();
                
            if(injson.get("result").toString().contains("BAD")){
                //this.state = FINISH_MOVEMENT;
                this.state = WAIT_AGENTS;
                System.out.println("ERROR_SERVER : " + server_response);
            }else if(injson.get("result").toString().contains("CRASHED")){
                //this.state = FINISH;
                this.state = WAIT_AGENTS;
                System.out.println("ERROR_SERVER : " + server_response);
            }else{
                if(this.clave.equals("")){
                    this.clave = injson.get("result").asString();
                    System.out.println("Logeado en servidor");
                }
                
                if(DEBUG){
            System.out.println("YA ESTABA REGISTRADO, HA LLEGADO UN OK");
            }
                    
                this.state = WAIT_AGENTS;   
                    
            }
   
        }else if(server_response.contains("trace")){
            
            if(DEBUG){
            System.out.println("CONTIENE TRAZA");
            }
            
            try{
                System.out.println("Recibiendo traza ...");

                JsonObject injson = Json.parse(server_response).asObject();

                JsonArray array = injson.get("trace").asArray();
                byte data[] = new byte[array.size()];
                for(int i = 0; i<data.length; i++)
                    data[i] = (byte) array.get(i).asInt();

                FileOutputStream fos  = new FileOutputStream("mitraza.png");
                fos.write(data);
                fos.close();
                System.out.println("Traza guardada");
            }catch(IOException ex){
                System.out.println("Error procesando traza");
            }
            
            finish = true;
        }
            
        
        
    }
    
    /*
    public void waitAgents(){
        
        for(int i = 0; i< 5 ; i++){
            if(DEBUG)
        System.out.println("bucle : " + i);
            
            JsonObject outjson = Json.object().add("command", "moveS")
                .add("key", this.clave);
            this.sendMessage(this.serverAgent, outjson.toString());
            if(DEBUG)
        System.out.println("bucle : " + outjson.toString());
            String a = this.receiveMessage();
            System.out.println("bucle : " + a);
        }
        
        JsonObject outjson = Json.object().add("command", "logout")
                .add("key", this.clave);
        
        this.sendMessage(this.serverAgent, outjson.toString());
        
        if(DEBUG)
        System.out.println("ENVIO LOGOUT : " + outjson.toString());
        
        String aux1 = this.receiveMessage();
        String aux2 = this.receiveMessage();
        
        if(DEBUG)
        System.out.println("aux1 : " + aux1);
        
        if(DEBUG)
        System.out.println("aux2 : " + aux2);
        
        
        if(aux1.contains("trace")){
            try{
                System.out.println("Recibiendo traza ...");

                JsonObject injson = Json.parse(aux1).asObject();

                JsonArray array = injson.get("trace").asArray();
                byte data[] = new byte[array.size()];
                for(int i = 0; i<data.length; i++)
                    data[i] = (byte) array.get(i).asInt();

                FileOutputStream fos  = new FileOutputStream("mitraza.png");
                fos.write(data);
                fos.close();
                double a  = array.size();
                System.out.println("TAMANIO MAPA: " + a);
                System.out.println("Traza guardada");
            }catch(IOException ex){
                System.out.println("Error procesando traza");
            }
        }else{
            try{
                System.out.println("Recibiendo traza ...");

                JsonObject injson = Json.parse(aux2).asObject();

                JsonArray array = injson.get("trace").asArray();
                byte data[] = new byte[array.size()];
                for(int i = 0; i<data.length; i++)
                    data[i] = (byte) array.get(i).asInt();

                FileOutputStream fos  = new FileOutputStream("mitraza.png");
                fos.write(data);
                fos.close();
                double a  = array.size();
                BufferedImage im = ImageIO.read(new File("mitraza.png"));
                
                System.out.println("TAMANIO MAPA: " + im.getWidth());
                System.out.println("Traza guardada");
            }catch(IOException ex){
                System.out.println("Error procesando traza");
            }
            
            
            
        }
        
        finish = true;
       // state = WAIT_SERVER_RESPONSE;
    }
    
    public void sendCommand(){
        
    }
    */
    
    public void waitAgents(){
        
        ArrayList<String> messages = new ArrayList();
        
        for(int i = 0; i < this.agentsNum; i++){
            messages.add(this.receiveMessage());
        }
        for(int i = 0; i < messages.size(); i++){
            if(messages.get(i).contains("battery")){
                this.refuel = Json.parse(messages.get(i)).asObject().get("battery").asBoolean();
            }
        }
        
        state = SEND_COMMAND;
    }
    
    public void sendCommand(){
        
        
        
        //LOGOUT
        
         command = Json.object().add("command", "logout")
                .add("key", this.clave);
        
        this.sendMessage(this.serverAgent, command.toString());
        
        this.state = FINISH;
        
        //
    }
    
    public void finish(){
        
        if(DEBUG)
        System.out.println("ENVIO LOGOUT : " + command.toString());
        
        String aux1 = this.receiveMessage(); // OK
        String aux2 = this.receiveMessage(); // traza
        
        
        //
        //se podrian recibir o no los mensajes de los sensores  ->> hace falta ?, es util ?  
        // String var = this.receiveMessage(); -> batteryAgent , por ejemplo
        // ...
        //
        
        if(DEBUG)
        System.out.println("aux1 : " + aux1);
        
        if(DEBUG)
        System.out.println("aux2 : " + aux2);
        
        JsonObject outjson = Json.object().add("signal", "FINISH");
        
        this.sendMessage(this.batteryAgent, outjson.toString());
       // this.sendMessage(this.gpsAgent, outjson.toString());
        
        if(aux1.contains("trace")){
            try{
                System.out.println("Recibiendo traza ...");

                JsonObject injson = Json.parse(aux1).asObject();

                JsonArray array = injson.get("trace").asArray();
                byte data[] = new byte[array.size()];
                for(int i = 0; i<data.length; i++)
                    data[i] = (byte) array.get(i).asInt();

                FileOutputStream fos  = new FileOutputStream("mitraza.png");
                fos.write(data);
                fos.close();
                double a  = array.size();
                System.out.println("TAMANIO MAPA: " + a);
                System.out.println("Traza guardada");
            }catch(IOException ex){
                System.out.println("Error procesando traza");
            }
        }else{
            try{
                System.out.println("Recibiendo traza ...");

                JsonObject injson = Json.parse(aux2).asObject();

                JsonArray array = injson.get("trace").asArray();
                byte data[] = new byte[array.size()];
                for(int i = 0; i<data.length; i++)
                    data[i] = (byte) array.get(i).asInt();

                FileOutputStream fos  = new FileOutputStream("mitraza.png");
                fos.write(data);
                fos.close();
                double a  = array.size();
                BufferedImage im = ImageIO.read(new File("mitraza.png"));
                
                System.out.println("TAMANIO MAPA: " + im.getWidth());
                System.out.println("Traza guardada");
            }catch(IOException ex){
                System.out.println("Error procesando traza");
            }
            
            
            
        }
        
        finish = true;
        
        
        
    }
    
    
    @Override
    public void init(){
        this.finish = false;
        this.clave = "";
        this.state = AWAKE_AGENTS;
        //this.state = WAIT_AGENTS;
    }
    
    @Override
    public void execute(){
        
        //String msg = this.receiveMessage();
        //System.out.print(msg);
      //  if(DEBUG)
       //     System.out.println("ESTADO_CAR : " + state);
        
        while(!finish)
        {
            if(DEBUG)
                System.out.println("ESTADO_CAR : " + state);
             
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
            
            System.out.println("------- CAR FINISHED -------");
        }
      
    }
    
}
