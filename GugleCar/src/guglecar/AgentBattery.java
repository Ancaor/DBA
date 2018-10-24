package guglecar;

import es.upv.dsic.gti_ia.core.AgentID;

/**
 *
 * @author Ruben
 */
public class AgentBattery extends Agent{
    private boolean end;
    private int state;
    private String msg;
    private static final int IDLE = 0;
    private static final int FINISH = 1;
    private static final int PROCESS_DATA = 2;
    private static final int SEND_CONF = 3;
    
    public AgentBattery(AgentID aid) throws Exception {
        super(aid);
    }
    
    @Override
    public void init(){
        end = false;
    }
    
    @Override
    public void execute(){
        while (!end){

            
            switch(state){
                case IDLE:
                    Idle();
                    break;
                case FINISH:
                    Finish();
                    break;
                case PROCESS_DATA:
                    ProcessData();
                    break;
                case SEND_CONF:
                    SendConf();
                    break;
            }
        
        }
    }
    
    private void Idle(){
        msg = this.receiveMessage();
        System.out.println(msg);
        if(msg.contains("CRASHED") || msg.contains("FINISH")){
            state = FINISH;
        }
        else{
            state = PROCESS_DATA;
        }
    }
    
    private void Finish(){}
    
    private void ProcessData(){
        JsonObject object = Json.parse(msg).asObject();
    }
    
    private void SendConf(){}
}
