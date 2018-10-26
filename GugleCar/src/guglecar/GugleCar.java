/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guglecar;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 *
 * @author Anton
 */
public class GugleCar {
    
    private static final AgentID SERVER_AGENT = new AgentID("Keid");
    private static final AgentID CAR_AGENT = new AgentID("JUANETE2");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        AgentsConnection.connect("isg2.ugr.es",6000,SERVER_AGENT.getLocalName(),"Cancer","Kipling",false);
        
        
        Agent Car = new AgentCar(CAR_AGENT,SERVER_AGENT);
        Car.start();
      // Agent1 a = new Agent1(new AgentID("aa"));
      // a.start();
    }
    
}
