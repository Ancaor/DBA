/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guglecar_reset;


import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

/**
 *
 * @author Anton
 */
public class Agent1 extends SingleAgent{
    
    public Agent1(AgentID aid) throws Exception {
        super(aid);
    }
    
    public String getName(){
        return this.getAid().getLocalName();
    }
    
    public void sendMessage(AgentID sendTo, String content){
        ACLMessage outbox = new ACLMessage();
		
        outbox.setSender(this.getAid());
        outbox.setReceiver(sendTo);
        outbox.setContent(content);
		
        this.send(outbox);
    }
    
    public String receiveMessage(){
        try {
            
            ACLMessage inbox = this.receiveACLMessage();	
            return inbox.getContent();
            
        } catch (InterruptedException ex) {
            
            System.err.println(ex.getMessage());
	    return null;
        
        }
    }
    
    @Override
    public void execute(){
        
        JsonObject outjson = Json.object().add("command", "login")
                .add("world", "map10");
               // .add("radar", radarAgent.getLocalName())
               // .add("scanner", radarAgent.getLocalName())
               // .add("battery", batteryAgent.getLocalName());
               
      
            System.out.println("CAR_LOGIN_MESSAGE : " + outjson.toString());
        
        
        this.sendMessage(new AgentID("Keid"), outjson.toString());
        
        this.receiveMessage();
        String aux = this.receiveMessage();
        
        System.out.println(aux);
        
         outjson = Json.object().add("command", "logout")
                .add("key",Json.parse(aux).asObject().get("result").asString());
         
         System.out.println(outjson.toString());
         
         this.sendMessage(new AgentID("Keid"), outjson.toString());
         
         aux = this.receiveMessage();
                         System.out.println(aux);

         aux = this.receiveMessage();
         

                System.out.println(aux);

    }
}
