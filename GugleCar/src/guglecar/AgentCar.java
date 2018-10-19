/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guglecar;

import es.upv.dsic.gti_ia.core.AgentID;

/**
 *
 * @author Anton
 */
public class AgentCar extends Agent{
    
    public AgentCar(AgentID aid) throws Exception {
        super(aid);
    }
    
    @Override
    public void execute(){
        while (true){
        String msg = this.receiveMessage();
        System.out.print(msg);
        }
    }
}
