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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        AgentsConnection.connect("isg2.ugr.es",6000,"Keid","Cancer","Kipling",false);
        
        Agent pepe = new AgentCar(new AgentID("PEPE"));
        Agent hablador = new AgentHablador(new AgentID("a"));
        pepe.start();
        hablador.start();
    }
    
}
