/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guglecar;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import es.upv.dsic.gti_ia.core.AgentID;
import java.util.ArrayList;

/**
 *
 * @author Ruben
 */
public class AgentScanner extends Agent{
   private String msg;
   private String msg2;
   private int state = 0;
   boolean end = false;
   private ArrayList<Float> array_scanner = new ArrayList<>(); 

   
   AgentID Car_ID;
   AgentID Explorador_ID;
   
   private static final int WAKE_UP = 0;
   private static final int IDLE = 1;
   private static final int PROCESS_DATA = 2;
   private static final int UPDATE_MAP = 3;
   private static final int WAIT_CONFIRM = 4;
   private static final int FINISH = 5;

    public AgentScanner(AgentID aid, AgentID car, AgentID explorador) throws Exception {
        super(aid);
        Car_ID = car;
        Explorador_ID = explorador;
    }
    
    private void WAKE_UP(){
        state = IDLE;
        System.out.println("SCANNER WAKE UP");
    }
    
    private void IDLE(){
        
        msg = this.receiveMessage();
       // System.out.println("LO QUE RECIBE EL ESCANER 1: " + msg);
        
        if(!msg.contains("FINISH"))
        msg2 = this.receiveMessage();
        else msg2 = "";
       
        
     //   System.out.println("LO QUE RECIBE EL ESCANER 2: " + msg2);
        
        if(msg.contains("CRASHED") || msg.contains("BAD") || msg.contains("FINISH") || msg2.contains("CRASHED") || msg2.contains("BAD") || msg2.contains("FINISH")){
            state = FINISH;
        }
        else{
            state = PROCESS_DATA;
        }
    }
    
    private void PROCESS_DATA(){
      
        JsonObject object = Json.parse(msg).asObject();
        array_scanner.clear();
                
        JsonArray ja = object.get("scanner").asArray();
        
        for (int i = 0; i < 25; i+=1){
            array_scanner.add(ja.get(i).asFloat());
          //  System.out.println("info scanner: " + i + " : " + ja.get(i).asFloat());
        }
        
        
        
        //Salida con formato matriz
     //   System.out.println("Vision de matriz del escaner");
        for (int i = 0; i < 25; i+=1){
            if(i%5 == 0){
       //         System.out.print("\n");
            }
         //   System.out.print(array_scanner.get(i));
         //   System.out.print("   ");
        }
      //  System.out.print("\n");
        
        state = UPDATE_MAP;
    }
    
    private void FindObjective(){
        /*
        IDEA:
        Tenemos la posicion de los puntos A y B y todas las distancias entre A, B y C
        La distancia entre dos puntos (p y q) es D=sqrt((Xq-Xp)^2+(Yq-Yp)^2)
        Tenemos entonces que la distancia de A a C es
        AC=sqrt((Xa-Xc)^2+(Ya-Yc)^2)
        Y  la distancia de B a C es
        BC=sqrt((Xb-Xc)^2+(Yb-Yc)^2)
        
        NOTA: El orden de la resta da igual porque vamos a tener siempre valores positivos
        Lo que es importante es que se reste el mayor al menor para no tener resultados negativos
        
        Los datos que tenemos en las ecuaciones serían AC, BC, Xa, Ya, Xb y Yb
        Las incognitas a resolver entonces serian Xc y Yc (las coordenadas del destino)
        Como son dos incognitas y tenemos dos ecuaciones hay que hacer un sistema de ecuaciones
        */
        
        float AC = array_scanner.get(12);    //Posicion central, donde está el coche
        float BC = array_scanner.get(13);    //Posición justo a la derecha del coche (podría ser cualquiera en teoría)
        
        JsonObject object = Json.parse(msg2).asObject();

        float Xa = object.get("gps").asObject().get("x").asFloat();
        float Ya = object.get("gps").asObject().get("y").asFloat();
 
        float Xb = Xa+1;
        float Yb = Ya;
        
        
        System.out.println("AC: " + AC + " BC: " + BC + "\nXa: " + Xa + " Ya: " + Ya
                + "\nXb: " + Xb + " Yb: " + Yb);
        
        //Ecuaciones (Intento despejar(sale mal))
        /*
        AC=sqrt((Xa-Xc)^2+(Ya-Yc)^2)
        AC^2=(Xa-Xc)^2+(Ya-Yc)^2
        AC^2=(Xa^2-Xa*Xc+Xc^2)+(Ya^2-Ya*Yc+Yc^2)
        AC^2-Xa^2+Ya^2=Xc*(Xc-Xa)+Yc*(Yc-Ya)
        
        
        Yc*(Yc-Ya)=AC^2-Xa^2+Ya^2+Xc(Xc-Xa)
        
        //No estoy seguro de lo de antes pero de lo siguiente menos
        Yc^2-YaYc=AC^2-Xa^2+Ya^2+Xc(Xc-Xa)
        //dividir todo entre Ya
        (Yc^2)/Ya-Yc=(AC^2-Xa^2+Ya^2+Xc(Xc-Xa))/Ya
        */
        
        
        
        /*
        IDEA 2:
        Obtener los angulos de AC y BC para poder sacar las coordenadas (triangulacion geodesica?)
        
        */
    }
    
    private void UPDATE_MAP(){
        FindObjective();
        
        JsonObject object = new JsonObject();
        object.add("gps", Json.parse(msg2).asObject().get("gps").asObject())
              .add("scanner",Json.parse(msg).asObject().get("scanner").asArray() );
        
        System.out.println("LO QUE VA AL EXPLORER : " + object.toString());
        
        this.sendMessage(Explorador_ID, object.toString());
        
        state = WAIT_CONFIRM;
    }
    
    private void WAIT_CONFIRM(){
        state = IDLE;
    }
    
    private void FINISH(){
    
        end = true;
    }
    
    @Override
    public void execute(){
        String msg = "";
        while(!end){
            switch(state){
                case WAKE_UP:
                    WAKE_UP();
                    break;
                case IDLE: 
                    IDLE();
                    break;
                case PROCESS_DATA: 
                    PROCESS_DATA();
                    break;
                case UPDATE_MAP: 
                    UPDATE_MAP();
                    break;
                case WAIT_CONFIRM: 
                    WAIT_CONFIRM();
                    break;
                case FINISH:
                    FINISH();
                    break;
            }
        }
        System.out.println("------- SCANNER FINISHED -------");

    }
}
