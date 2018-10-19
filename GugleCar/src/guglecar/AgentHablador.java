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
public class AgentHablador extends Agent{
    
    public AgentHablador(AgentID aid) throws Exception {
        super(aid);
    }
    
    @Override
    public void execute(){
        while(true){
        String msg = "Hola que tal";
        this.sendMessage(new AgentID("PEPE"), msg);
        //System.out.print(msg);
        }
    }
}
