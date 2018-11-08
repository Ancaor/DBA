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
 * @author Rubén Marín Asunción
 * 
 * Clase del agente explorador que se encarga de recibir información de los demás 
 * agentes que actúan como sensores como son el radar, el gps y el scanner.
 * Guarda información sobre dos mapas. 
 * 
 * Mapa real: En este mapa se almacena la información que le llega del radar
 * ayudandose del GPS para conocer su ubicación. Con esta información el agente
 * conoce donde hay caminos, muros y donde esta el objetivo si se cruza con el.
 * Como al principio no conocemos el tamaño real del mapa usamos un mapa auxiliar
 * 'map' de tamaño 504x504 que es el tamaño máximo posible. En cuanto conocemos
 * el tamaño real del mapa hacemos uso de map_real con los tamaños reales.
 * 
 * Mapa pulgarcito: En este mapa se almacena la información del pulgarcito para
 * conocer por que zonas se ha movido el agente para evitar, en medida de lo 
 * posible, pasar por una misma zona varias veces.
 * 
 * La información de estos dos mapas se crean y se guardan en un fichero. Si 
 * existe el fichero se carga.
 */
public class AgentExplorer extends Agent {
    
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
        
    //-------------Pulgarcito------------------
    
    private ArrayList<Integer> mapPulgarcito = new ArrayList<>();
    private boolean mapExist;
    private int steps;
    private int stepsPulgarcito;
    private int iter;
    
    private int MAX_STEPS = 5000;
    
    private static final int WALL = 999999999;
    private static final int ROAD = 0;
    
    private boolean map_loaded = false;
    private AStar aStar;
    private boolean aStarExecuted = false;
    private ArrayList<String> instructions = new ArrayList<String>();
    private int instructionIndex = 0;
    private boolean aStarFinished = true;
    private int actual_x = 9999999;
    private int actual_y = 9999999;
   
    /**
     * @author Rubén Marín Asunción
     * 
     * Constructor con parámetros.
     * 
     * @param aid Representa el ID que se va a asignar al AgentExplorer.
     * @param car Representa el ID del AgentCar con el que se comunica.
     * @param mapName Representa el nombre del mapa que va a recorrer.
     * @throws Exception 
     */
    
    public AgentExplorer(AgentID aid, AgentID car, String mapName) throws Exception {
        super(aid);
        Car_ID = car;
        this.mapName = mapName;
        mapExist = false;
        steps = 1;
        stepsPulgarcito = steps;
        iter = 0;
        
        this.loadMap(mapName);
        
        if(DEBUG)
            System.out.println(ANSI_YELLOW+"MAPA CARGADO / CREADO");
        
        if(this.map_real.size() != 0){
            this.map_loaded = true;
            if(DEBUG)
                System.out.println(ANSI_YELLOW+"Map loaded");
        }
        
        if(this.map_loaded){
            this.aStar= new AStar(m_real,m_real,this.map_real);
            this.aStarFinished=false;
            if(DEBUG)
                System.out.println(ANSI_YELLOW+"aStar inicializado");
        }
        
        

    }
    
    /**
     * @author Rubén Marín Asunción
     * 
     * Función que inicializa el mapa real. Guarda el mapa en un array cuyos bordes
     * con grosor de 2 unidades son inicializados a 1 (muros) y el resto del mapa a -1 (desconocido)
     * @param mapa ArrayList de enteros que representa el mapa real.
     */
    public void initMap(ArrayList<Integer> mapa){
        
        if(DEBUG)
            System.out.println(ANSI_YELLOW+"INIT MAP");
        
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
    
    
    /**
     * @author Rubén Marín Asunción
     * 
     * Función que inicializa el mapa pulgarcito. Guarda el mapa en un array cuyos bordes
     * con grosor de 2 unidades son inicializados a WALL (valor muy alto que 
     * representa un muro) y el resto del mapa a -1 (desconocido).
     * 
     * @param mapa ArrayList de enteros que representa el mapa pulgarcito.
     */
    public void initMapPulgarcito(ArrayList<Integer> mapa){
        
        if(DEBUG)
            System.out.println(ANSI_YELLOW+"INIT MAP PULGARCITO");
        
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

    /**
     * @author Rubén Marín Asunción
     * 
     * Función que se ejecuta al despertar al agente y cambia el estado a IDLE.
     */
     private void WAKE_UP(){
    
        state = IDLE;
        System.out.println(ANSI_YELLOW+"------- EXPLORER WAKE UP -------");

    }
    
    /**
     * @author Rubén Marín Asunción
     * 
     * Función que recibe mensajes procedentes del radar y del GPS. Cambia el estado 
     * a FINISH si se finaliza la ejecución o ocurre un error en cualquiera de
     * los dos mensajes. En caso contrario cambia el estado a PROCESS_DATA.
     * Los mensajes llegan en un orden desconocido así que en este punto no sabriamos
     * distinguir de que agente es cada mensaje.
     */
    private void IDLE(){
        msg = this.receiveMessage();
        if(DEBUG){
            System.out.println(ANSI_YELLOW+"IDLE");
            System.out.println(ANSI_YELLOW+"LO QUE RECIBE EL EXPLORER 1: " + msg);
        }
        if(!msg.contains("FINISH")){
        msg2 = this.receiveMessage();
        if(DEBUG)
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
    
    
    /**
     * @author Rubén Marín Asunción
     * 
     * Función que procesa los dos mensajes recibidos para saber a quien pertenece
     * y almacena los valores en variables. Si se va a ejecutar el A* inicializa
     * los valores del origen del movimiento y el objetivo y se genera una secuencia
     * de instrucciones a seguir. Una vez procesada toda la información cambia 
     * el estado a UPDATE_MAP.
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
        
        if(DEBUG){
            System.out.println(ANSI_YELLOW+"Radar: " + msg_radar);
            System.out.println(ANSI_YELLOW+"GPS: " + msg_gps);
        }
      
        JsonObject objectGPS = Json.parse(msg_gps).asObject().get("gps").asObject();
        JsonArray arrayScanner = Json.parse(msg_gps).asObject().get("scanner").asArray();
        JsonArray arrayRadar = Json.parse(msg_radar).asObject().get("radar").asArray();
        
            
        x = objectGPS.get("x").asInt();
        y = objectGPS.get("y").asInt();
        y+=2;
        x+=2;
        
        if(!this.aStarExecuted && !this.aStarFinished){
            
            MapPoint start = new MapPoint(x,y);
            MapPoint goal = new MapPoint(this.x_objetivo,this.y_objetivo);
            
            if(DEBUG){
                System.out.println(ANSI_YELLOW+"Crea puntos inicio y final");
                System.out.println(ANSI_YELLOW+"Punto start: " + start + " Punto goal: " + goal);
            }
            
            ArrayList<MapPoint> points = aStar.calculateAStar(start, goal);
            Collections.reverse(points);
            
            if(DEBUG){
                System.out.println(ANSI_YELLOW+"Mostrando puntos");
                for(int i = 0; i < points.size(); i++){
                    System.out.println(points.get(i));
                }
                System.out.println(ANSI_YELLOW+"Calcula points");
                if(points == null){
                    System.out.println(ANSI_YELLOW+"Puntos nulos");
                }
                System.out.println(ANSI_YELLOW+points.get(0).x + " "+ points.get(0).y);
            }
            
            instructions = aStar.convertToInstructions(points, start);
            
            if(DEBUG){
                System.out.println(ANSI_YELLOW+"Size de return de instructions: " + instructions.size());
                System.out.println(ANSI_YELLOW+"Calcula a*");
                System.out.println(ANSI_YELLOW+"tenemos instrucciones");
            
                for(int i=0; i < instructions.size(); i++){
                    System.out.println(ANSI_YELLOW+instructions.get(i));
                }  
            }
            
            this.aStarExecuted = true;
        }
        
        array_radar.clear();
        for (int i = 0; i < 25; i+=1){
            array_radar.add(arrayRadar.get(i).asInt());
        }
        
        array_scanner.clear();
        for (int i = 0; i < 25; i+=1){
            array_scanner.add(arrayScanner.get(i).asFloat());
        }
    
        state = UPDATE_MAP;
        
        
    }
    
    

   /**
    * @author Rubén Marín Asunción
    * 
    * Función que actualiza el mapa con la información de los sensores. Una vez
    * actualizados los mapas evalúa segun la estrategia elegida(pulgarcito o A*)
    * el movimiento que va a realizar. Una vez realizadas las funciones del A* o
    * del pulgarcito cambia el estado a IDLE.
    */
    private void UPDATE_MAP(){
        int index = 0;
        
        if(map_real.size() == 0){
            for(int i = y-2; i <= y+2; i+=1)
                for(int j = x-2; j <= x+2; j+=1){
                   if(DEBUG){
                        System.out.println(ANSI_YELLOW+"pos mapa: " + j +" " + i +" contiene " +map.get(i*m+j));
                        System.out.println(ANSI_YELLOW+"radar en esa pos contiene " +array_radar.get(index));
                   }
                    map.set(i*m+j, array_radar.get(index));
                    index+=1;
                }
        }
        else{
            for(int i = y-2; i <= y+2; i+=1)
                for(int j = x-2; j <= x+2; j+=1){
                    if(map_real.get(i*m_real+j) ==0 && array_radar.get(index) ==1){
                        if(DEBUG)
                            System.out.println(ANSI_YELLOW+"SOBRESCRIBIENDO UN CERO CON UN UNO");
                    }
                    if(DEBUG)
                        System.out.println(ANSI_YELLOW+array_radar.get(index));
                    
                    map_real.set(i*m_real+j, array_radar.get(index));
                    if(map_real.get(i*m_real+j) == 0){
                        if(DEBUG)
                            System.out.println(ANSI_YELLOW+"PUESTOS 0");
                    }
                    index+=1;
                }
        }
        
        if(DEBUG){
            System.out.println(ANSI_YELLOW+"Pasa el parseo ");

            System.out.println(ANSI_YELLOW+"Antes del if aStarExecuted es: " + this.aStarExecuted);
        }
        if(this.aStarExecuted)
            aStar();
        else{
            if(DEBUG)
                System.out.println(ANSI_YELLOW+"Entra en else de pulgarcito");
              
            pulgarcito();
        }
        
        
        state = IDLE;
        
        
    }
    
    /**
     * @author Rubén Marín Asunción
     * 
     * Función que coge una instrucción de una lista de instrucciones generadas
     * anteriormente por el A* y le envía ese movimiento al AgentCar. Si se
     * han ejecutado todas las ordenes finaliza el movimiento A* y comienza el
     * pulgarcito.
     */
    private void aStar() {
       
        JsonObject message = new JsonObject();
        
        if(this.instructionIndex < this.instructions.size()){
            
            if(this.actual_x == this.x && this.actual_y == this.y){
                this.instructionIndex--;
                
            }
            
            message.add("command", this.instructions.get(this.instructionIndex));
            if(DEBUG)
                System.out.println(message.toString());
            
            this.sendMessage(this.Car_ID, message.toString());
            
            this.actual_x = x;
            this.actual_y=y;
            this.instructionIndex++;
            this.steps++;
        }else{
            if(DEBUG)
                System.out.println(ANSI_YELLOW+"AStar acaba en : " + x + " " + y);
            
            this.aStarExecuted = false;
            this.aStarFinished = true;
            pulgarcito();
        }
        
        
        
    }
  
    
    
    
    /**
     * @author Rubén Marín Asunción
     * 
     * Función que se encarga de ejecutar el algoritmo del pulgarcito para evaluar
     * el camino que va a seguir. El algoritmo se ejecuta hasta alcanzar un número
     * máximo de pasos.
     */
    private void pulgarcito(){
        
        if(!this.mapExist){
           loadPulgarcito();
            this.mapExist = true;
        }
 
        updatePulgarcitoMap();
        
        String movement = selectMovement();
        
        if(DEBUG)
            System.out.println(ANSI_YELLOW+"step: " + steps + ", stepsPulgarcito: "+ stepsPulgarcito +", iter : " + iter);
        
        if(steps < MAX_STEPS){
            JsonObject message = new JsonObject();
            message.add("command", movement);
            this.sendMessage(this.Car_ID, message.toString());
        }else{
            JsonObject message = new JsonObject();
            message.add("signal","NO_MOVE");
            this.sendMessage(this.Car_ID, message.toString());
        }
        
        
       
    }

    
    /**
     * @author Rubén Marín Asunción
     * 
     * Función que actualiza los valores del mapa pulgarcito
     */
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
    
    /**
     * @author Rubén Marín Asunción
     * 
     * Función que se encarga de elegir el movimiento del pulgarcito evaluando
     * los valores del mapa.
     */
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
                System.out.println(ANSI_YELLOW+"Box " + i + ": pulg = "+box_values.get(i) + " scanner = " + scanner_near.get(i) + " radar = " + radar_near.get(i));
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
           System.out.println(ANSI_YELLOW+"Elije Box "+ box_selected + ": "+ movement);
        return movement;
        
    }
    
    
    
    
    /**
     * @author Rubén Marín Asunción
     * 
     * Función que determina el final de la ejecución del agente. Al finalizar
     * guarda los dos mapas utilizados en archivos.
     */
    private void FINISH(){
        
        msg_finish = this.receiveMessage();
        
        if(DEBUG)
            System.out.println(ANSI_YELLOW+"MENSAGE DE FINISH " + msg_finish);
        end = true;
        
        this.m_real = Json.parse(msg_finish).asObject().get("size").asInt();
        this.n_real = this.m_real;
        
        if(DEBUG)
            System.out.println(ANSI_YELLOW+"m_real : " + m_real);
        
        
        saveMap(this.mapName);
        PrintMap();
        savePulgarcito();
    }
    
    
    /**
     * @author Rubén Marín Asunción
     * 
     * Función que te crea un archivo PNG usando los valores del mapa como 
     * referencia.
     */
    private void PrintMap(){
        
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
    
    
    /**
     * @author Rubén Marín Asunción
     * 
     * Función que guarda el mapa pulgarcito en un archivo.
     */
    public void savePulgarcito(){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("pulg_"+this.mapName+".map"));
            bw.write(m + " " + m + " " + this.stepsPulgarcito);
            bw.newLine();
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < m; j++) {
                    bw.write(this.mapPulgarcito.get(i*m + j) + ((j == m-1) ? "" : ","));
                }
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            System.out.println(ANSI_YELLOW+"ERROR AL ESCRIBIR EL .MAP");
        }
    }
    
    
    /**
     * @author Rubén Marín Asunción
     * 
     * Función que guarda el mapa real en un archivo
     */
    public void saveMap(String mapName){
        if(this.map_real.size() == 0){
            if(DEBUG)
                System.out.println(ANSI_YELLOW + "map_real.size=0");
            
            this.initMap(map_real);
        
            for(int i=0;i<this.m_real;i++){
                for(int j=0;j<this.m_real;j++){
                    int casilla = this.map.get(i*m+j);
                    this.map_real.set(i*m_real+j, casilla);
                }
            }
        }
        
        
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(mapName+".map"));
            bw.write(m_real + " " + n_real + " " +x + " "+ y);
            bw.newLine();
            for (int i = 0; i < m_real; i++) {
                for (int j = 0; j < n_real; j++) {
                    bw.write(map_real.get(i*m_real + j) + ((j == m_real-1) ? "" : ","));
                }
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            System.out.println(ANSI_YELLOW+"PETA AL ESCRIBIR EL .MAP");
        }
        
    }
    
    
    /**
     * @author Rubén Marín Asunción
     * 
     * Función que carga el archivo .map que representa el mapa real y almacena 
     * esa información en variables.
     * 
     * @param mapName String que representa el nombre del mapa.
     */
    public void loadMap(String mapName){
        Scanner sc;
        
        try {
            sc = new Scanner(new BufferedReader(new FileReader(mapName+".map")));
            String[] line = sc.nextLine().trim().split(" ");
      
            m_real = Integer.valueOf(line[0]);
            n_real = Integer.valueOf(line[1]);
            x_objetivo = Integer.valueOf(line[2]);
            y_objetivo = Integer.valueOf(line[3]);
            
            while(sc.hasNextLine()) {
              for (int i=0; i<(m_real); i++) {
                  line = sc.nextLine().trim().split(",");
                 for (int j=0; j<line.length; j++) {
                    map_real.add( Integer.parseInt(line[j]));
                 }
              }
           }
        } catch (FileNotFoundException ex) {

            initMap(map);
            System.out.println(ANSI_YELLOW+"No existe mapa, se utilizan valores por defecto");
        }
      
    }
    
    /**
     * @author Rubén Marín Asunción
     * 
     * Función que carga el archivo .map del pulgarcito y almacena esa información
     * en variables.
     * 
     * 
     */
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
            System.out.println(ANSI_YELLOW+"pulg_"+this.mapName+".map no existe. inicializando.");
        }
      
    }
    
    
    /**
     * @author Rubén Marín Asunción
     * 
     * Función que ejecuta la función del agente. Mientras no finalice controla
     * los diferentes estados por los que pasa el agente.
     */
    @Override
    public void execute(){
        
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
        
        System.out.println(ANSI_YELLOW+"------- EXPLORER FINISHED -------");
        
    }

    
}
