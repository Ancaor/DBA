/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guglecar;

import es.upv.dsic.gti_ia.core.AgentID;
import java.util.ArrayList;

/**
 *
 * @author Rubén
 */
public class AgentMap extends Agent {
    
    private ArrayList<Integer> map = new ArrayList<>();
    private final static int m = 500;
    private final static int n = 500;
    
    
    public AgentMap(AgentID aid) throws Exception {
        super(aid);
        for(int i = 0; i < m; i+=1)
            for(int j = 0; j < n; j+=1)
                map.add(0);
    }
    
    @Override
    public void execute(){
        /*while(true){
            String msg = "MapAgent";
            this.sendMessage(new AgentID("PEPE"), msg);
        }*/
        
        
    }
}
