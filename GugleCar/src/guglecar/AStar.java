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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;


public class AStar {
    //De la clase explorer para hacer pruebas
    int m_real;
    int n_real;
    private ArrayList<Integer> map_real = new ArrayList<>();
    
    private final int width;
    private final int height;

    private HashMap<MapPoint, AStarNode> nodes = new HashMap<MapPoint, AStarNode>();
    private ArrayList<MapPoint> points = new ArrayList<MapPoint>();

    private MapPoint destinoDebug;
    

    public AStar(int width, int height, ArrayList<Integer> map) {
        this.width = width;
        this.height = height;
      //  this.width = 104;
    //    this.height = 104;
        map_real = map;

        for(int i=0; i < 5; i++){
            for(int j=80; j < 104; j++){
                System.out.print(map_real.get(i*104+j)+" ");
            }
            System.out.println("\n");
        }
        
        System.out.println(map_real.get(104*3 - 2)+" ");
     
/*
        for (MapPoint point : wallPositions) {
            AStarNode node = this.nodes.get(point);
            node.isWall = true;
        }
*/  
        
    }

    @SuppressWarnings("unchecked")
    public ArrayList<MapPoint> calculateAStar(MapPoint inicio, MapPoint destino) {
        Comparator<AStarNode> comparator = new ComparatorNode();
        PriorityQueue<AStarNode> openList = new PriorityQueue<AStarNode>(comparator);
        HashSet<AStarNode> closedList = new HashSet<AStarNode>();
        boolean solFound = false;

        System.out.println("EMPIEZA A*");
        AStarNode currentNode =new AStarNode(inicio,null);

        
 

        currentNode.setGValue(0);
        currentNode.calculateHValue(destino);
        currentNode.calculateFValue();
                
        openList.add(currentNode);
        
        
        while(!openList.isEmpty() && !solFound) {
            
            currentNode = openList.poll();      //pop de c++
            closedList.add(currentNode);

            if (currentNode.point.equals(destino)) {
                solFound = true;
                return this.calculatePath(currentNode);
            }else{
                
                for (int i = 0; i < 8; i++) {
                //Recorrer 8 direcciones en sentido horario empezando por el norte
                    MapPoint currentPoint = currentNode.point;
                    MapPoint adjPoint ;
                    if(i == 0){     //Norte
                        adjPoint = new MapPoint(currentPoint.x, currentPoint.y-1);
                      //  isDiagonal = false;
                    }
                    else if(i == 1){     //NorEste
                        adjPoint = new MapPoint(currentPoint.x+1, currentPoint.y-1);
                        //isDiagonal = true;
                    }

                    else if(i == 2){     //Este
                        adjPoint = new MapPoint(currentPoint.x+1, currentPoint.y);
                        //isDiagonal = false;
                    }

                    else if(i == 3){     //SurEste
                        adjPoint = new MapPoint(currentPoint.x+1, currentPoint.y+1);
                        //isDiagonal = true;
                    }

                    else if(i == 4){     //Sur
                        adjPoint = new MapPoint(currentPoint.x, currentPoint.y+1);
                        //isDiagonal = false;
                    }

                    else if(i == 5){     //SurOeste
                        adjPoint = new MapPoint(currentPoint.x-1, currentPoint.y+1);
                        //isDiagonal = true;
                    }

                    else if(i == 6){     //Oeste
                        adjPoint = new MapPoint(currentPoint.x-1, currentPoint.y);
                        //isDiagonal = false;
                    }

                    else{     //NorOeste
                        adjPoint = new MapPoint(currentPoint.x-1, currentPoint.y-1);
                        //isDiagonal = true;
                    }

                  //  if (!this.isInsideBounds(adjPoint)) {
                   //     continue;
                   // }

                   // AStarNode adjNode = this.nodes.get(adjPoint);
                   // if (adjNode.isWall) {
                   //     continue;
                   // }
                  // System.out.println(adjPoint.toString());
                  // System.out.println(this.map_real.get(adjPoint.y * this.width + adjPoint.x));
                  // System.out.println(this.map_real.get(50 * this.width + 50));
                   if((this.map_real.get(adjPoint.y * this.width + adjPoint.x) != 1) && (this.map_real.get(adjPoint.y * this.width + adjPoint.x) != -1) ){
                       System.out.println("EMPIEZA WHILE" + i);
                       AStarNode adjNode = new AStarNode(adjPoint,currentNode);
                       adjNode.calculateValues(destino);
                       
                       if (!closedList.contains(adjNode)) {
                                if (!openList.contains(adjNode)) {
                                    
                               //     System.out.println("Nodo: " + adjNode.point);
                                    openList.add(adjNode);
                             //       System.out.println("Pasa");
                                } else {
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
                 //  System.out.println("FUERA DE IF");
                   

                    
                }
                
                
            }
            
            
        }

        return null;
    }

    public ArrayList<String> convertToInstructions(ArrayList<MapPoint> points, MapPoint startingPosition){
        ArrayList<MapPoint> ordenado = new ArrayList<MapPoint>();
        for(int i=0; i<points.size(); i++){
            ordenado.add(points.get(points.size()-i-1));
        }
        
        System.out.println("Ordenado: ");
        
        for(int i = 0; i < ordenado.size(); i++){
            System.out.println(ordenado.get(i));
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
        
        System.out.println("Tamanio before bucle: " + result.size());
       
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
        
        int aux = 9 + ordenado.size()-1;
        System.out.println("Tamanio real result: " + result.size() + " Tamanio calculado: " + aux);
        
        return result;
    }
    
    private ArrayList<MapPoint> calculatePath(AStarNode destinationNode) {
        ArrayList<MapPoint> path = new ArrayList<MapPoint>();
        AStarNode node = destinationNode;
        while (node.parent != null) {
            path.add(node.point);
            node = node.parent;
        }
        return path;
    }
    
    public AStarNode findNode(AStarNode source, HashSet<AStarNode> set)
{
      for (AStarNode obj : set) {
        if (obj.equals(source)) 
          return obj;
      } 
        return null;
        
}
    
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
