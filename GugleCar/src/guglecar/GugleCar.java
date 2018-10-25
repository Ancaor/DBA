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
        
        String Car_ID = "Carro4";
        String GPS_ID = "gps4";
        String Radar_ID = "Radar4";
        String Explorer_ID = "Explorador4";
        
        Agent Car = new AgentCar(new AgentID(Car_ID));
        //Agent hablador = new AgentHablador(new AgentID("a"));
        Agent explorer = new AgentExplorer(new AgentID(Explorer_ID), GPS_ID, Car_ID);
        Agent GPS = new AgentGPS(new AgentID(GPS_ID), Car_ID, Explorer_ID);
        Agent radar = new AgentRadar(new AgentID(Radar_ID), Car_ID, Explorer_ID);
        Car.start();
        //hablador.start();
        GPS.start();
        explorer.start();
        radar.start();
    }
    
}
