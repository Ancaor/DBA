package guglecar;

import static guglecar.Agent.ANSI_YELLOW;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;


/**
 * @author Rubén Mogica Garrido
 * @author Rubén Marín Asunción
 * 
 * Clase que lleva a cabo el algoritmo del A*
 * 
 */
public class AStar {
    int m_real;
    int n_real;
    private ArrayList<Integer> map_real = new ArrayList<>();
    
    private final int width;
    private final int height;

    private static final boolean DEBUG = false;
    
    private HashMap<MapPoint, AStarNode> nodes = new HashMap<MapPoint, AStarNode>();
    private ArrayList<MapPoint> points = new ArrayList<MapPoint>();

    private MapPoint destinoDebug;
    
    @SuppressWarnings("rawtypes")
    private final Comparator fComparator = new Comparator<AStarNode>() {
        public int compare(AStarNode a, AStarNode b) {
            return Integer.compare(a.getFValue(), b.getFValue());
        }
    };

    /**
     * @author Rubén Mogica Garrido
     * 
     * Constructor con parametros.
     * 
     * @param width Representa el ancho del mapa.
     * @param height Representa la altura del mapa.
     * @param map ArrayList con la información del mapa.
     */
    public AStar(int width, int height, ArrayList<Integer> map) {
        this.width = width;
        this.height = height;
        map_real = map;

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {

                MapPoint point = new MapPoint(x, y);
                points.add(point);
                this.nodes.put(point, new AStarNode(point));
                
               
                if(map_real.get(y*this.width+x) != 0 && map_real.get(y*this.width+x) != 2 ){
                    AStarNode node = this.nodes.get(point);
                    node.isWall = true;
                }

            }

        }        
    }

    /**
     * 
     * @author Rubén Mogica Garrido
     * 
     * Función que ejecuta el algoritmo A* y genera un camino.
     * 
     * @param inicio MapPoint que representa el lugar donde se empieza el movimiento.
     * @param destino MapPoint que representa el lugar donde finaliza el movimiento.
     * 
     * @return ArrayList<MapPoint> con los puntos por los que pasa el camino encontrado.
     */
    @SuppressWarnings("unchecked")
    public ArrayList<MapPoint> calculateAStar(MapPoint inicio, MapPoint destino) {
                    
        List<AStarNode> openList = new ArrayList<AStarNode>();
        ArrayList<AStarNode> closedList = new ArrayList<AStarNode>();

        AStarNode destNode = this.nodes.get(destino);        
        AStarNode currentNode = this.nodes.get(inicio);
         
         
         if(DEBUG){
            System.out.println("A* dest: " +  destNode.isWall);
            System.out.println("A* ini: " +  currentNode.isWall);
         }
        
        currentNode.parent = null;
        
 

        currentNode.setGValue(0);
        openList.add(currentNode);

        
        while(!openList.isEmpty()) {

            currentNode = openList.get(0);    
            

            if (currentNode.point.equals(destNode.point)) {
                return this.calculatePath(destNode);
            }
            openList.remove(currentNode);
            closedList.add(currentNode);

            for (int i = 0; i < 8; i++) {       //Recorrer 8 direcciones en sentido horario empezando por el norte
                
                MapPoint currentPoint = currentNode.point;
                MapPoint adjPoint ;
                if(i == 0){     //Norte
                    adjPoint = new MapPoint(currentPoint.x, currentPoint.y-1);
                }
                else if(i == 1){     //NorEste
                    adjPoint = new MapPoint(currentPoint.x+1, currentPoint.y-1);
                }
                
                else if(i == 2){     //Este
                    adjPoint = new MapPoint(currentPoint.x+1, currentPoint.y);
                }
                
                else if(i == 3){     //SurEste
                    adjPoint = new MapPoint(currentPoint.x+1, currentPoint.y+1);
                }
                
                else if(i == 4){     //Sur
                    adjPoint = new MapPoint(currentPoint.x, currentPoint.y+1);
                }
                
                else if(i == 5){     //SurOeste
                    adjPoint = new MapPoint(currentPoint.x-1, currentPoint.y+1);
                }
                
                else if(i == 6){     //Oeste
                    adjPoint = new MapPoint(currentPoint.x-1, currentPoint.y);
                }
                
                else{     //NorOeste
                    adjPoint = new MapPoint(currentPoint.x-1, currentPoint.y-1);
                }
                
                if (!this.isInsideBounds(adjPoint)) {
                    continue;
                }

                AStarNode adjNode = this.nodes.get(adjPoint);
                if (adjNode.isWall) {
                    continue;
                }

                if (!closedList.contains(adjNode)) {
                    if (!openList.contains(adjNode)) {
                        adjNode.parent = currentNode;
                        adjNode.calculateGValue(currentNode);
                        adjNode.calculateHValue(destNode);
                        openList.add(adjNode);
                    } else {
                        if (adjNode.gValue < currentNode.gValue) {
                            adjNode.calculateGValue(currentNode);
                            currentNode = adjNode;
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * @author Rubén Marín Asunción
     * @author Rubén Mogica Garrido
     * 
     * Función que transforma el ArrayList de MapPoints obtenido con el A* en instrucciones
     * 
     * @param points ArrayList<MapPoint> que representa los puntos de la ruta calculada
     * @param startingPosition MapPoint que nos indica la posición inicial.
     * 
     * @return ArrayList<String> de instrucciones.
     */
    public ArrayList<String> convertToInstructions(ArrayList<MapPoint> points, MapPoint startingPosition){
        ArrayList<MapPoint> ordenado = new ArrayList<MapPoint>();
        for(int i=0; i<points.size(); i++){
            ordenado.add(points.get(points.size()-i-1));
        }
        
        if(DEBUG){
            System.out.println("Ordenado: ");

            for(int i = 0; i < ordenado.size(); i++){
                System.out.println(ordenado.get(i));
            }
        }
        
        ArrayList<String> result = new ArrayList<String>();

        
        //Norte
        if(startingPosition.x == points.get(0).x && startingPosition.y < points.get(0).y){
            result.add("moveS");
        }

        //Norteste
        else if(startingPosition.x > points.get(0).x && startingPosition.y < points.get(0).y){
            result.add("moveSW");
        }
        
        //Este
        else if(startingPosition.x > points.get(0).x && startingPosition.y == points.get(0).y){
            result.add("moveW");
        }
        
        //Sureste
        else if(startingPosition.x > points.get(0).x && startingPosition.y > points.get(0).y){
            result.add("moveNW");
        }
        
        //Sur
        else if(startingPosition.x == points.get(0).x && startingPosition.y > points.get(0).y){
            result.add("moveN");
        }
        
        //Suroeste
        else if(startingPosition.x < points.get(0).x && startingPosition.y > points.get(0).y){
            result.add("moveNE");
        }
        
        //Oeste
        else if(startingPosition.x < points.get(0).x && startingPosition.y == points.get(0).y){
            result.add("moveE");
        }
        
        //Oeste
        else {
            result.add("moveSE");
        }   
       
        for(int i = 0; i < ordenado.size()-1; i++){
            //Norte
            if(points.get(i).x == points.get(i+1).x && points.get(i).y < points.get(i+1).y){
                result.add("moveS");
            }

            //Norteste
            else if(points.get(i).x > points.get(i+1).x && points.get(i).y < points.get(i+1).y){
                result.add("moveSW");
            }

            //Este
            else if(points.get(i).x > points.get(i+1).x && points.get(i).y == points.get(i+1).y){
                result.add("moveW");
            }

            //Sureste
            else if(points.get(i).x > points.get(i+1).x && points.get(i).y > points.get(i+1).y){
                result.add("moveNW");
            }

            //Sur
            else if(points.get(i).x == points.get(i+1).x && points.get(i).y > points.get(i+1).y){
                result.add("moveN");
            }

            //Suroeste
            else if(points.get(i).x < points.get(i+1).x && points.get(i).y > points.get(i+1).y){
                result.add("moveNE");
            }

            //Oeste
            else if(points.get(i).x < points.get(i+1).x && points.get(i).y == points.get(i+1).y){
                result.add("moveE");
            }

            //Oeste
            else {
                result.add("moveSE");
            }
        }
        if(DEBUG)
            System.out.println("Tamanio real result: " + result.size());
        
        return result;
    }
    
    
    /**
     * @author Rubén Mogica Garrido
     * 
     * Función que te recorre desde el objetivo hasta el origen del camino
     * para calcular el camino.
     * 
     * @param destinationNode último nodo descubierto en el algoritmo A* que esta
     * situado en el objetivo.
     * 
     * @return ArrayList<MapPoint> con los puntos del camino a recorrer.
     */
    private ArrayList<MapPoint> calculatePath(AStarNode destinationNode) {
        ArrayList<MapPoint> path = new ArrayList<MapPoint>();
        AStarNode node = destinationNode;
        while (node.parent != null) {
            path.add(node.point);
            node = node.parent;
        }
        return path;
    }

    /**
     * @author Rubén Mogica Garrido
     * 
     * Función que nos indica si un MapPoint esta dentro de los límites del mapa
     * 
     * @param point MapPoint que vamos a comprobar
     * @return true si esta dentro, false si no.
     */
    private boolean isInsideBounds(MapPoint point) {
        return point.x >= 0 &&
               point.x < this.width && 
               point.y >= 0 && 
               point.y < this.height;
    }
    
}
