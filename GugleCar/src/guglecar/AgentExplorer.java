/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guglecar;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import edu.emory.mathcs.backport.java.util.Collections;
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
import static java.lang.Thread.sleep;
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
    
    
    private  static int m = 504;
    private  static int n = 504;
    
    private static final boolean DEBUG = false;
    
    
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
    
    private int x_objetivo;
    private int y_objetivo;
    
    private String mapName;
    
    private int pasos = 0; // para movimiento pocho
    
    //-------------Pulgarcito START------------------
    
    private ArrayList<Integer> mapPulgarcito = new ArrayList<>();
    private boolean mapExist;
    private int steps;
    private int stepsPulgarcito;
    private int iter;
    
    private final int STEPS_PER_ITER = 1000;
    private int MAX_STEPS = 1000;
    private final int MAX_ITERS = 1;
    
    private static final int WALL = 999999999;
    private static final int ROAD = 0;
    
    
    ///////////////////////////////////////////////////
    
    //PULGARCITO
    
    private boolean isPulgarcito = true;
    private boolean map_loaded = false;
    private AStar aStar;
    private boolean aStarExecuted = false;
    private ArrayList<String> instructions = new ArrayList<String>();
    private int instructionIndex = 0;
    private boolean aStarFinished = true;
    private int actual_x = 9999999;
    private int actual_y = 9999999;
   
    
    
    public AgentExplorer(AgentID aid, AgentID gps, AgentID car, String mapName) throws Exception {
        super(aid);
        GPS_ID = gps;
        Car_ID = car;
        this.mapName = mapName;
        mapExist = false;
        steps = 1;
        stepsPulgarcito = steps;
        iter = 0;
        
        this.loadMap(mapName);
        /*/Imprimir mapa
        for(int i = 0; i < m_real; i++){
            System.out.print("\n");
            for (int j = 0; j < m_real; j++)
                System.out.print(map_real.get(i*m_real+j) + " ");
        }
        */
        
        System.out.println("MAPA CARGADO / CREADO");
        //initMap(map_real);
        
        if(this.map_real.size() != 0){
            this.map_loaded = true;
            System.out.println("Map loaded");
        }
        
        if(this.map_loaded){
            this.aStar= new AStar(m_real,m_real,this.map_real);
            this.aStarFinished=false;
            System.out.println("aStar inicializado");
        }
        
        

    }
    
    public void initMap(ArrayList<Integer> mapa){
        System.out.println("INIT MAP");
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
    
    public void initMapPulgarcito(ArrayList<Integer> mapa){
        
        System.out.println("INIT MAP PULGARCITO");
        for(int i = 0; i < m*2; i+=1)
            mapa.add(WALL);
 
        for(int i = 0; i < m-4; i+=1){
           mapa.add(WALL);
           mapa.add(WALL);
           for (int j = 0; j < m-4; j+=1)
               mapa.add(-1);
           mapa.add(WALL);
           mapa.add(WALL);
        }
        
        for(int i = 0; i < m*2; i+=1)
            mapa.add(WALL);
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
                System.out.println("IDLE");
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
        
        
        
        x = objectGPS.get("x").asInt();
        y = objectGPS.get("y").asInt();
        y+=2; // el mapa es 104 pero las coordenadas solo cuentan 100;
        x+=2;
        
        if(!this.aStarExecuted && !this.aStarFinished){
            
            MapPoint start = new MapPoint(x,y);
            
            this.findObjetive();
            
            MapPoint goal = new MapPoint(this.x_objetivo,this.y_objetivo);
            
            System.out.println("Crea puntos inicio y final");
            System.out.println("Punto start: " + start + " Punto goal: " + goal);
            ArrayList<MapPoint> points = aStar.calculateAStar(start, goal);
            Collections.reverse(points);
            System.out.println("Mostrando puntos");
            for(int i = 0; i < points.size(); i++){
                System.out.println(points.get(i));
            }
            
            System.out.println("Calcula points");
            if(points == null){
                System.out.println("Puntos nulos");
            }
                System.out.println(points.get(0).x + " "+ points.get(0).y);
            instructions = aStar.convertToInstructions(points, start);
            System.out.println("Size de return de instructions: " + instructions.size());
            System.out.println("Calcula a*");
 
      //      Collections.reverse(instructions);
            System.out.println("tenemos instrucciones");
            
            for(int i=0; i < instructions.size(); i++){
                System.out.println(instructions.get(i));
            }
            this.aStarExecuted = true;
        }
      //  x = 17;
      //  y=2;
        
       // msg = "Explorer: GPS x = " +x+"\ty = "+y+"\n";
       // this.sendMessage(new AgentID(Car_ID), msg);
        
       
       // JsonObject object2 = Json.parse(msg_radar).asObject();
        
      //  JsonArray ja = object2.get("radar").asArray();
        
        array_radar.clear();
        for (int i = 0; i < 25; i+=1){
            array_radar.add(arrayRadar.get(i).asInt());
            // System.out.println(ANSI_YELLOW+"Radar: " + arrayRadar.get(i).asInt());
        }
        
        array_scanner.clear();
        for (int i = 0; i < 25; i+=1){
            array_scanner.add(arrayScanner.get(i).asFloat());
        }
    
        state = UPDATE_MAP;
        
        
    }
    
    
    /*
        Enviar información al agente del mapa.
    */
    
    //Funcion debug para imprimir matriz del mapa real en la consola
    private void DEBUG_IMPRIMIRMAPAREAL(){
        System.out.println("PASOS: " + pasos);
        for(int i = 0; i < m_real; i++){
            System.out.print("\n");
            for(int j = 0; j < n_real; j++){
                System.out.print(map_real.get(i*m_real + j) + " ");
                if(map_real.get(i*m_real + j) != -1){
                    System.out.print(" ");
                }
            }
        }
    }
   
    private void UPDATE_MAP(){
        int index = 0;
        
        
        if(map_real.size() == 0){
            if(DEBUG)
            System.out.println("ENTRA EN IF");
        for(int i = y-2; i <= y+2; i+=1)
            for(int j = x-2; j <= x+2; j+=1){
               // System.out.println(ANSI_YELLOW+"aaaaaaaaaaa ");
               // System.out.println("i:" + i + ", j" + j);
               if(DEBUG)
                System.out.println("pos mapa: " + j +" " + i +" contiene " +map.get(i*m+j));
               if(DEBUG) 
               System.out.println("radar en esa pos contiene " +array_radar.get(index));
                map.set(i*m+j, array_radar.get(index));
                index+=1;
            }
        }
        else{
            if(DEBUG)
             System.out.println("ENTRA EN ELSE: " + index );
            for(int i = y-2; i <= y+2; i+=1)
                for(int j = x-2; j <= x+2; j+=1){
                   // System.out.println(ANSI_YELLOW+"aaaaaaaaaaa ");
                   // System.out.println("i:" + i + ", j" + j);
                   // System.out.println(map_real.get(i*m_real+j));
                   // System.out.println(array_radar.get(index));
                 //  if(map_real.get(i*m_real+j))
                    if(map_real.get(i*m_real+j) ==0 && array_radar.get(index) ==1){
                        if(DEBUG)
                        System.out.println("SOBRESCRIBIENDO UN CERO CON UN UNO");
                    }
                    if(DEBUG)
                    System.out.println(array_radar.get(index));
                    
                    map_real.set(i*m_real+j, array_radar.get(index));
                    if(map_real.get(i*m_real+j) == 0){
                        if(DEBUG)
                        System.out.println("PUESTOS 0");
                    }
                    index+=1;
                }
        }
        
        /*
            //DEBUG 
            System.out.println("ESTO ES LO QUE VE EL ARRAY");
            index = 0;
            for(int i = y-2; i <= y+2; i+=1){
                System.out.print("\n");
                for(int j = x-2; j <= x+2; j+=1){
                    System.out.print(array_radar.get(index) + " ");
                    index++;
                }
            }
        */
       /*     
         System.out.println("\nESTO ES LO QUE VE EL MAPA");
                for(int i = y-2; i <= y+2; i+=1){
                    System.out.print("\n");
                    for(int j = x-2; j <= x+2; j+=1){
                        System.out.print(map_real.get(i*m_real+j) + " ");
                    }
                }
                
                System.out.print("\n");
*/
       // map.set(x*m+y, 9);
        System.out.println(ANSI_YELLOW+"Pasa el parseo ");
        /*
        
        JsonObject movement = new JsonObject();
        */
        //Mirar a que posicion moverse
        //Mirar donde pude moverse por el radar
    //    System.out.println(ANSI_YELLOW+"Info radar PULGARCITO: "+ array_radar.toString());
        
    //    DEBUG_IMPRIMIRMAPAREAL();
    /*
        if(this.array_radar.get(11) == 0 || this.array_radar.get(11) == 2){
            movement.add("command", "moveW");
            this.sendMessage(this.Car_ID, movement.toString());
            pasos++;
        }else if(this.array_radar.get(17) == 0 || this.array_radar.get(17) == 2){
            movement.add("command", "moveS");  
            this.sendMessage(this.Car_ID, movement.toString());
            pasos++;
        }
    */
        /*if(this.array_radar.get(12) != 2){
            movement.add("command", "moveSW");
            this.sendMessage(this.Car_ID, movement.toString());
            pasos++;
        }
        *//*else{
            System.out.println("--------PASOS HASTA LLEGAR A OBJETIVO : "+ pasos + " -------------");
            movement.add("signal","NO_MOVE");
            this.sendMessage(this.Car_ID, movement.toString());
        }
            */
        System.out.println("Antes del if aStarExecuted es: " + this.aStarExecuted);
        if(this.aStarExecuted)
            aStar();
        else{
            System.out.println("Entra en else de pulgarcito");
              
            pulgarcito();
        }
        
        
        state = IDLE;
        
        
    }
    
    private void aStar() {
       
        JsonObject message = new JsonObject();
        
        if(this.instructionIndex < this.instructions.size()){
            
            if(this.actual_x == this.x && this.actual_y == this.y){
                this.instructionIndex--;
                
            }
            
                message.add("command", this.instructions.get(this.instructionIndex));
                System.out.println(message.toString());
                this.sendMessage(this.Car_ID, message.toString());
            
            this.actual_x = x;
            this.actual_y=y;
            this.instructionIndex++;
            this.steps++;
            System.out.println(steps);
        }else{
            this.aStarExecuted = false;
            this.aStarFinished = true;
            System.out.println("AStar acaba en : " + x + " " + y);
           message.add("signal","NO_MOVE");
            System.out.println(message.toString());
            this.sendMessage(this.Car_ID, message.toString());
        }
        
        
        
    }
  
    
    private void findObjetive() {

        int x_min=0;
        int y_min=0;
        
        float distance;
        float min_distance = 999999;
        int a = 0;
        
        for(int i=0; i < m_real; i++){
            for(int j=0;j<m_real;j++){
                
                if(this.map_real.get(i*m_real+j) == 2){
                    a++;
                    distance = (Math.abs(x - j) + Math.abs(y - i));
                    //if(a==9){
                    if(distance < min_distance){
                        min_distance = distance;
                        x_min = j;
                        y_min = i;
                    }
                }
            }
        }
        
        x_objetivo = x_min;
        y_objetivo = y_min;
        


    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private void pulgarcito(){
        
        if(!this.mapExist){
           // this.initMapPulgarcito(this.mapPulgarcito);
           loadPulgarcito();
            this.mapExist = true;
        }
 
        updatePulgarcitoMap();
        
        String movement = selectMovement();
        
        System.out.println("step: " + steps + ", stepsPulgarcito: "+ stepsPulgarcito +", iter : " + iter);
        
        if(steps < MAX_STEPS){
            JsonObject message = new JsonObject();
            message.add("command", movement);
            this.sendMessage(this.Car_ID, message.toString());
            if(steps == MAX_STEPS-1){
                iter++;
            }
        }else if(iter < MAX_ITERS){
            try {
                System.out.println("SE VA A DORMIR ");
                sleep(6000);
                System.out.println("SE DESPIERTA ");
                MAX_STEPS = MAX_STEPS + STEPS_PER_ITER;
                JsonObject message = new JsonObject();
                message.add("command", movement);
                System.out.println(message.toString());
                this.sendMessage(this.Car_ID, message.toString());
                
            } catch (InterruptedException ex) {
               // Logger.getLogger(AgentExplorer.class.getName()).log(Level.SEVERE, null, ex);
               System.out.println("Peta en el sleep de pulgarcito");
            }
            
            }else{
                JsonObject message = new JsonObject();
                message.add("signal","NO_MOVE");
                this.sendMessage(this.Car_ID, message.toString());
            }
        
        
       
    }

    
    
    private void updatePulgarcitoMap(){
        int index = 0;
        
        for(int i = y-2; i <= y+2; i+=1)
            for(int j = x-2; j <= x+2; j+=1){
                
                if(mapPulgarcito.get(i*m+j) == -1 && (array_radar.get(index) == 0 || array_radar.get(index) == 2 )){
                    mapPulgarcito.set(i*m+j, ROAD);
                }else if(mapPulgarcito.get(i*m+j) == -1 && array_radar.get(index) == 1 ){
                    mapPulgarcito.set(i*m+j, WALL);
                }
                index+=1;
            }

        mapPulgarcito.set(y*m+x, this.stepsPulgarcito);
        steps++;
        stepsPulgarcito++;
        
    }
    
    
    private String selectMovement(){
        String movement = "";
        int box_selected = 0;
        float box_selected_scanner = 0;
        int min = 999999;
        ArrayList<Integer> box_values = new ArrayList<>();
        ArrayList<Float> scanner_near = new ArrayList<>();
        ArrayList<Integer> radar_near = new ArrayList<>();
        
        for(int i = y-1; i <= y+1; i+=1)
            for(int j = x-1; j <= x+1; j+=1){
                if(i != y || j != x)
                box_values.add(this.mapPulgarcito.get(i*m+j));
            }
        
        for(int i=6; i < array_scanner.size()-6; i++){
            if(i != 9 && i != 10 && i != 12 && i != 14 && i != 15){
                scanner_near.add(array_scanner.get(i));
                radar_near.add(array_radar.get(i));
            }
        }
        
        for(int i=0; i < box_values.size(); i++){
            if(DEBUG)
                System.out.println("Box " + i + ": pulg = "+box_values.get(i) + " scanner = " + scanner_near.get(i) + " radar = " + radar_near.get(i));
            if(box_values.get(i) < min){
                box_selected = i;
                min = box_values.get(i);
                box_selected_scanner = scanner_near.get(i);
            }
            else if(box_values.get(i) == min){
                if(scanner_near.get(i) < box_selected_scanner){
                    box_selected = i;
                    min = box_values.get(i);
                    box_selected_scanner = scanner_near.get(i);
                }
            }
            
        }
        
        switch(box_selected){
            case 0:
                movement = "moveNW";
                break;
            case 1:
                movement = "moveN";
                break;
            case 2:
                movement = "moveNE";
                break;
            case 3:
                movement = "moveW";
                break;
            case 4:
                movement = "moveE";
                break;
            case 5:
                movement = "moveSW";
                break;
            case 6:
                movement = "moveS";
                break;
            case 7:
                movement = "moveSE";
                break;
            
        }
        
        if(DEBUG)
           System.out.println("Elije Box "+ box_selected + ": "+ movement);
        return movement;
        
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private void printPulgarcito(){
        for(int i = 0; i < m; i+=1){
            System.out.print("\n");
            for(int j = 0; j < n; j+=1){
                if(mapPulgarcito.get(i*m+j) == WALL)
                    System.out.print((char)1);
                else{
                    System.out.print(mapPulgarcito.get(i*m+j));
                }
                System.out.print("  ");
            }
        }
        System.out.println();
    }
    
    
    
    private void FINISH(){
        
        msg_finish = this.receiveMessage();
        System.out.println("MENSAGE DE FINISH " + msg_finish);
        end = true;
        
        this.m_real = Json.parse(msg_finish).asObject().get("size").asInt();
        this.n_real = this.m_real;
        System.out.println(ANSI_YELLOW+"m_real : " + m_real);
        
        
        saveMap(this.mapName);
        PrintMap();
        //savePulgarcito();
        //msg = "\nEl Explorer ha finalizado su ejecución.\n";
        //this.sendMessage(new AgentID(Car_ID), msg);
    }
    
    
    private void PrintPulgarcito(){
        byte [][] a = new byte[m][m];
        for(int i = 0; i < m; i++)
            for(int j = 0; j < m; j++){
                if((this.mapPulgarcito.get(i*m+j) == 1) || ((mapPulgarcito.get(i*m+j) == -1)))
                a[i][j] = 0;
                else a[i][j] = 1;
            }
        
        byte raw[] = new byte[m * m];
        for (int i = 0; i < a.length; i++) {
            System.arraycopy(a[i], 0, raw, i*m, m);
        }

        byte levels[] = new byte[]{0, -1};
        BufferedImage image = new BufferedImage(m, m, 
                BufferedImage.TYPE_BYTE_INDEXED,
                new IndexColorModel(8, 2, levels, levels, levels));
        DataBuffer buffer = new DataBufferByte(raw, raw.length);
        SampleModel sampleModel = new ComponentSampleModel(DataBuffer.TYPE_BYTE, m, m, 1, m * 1, new int[]{0});
        Raster raster = Raster.createRaster(sampleModel, buffer, null);
        image.setData(raster);
        try {
            ImageIO.write(image, "png", new File("testpulg.png"));
        } catch (IOException ex) {
            Logger.getLogger(AgentExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
       
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
        
        byte [][] a = new byte[m_real][n_real];
        for(int i = 0; i < m_real; i++)
            for(int j = 0; j < n_real; j++){
                if((map_real.get(i*m_real+j) == 1) || ((map_real.get(i*m_real+j) == -1)))
                a[i][j] = 0;
                else a[i][j] = 1;
            }
        
        byte raw[] = new byte[m_real * n_real];
        for (int i = 0; i < a.length; i++) {
            System.arraycopy(a[i], 0, raw, i*m_real, n_real);
        }

        byte levels[] = new byte[]{0, -1};
        BufferedImage image = new BufferedImage(m_real, n_real, 
                BufferedImage.TYPE_BYTE_INDEXED,
                new IndexColorModel(8, 2, levels, levels, levels));
        DataBuffer buffer = new DataBufferByte(raw, raw.length);
        SampleModel sampleModel = new ComponentSampleModel(DataBuffer.TYPE_BYTE, m_real, n_real, 1, m_real * 1, new int[]{0});
        Raster raster = Raster.createRaster(sampleModel, buffer, null);
        image.setData(raster);
        try {
            ImageIO.write(image, "png", new File("test_"+this.mapName+".png"));
        } catch (IOException ex) {
            Logger.getLogger(AgentExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        
    }
    
    public void savePulgarcito(){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("pulg_"+this.mapName+".map"));
            bw.write(m + " " + m + " " + this.stepsPulgarcito);
            bw.newLine();
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < m; j++) {
                //    System.out.println(map_real.get(i*m_real + j));
                    bw.write(this.mapPulgarcito.get(i*m + j) + ((j == m-1) ? "" : ","));
                }
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {System.out.println("PETA AL ESCRIBIR EL .MAP");}
    }
    
    public void saveMap(String mapName){
        if(this.map_real.size() == 0){
            System.out.println(ANSI_YELLOW + "size=0");
            
            this.initMap(map_real);
        
            for(int i=0;i<this.m_real;i++){
                for(int j=0;j<this.m_real;j++){
                    int casilla = this.map.get(i*m+j);
                   // if(casilla == 0)
                    this.map_real.set(i*m_real+j, casilla);
                }
            }
        }
        
        
        System.out.println(ANSI_YELLOW + "sale bien");
        
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(mapName+".map"));
            bw.write(m_real + " " + n_real + " " +x + " "+ y);
            bw.newLine();
            for (int i = 0; i < m_real; i++) {
                for (int j = 0; j < n_real; j++) {
                //    System.out.println(map_real.get(i*m_real + j));
                    bw.write(map_real.get(i*m_real + j) + ((j == m_real-1) ? "" : ","));
                }
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {System.out.println("PETA AL ESCRIBIR EL .MAP");}
        System.out.println(ANSI_YELLOW + "MAPA ESCRITO");
    }
    
    public void loadMap(String mapName){
        Scanner sc;
        
        try {
            sc = new Scanner(new BufferedReader(new FileReader(mapName+".map")));
            String[] line = sc.nextLine().trim().split(" ");
      
            m_real = Integer.valueOf(line[0]);
            n_real = Integer.valueOf(line[1]);
            x_objetivo = Integer.valueOf(line[2]);
            y_objetivo = Integer.valueOf(line[3]);
            
          //  int [][] myArray = new int[m][n];
            while(sc.hasNextLine()) {
              for (int i=0; i<(m_real); i++) {
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
    
    public void loadPulgarcito(){
        Scanner sc;
        
        try {
            sc = new Scanner(new BufferedReader(new FileReader("pulg_"+this.mapName+".map")));
            String[] line = sc.nextLine().trim().split(" ");
      
            m = Integer.valueOf(line[0]);
            n = Integer.valueOf(line[1]);
            this.stepsPulgarcito = Integer.valueOf(line[2]);
            this.stepsPulgarcito -=1;
            System.out.println("Pulgarcito cargado: " + stepsPulgarcito );
            
          //  int [][] myArray = new int[m][n];
            while(sc.hasNextLine()) {
              for (int i=0; i<(m); i++) {
                  line = sc.nextLine().trim().split(",");
                 for (int j=0; j<line.length; j++) {
                   // myArray[i][j] = Integer.parseInt(line[j]);
                    mapPulgarcito.add( Integer.parseInt(line[j]));
                 }
              }
           }
        } catch (FileNotFoundException ex) {   // SI NO EXISTE EL ARCHIVO

            initMapPulgarcito(mapPulgarcito);
            System.out.println(ANSI_YELLOW+"ERROR");
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
