/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guglecar_reset;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 *
 * @author Anton
 */
public class GugleCar_Reset {
    
    private static final AgentID SERVER_AGENT = new AgentID("Keid");

    public static void main(String[] args) throws Exception {
        AgentsConnection.connect("isg2.ugr.es",6000,SERVER_AGENT.getLocalName(),"Cancer","Kipling",false);
        
        Agent1 a = new Agent1(new AgentID("aa"));
        a.start();
    }
    
}