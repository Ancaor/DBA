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
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.log4j.BasicConfigurator;

/**
 *
 * @author Rubén
 */
public class AgentExplorer extends Agent {
    
    private AgentID GPS_ID;
    private AgentID Car_ID;
    
    private ArrayList<Integer> map = new ArrayList<>();
    private ArrayList<Integer> map_real = new ArrayList<>();
    
    
    private  static int m = 20;
    private  static int n = 20;
    
    private int m_real=m;
    private int n_real=n;
    
    private int x;
    private int y;
    private ArrayList<Integer> array_radar = new ArrayList<>(); 
    private ArrayList<Float> array_scanner = new ArrayList<>(); 
    private String msg;
    private String msg2;
    private String msg_finish;
    private JsonObject msgJson;
    private int state = 0;
    Boolean end = false;
    private final static int WAKE_UP = 0;
    private final static int IDLE = 1;
    private final static int PROCESS_DATA = 2;
    private final static int UPDATE_MAP = 3;
    private final static int FINISH = 4;
    
    private String mapName;
    
    //-------------Pulgarcito START------------------
    
    private ArrayList<Integer> mapPulgarcito = new ArrayList<>();
    private boolean MPCreado = false;
    private int MPtam = 500;
    private boolean MapCompleted = false;
    
    
    public AgentExplorer(AgentID aid, AgentID gps, AgentID car, String mapName) throws Exception {
        super(aid);
        GPS_ID = gps;
        Car_ID = car;
        this.mapName = mapName;
        
        this.loadMap(mapName);
        
        
    }
    
    public void initMap(ArrayList<Integer> mapa){
        for(int i = 0; i < m_real*2; i+=1)
            mapa.add(1);
 
        for(int i = 0; i < m_real-4; i+=1){
           mapa.add(1);
           mapa.add(1);
           for (int j = 0; j < m_real-4; j+=1)
               mapa.add(-1);
           mapa.add(1);
           mapa.add(1);
        }
        
        for(int i = 0; i < m_real*2; i+=1)
            mapa.add(1);
    }

    
     private void WAKE_UP(){
    
        state = IDLE;
        msg = "\nExplorer: Wake_up\n";
        
       // this.sendMessage(new AgentID(Car_ID), msg);
    }
    
    /*
        Esperar mensaje 
    */
    private void IDLE(){
    
        msg = this.receiveMessage();
        System.out.println(ANSI_YELLOW+"LO QUE RECIBE EL EXPLORER 1: " + msg);
        
        if(!msg.contains("FINISH")){
        msg2 = this.receiveMessage();
        System.out.println(ANSI_YELLOW+"LO QUE RECIBE EL EXPLORER 2: " + msg2);
        }
        else msg2 = "";
        
        if(msg.contains("CRASHED") || msg.contains("BAD") || msg.contains("FINISH") || msg2.contains("CRASHED") || msg2.contains("BAD") || msg2.contains("FINISH")){
            state = FINISH;
            if(msg.contains("FINISH"))
                this.msg_finish = msg;
            else this.msg_finish = msg2;
        }
        else{
            state = PROCESS_DATA;
        }
        
    }
    
    /*
        Parseo de mensaje.
    */
    
    private void PROCESS_DATA(){
        BasicConfigurator.configure();
        String msg_radar;
        String msg_gps;
        
        if(msg.contains("radar")){
            msg_radar = msg;
            msg_gps = msg2;
        }else{
            msg_radar = msg2;
            msg_gps = msg;
        }
        
        System.out.println(ANSI_YELLOW+"Radar: " + msg_radar);
        System.out.println(ANSI_YELLOW+"GPS: " + msg_gps);
        
      
        JsonObject objectGPS = Json.parse(msg_gps).asObject().get("gps").asObject();
        JsonArray arrayScanner = Json.parse(msg_gps).asObject().get("scanner").asArray();
        JsonArray arrayRadar = Json.parse(msg_radar).asObject().get("radar").asArray();
        
        
        
       // x = objectGPS.get("x").asInt();
       // y = objectGPS.get("y").asInt();
        x = 17;
        y=2;
        
       // msg = "Explorer: GPS x = " +x+"\ty = "+y+"\n";
       // this.sendMessage(new AgentID(Car_ID), msg);
        
       
       // JsonObject object2 = Json.parse(msg_radar).asObject();
        
      //  JsonArray ja = object2.get("radar").asArray();
        
     /*   for (int i = 0; i < 25; i+=1){
            array_radar.add(arrayRadar.get(i).asInt());
        }
        
        for (int i = 0; i < 25; i+=1){
            array_scanner.add(arrayScanner.get(i).asFloat());
        }
    */
     array_radar.add(1);
     array_radar.add(1);
     array_radar.add(1);
     array_radar.add(1);
     array_radar.add(1);
     array_radar.add(1);
     array_radar.add(1);
     array_radar.add(1);
     array_radar.add(1);
     array_radar.add(1);
     array_radar.add(0);
     array_radar.add(0);
     array_radar.add(0);
     array_radar.add(1);
     array_radar.add(1);
     array_radar.add(0);
     array_radar.add(0);
     array_radar.add(0);
     array_radar.add(1);
     array_radar.add(1);
     array_radar.add(0);
     array_radar.add(0);
     array_radar.add(0);
     array_radar.add(1);
     array_radar.add(1);
        
        
        state = UPDATE_MAP;
        
       // msg = "Explorer: Radar = " + array_radar.toString();
       // this.sendMessage(new AgentID(Car_ID), msg);
        
    }
    
    
    /*
        Enviar información al agente del mapa.
    */
   
    private void UPDATE_MAP(){
        int index = 0;
        
        for(int i = y-2; i <= y+2; i+=1)
            for(int j = x-2; j <= x+2; j+=1){
                map.set(i*m+j, array_radar.get(index));
                index+=1;
            }
        
       // map.set(x*m+y, 9);
        System.out.println(ANSI_YELLOW+"Pasa el parseo ");
        state = IDLE;
        
        
    }
    
    private void actualizarMapa(){
        int index = 0;
        for(int i = x-2; i <= x+2; i+=1){
            for(int j = y-2; j <= y+2; j+=1){
                if(mapPulgarcito.get(i*m+j)>=10 && mapPulgarcito.get(i*m+j)<=999990){
                    mapPulgarcito.set(i*m+j, mapPulgarcito.get(i*m+j)+1);
                }
                else{
                    if(array_radar.get(index) == 1){
                        mapPulgarcito.set(i*m+j, 999999);
                    }
                    if(array_radar.get(index) == 2){
                        mapPulgarcito.set(i*m+j, 999998);
                    }
                    if(array_radar.get(index) == 0){
                        mapPulgarcito.set(i*m+j, 10);
                    }
                }
                index+=1;
            }
        }
    }
    
    private void testEnd(){
        
        boolean cAux = true;
        int aux;
        
        for(int i = 501; i < MPtam*MPtam - 500 && cAux == true; i++){
            
            if(i%MPtam == 499){
                i+=2;
            }
            aux = mapPulgarcito.get(i);
            if(aux >= 10 && aux <= 999990 ){
                for(int j = x-1; i <= x+1 && cAux ==  true; i+=2){
                    for(int k = y-1; j <= y+1 && cAux ==  true; j+=2){
                        if(mapPulgarcito.get(j*m+k)==-1){
                            cAux = false;
                        }
                    }
                }
            }
        }
        
        MapCompleted = cAux;
    }
    
    private void pulgarcito(){
        if(!MPCreado){
            for(int i = 0; i < MPtam*MPtam; i++){
                mapPulgarcito.set(i, -1);
            }
            MPCreado = true;
        }
        
        actualizarMapa();
        testEnd();
        
        if(!MapCompleted){
            
        }
        
        
        
    }

    
    private void FINISH(){
    
        end = true;
        
    //    this.m_real = Json.parse(msg_finish).asObject().get("size").asInt();
    //    this.n_real = this.m_real;
        System.out.println(ANSI_YELLOW+"m_real : " + m_real);
        
        //PrintMap();
        saveMap(this.mapName);
        
        //msg = "\nEl Explorer ha finalizado su ejecución.\n";
        //this.sendMessage(new AgentID(Car_ID), msg);
    }
    
    private void PrintMap(){
        
        /*
        for(int i = 0; i < m; i+=1){
            System.out.print("\n");
            for(int j = 0; j < n; j+=1){
                if(map.get(i*m+j) == 1)
                    System.out.print((char)1);
                else{
                    System.out.print("1");
                }
                System.out.print("  ");
            }
        }
        System.out.println();
        */
        
        // Crear una imagen con el contenido del mapa
        
        // Esta en sucio pero funciona
        
        byte [][] a = new byte[m][n];
        for(int i = 0; i < m; i++)
            for(int j = 0; j < n; j++){
                if(map.get(i*m+j) == 1)
                a[i][j] = 0;
                else a[i][j] = 1;
            }
        
        byte raw[] = new byte[m * n];
        for (int i = 0; i < a.length; i++) {
            System.arraycopy(a[i], 0, raw, i*m, n);
        }

        byte levels[] = new byte[]{0, -1};
        BufferedImage image = new BufferedImage(m, n, 
                BufferedImage.TYPE_BYTE_INDEXED,
                new IndexColorModel(8, 2, levels, levels, levels));
        DataBuffer buffer = new DataBufferByte(raw, raw.length);
        SampleModel sampleModel = new ComponentSampleModel(DataBuffer.TYPE_BYTE, m, n, 1, m * 1, new int[]{0});
        Raster raster = Raster.createRaster(sampleModel, buffer, null);
        image.setData(raster);
        try {
            ImageIO.write(image, "png", new File("test.png"));
        } catch (IOException ex) {
            Logger.getLogger(AgentExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        
    }
    
    public void saveMap(String mapName){
        
        if(this.map_real.size() == 0){
            System.out.println(ANSI_YELLOW + "size=0");
            
            this.initMap(map_real);
            
            for(int i=0;i<this.m_real;i++){
                for(int j=0;j<this.m_real;j++){
                    int casilla = this.map.get(i*m+j);
                    if(casilla == 0 ||casilla == 1)
                    this.map_real.set(i*m_real+j, casilla);
                }
            }
            
            
            
            
        }
        
        System.out.println(ANSI_YELLOW + "sale bien");
        
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(mapName+".map"));
            bw.write(m_real + " " + n_real);
            bw.newLine();
            for (int i = 0; i < m_real; i++) {
                for (int j = 0; j < n_real; j++) {
                    bw.write(map_real.get(i*m_real + j) + ((j == m_real-1) ? "" : ","));
                }
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {System.out.println("PETA AL ESCRIBIR EL .MAP");}
        
    }
    
    public void loadMap(String mapName){
        Scanner sc;
        
        try {
            sc = new Scanner(new BufferedReader(new FileReader(mapName+".map")));
            String[] line = sc.nextLine().trim().split(" ");
      
            m_real = Integer.valueOf(line[0]);
            n_real = Integer.valueOf(line[1]);
            
          //  int [][] myArray = new int[m][n];
            while(sc.hasNextLine()) {
              for (int i=0; i<(m*n); i++) {
                  line = sc.nextLine().trim().split(",");
                 for (int j=0; j<line.length; j++) {
                   // myArray[i][j] = Integer.parseInt(line[j]);
                    map_real.add( Integer.parseInt(line[j]));
                 }
              }
           }
        } catch (FileNotFoundException ex) {   // SI NO EXISTE EL ARCHIVO

            initMap(map);
            System.out.println(ANSI_YELLOW+"No existe mapa, se utilizan valores por defecto");
        }
      
    }
    
    @Override
    public void execute(){
        
        //PrintMap();
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
                case FINISH:
                    FINISH();
                    break;
            }
        }   
        System.out.println(ANSI_YELLOW+"Fin de AgentExplorer");
        
    }
}
