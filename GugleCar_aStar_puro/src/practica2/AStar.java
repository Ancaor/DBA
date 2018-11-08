package practica2;

import static practica2.Agent.ANSI_YELLOW;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;


/**
 * 
 * @author Rubén Marín Asunción
 * @author Rubén Mogica Garrido
 * @author Antonio Camarero Ortega
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
    }

    /**
     * 
     * @author Rubén Marín Asunción
     * @author Rubén Mogica Garrido
     * @author Antonio Camarero Ortega
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
        Comparator<AStarNode> comparator = new ComparatorNode();
        PriorityQueue<AStarNode> openList = new PriorityQueue<AStarNode>(comparator);
        HashSet<AStarNode> closedList = new HashSet<AStarNode>();
        boolean solFound = false;

        
        
        AStarNode currentNode =new AStarNode(inicio,null);
        currentNode.setGValue(0);
        currentNode.calculateHValue(destino);
        currentNode.calculateFValue();
                
        openList.add(currentNode);
        
        System.out.println("Ejecutando A* ...");
        
        
        while(!openList.isEmpty() && !solFound) {
            
            currentNode = openList.poll();
            closedList.add(currentNode);

            if (currentNode.point.equals(destino)) {
                solFound = true;
                System.out.println("A* Finalizado");
                return this.calculatePath(currentNode);
            }else{
                
                for (int i = 0; i < 8; i++) {   //Recorrer 8 direcciones en sentido horario empezando por el norte
                
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

                    
                   if((this.map_real.get(adjPoint.y * this.width + adjPoint.x) != 1) && (this.map_real.get(adjPoint.y * this.width + adjPoint.x) != -1) ){

                       AStarNode adjNode = new AStarNode(adjPoint,currentNode);
                       adjNode.calculateValues(destino);
                       
                       if (!closedList.contains(adjNode)) {
                            if (!openList.contains(adjNode)) {
                                openList.add(adjNode);
                            }else{
                                Iterator<AStarNode> it = openList.iterator();
                                AStarNode node = null;
                                while(it.hasNext()){
                                    AStarNode aux = it.next();
                                    if(aux.equals(adjNode)){
                                        node = aux; 
                                    }
                                }
                                    
                                if (adjNode.getGValue() < node.getGValue()) {
                                    node.parent = currentNode;
                                    node.calculateValues(destino);
                                }
                            }
                        }else{
                        /*
                           //Comentado por llenar el heap en mapas complejos
                           
                            AStarNode old = findNode(adjNode,closedList);
                            
                            if(adjNode.getGValue() < old.getGValue()){
                                old.parent = currentNode;
                                old.calculateValues(destino);
                                
                                propagarG(old,destino,closedList);
                                propagarG(old,destino,openList);
                                
                               // openList.add(old);
                            }
                        */
                            
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
            System.out.println("Tamanio real result: " + result.size() );
        
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
     * @author Antonio Camarero Ortega
     * @author Rubén Marín Asunción
     * 
     * Función que devuelve un nodo de un HashSet
     * 
     * @param source Nodo que se busca.
     * @param set HashSet donde se va a buscar el nodo.
     * 
     * @return AStarNode nodo encontrado en el hashSet
     */
    public AStarNode findNode(AStarNode source, HashSet<AStarNode> set)
{
      for (AStarNode obj : set) {
        if (obj.equals(source)) 
          return obj;
      } 
        return null;
        
}
    /**
     * @author Antonio Camarero Ortega
     * @Rubén Marín Asunción
     * 
     * Función que recorre de forma recursiva un nodo y sus hijos para el recalculo
     * del fValue en los nodos abiertos.
     * 
     * @param old Nodo del que partimos 
     * @param destino Destino del algoritmo. Se usa para calcular los values.
     * @param openList PriorityQueue de nodos en abiertos 
     */
    public void propagarG(AStarNode old, MapPoint destino,PriorityQueue<AStarNode> openList ){
        Iterator<AStarNode> it = openList.iterator();
        while(it.hasNext() == true){
            AStarNode node  = it.next();
            if(node.parent.equals(old)){
                node.calculateValues(destino);
                propagarG(node,destino,openList);
            }
        }
    }
    
    /**
     * @author Antonio Camarero Ortega
     * @Rubén Marín Asunción
     * 
     * Función que recorre de forma recursiva un nodo y sus hijos para el recalculo
     * del fValue en los nodos cerrados.
     * 
     * @param old Nodo del que partimos 
     * @param destino Destino del algoritmo. Se usa para calcular los values.
     * @param openList HashSet de nodos en cerrados 
     */
    public void propagarG(AStarNode old, MapPoint destino,HashSet<AStarNode> closedList ){
        Iterator<AStarNode> it = closedList.iterator();
        while(it.hasNext() == true){
            AStarNode node  = it.next();
            if(node.parent.equals(old)){
                node.calculateValues(destino);
                propagarG(node,destino,closedList);
            }
        }
    }

    

}
