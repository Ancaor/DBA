/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guglecar;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

/**
 *
 * @author Anton
 */
public class Agent extends SingleAgent{
    
    public Agent(AgentID aid) throws Exception {
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
}
