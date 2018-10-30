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


public class AStar {
    //De la clase explorer para hacer pruebas
    int m_real;
    int n_real;
    private ArrayList<Integer> map_real = new ArrayList<>();
    
    private final int width;
    private final int height;

    private HashMap<MapPoint, AStarNode> nodes = new HashMap<MapPoint, AStarNode>();
    private ArrayList<MapPoint> points = new ArrayList<MapPoint>();

    @SuppressWarnings("rawtypes")
    private final Comparator fComparator = new Comparator<AStarNode>() {
        public int compare(AStarNode a, AStarNode b) {
            return Integer.compare(a.getFValue(), b.getFValue()); //ascending to get the lowest
        }
    };

    public AStar(int width, int height, List<MapPoint> wallPositions) {
    //    this.width = width;
    //    this.height = height;
        this.width = 104;
        this.height = 104;
        loadMap("map1");
        MapPoint aux = new MapPoint(0,0);
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {

                MapPoint point = new MapPoint(x, y);
                points.add(point);
                this.nodes.put(point, new AStarNode(point));
                if(x == 100 && y == 4){
                    aux = point;
                    System.out.println("metido 0 99: " + map_real.get(x*this.width+y));
                }
                
                
               // System.out.println("Mapa real tiene: " + map_real.get(x*this.width+y));
                if(map_real.get(x*this.width+y) != 0 && map_real.get(x*this.width+y) != 2 ){
                    AStarNode node = this.nodes.get(point);
                    node.isWall = true;
                 //   System.out.println("Muro en: "+  x + ":" + y );
                }
                 
                else{
               //     System.out.println("ENTRA¿?");
                }
            }

        }
/*
        for (MapPoint point : wallPositions) {
            AStarNode node = this.nodes.get(point);
            node.isWall = true;
        }
*/  
        
    }

    @SuppressWarnings("unchecked")
    public ArrayList<MapPoint> calculateAStar(MapPoint p1, MapPoint p2) {
                    
        List<AStarNode> openList = new ArrayList<AStarNode>();
        HashSet<AStarNode> closedList = new HashSet<AStarNode>();

        AStarNode destNode = this.nodes.get(p2);

        AStarNode currentNode = this.nodes.get(p1);
 
        
        currentNode.parent = null;
        
 

        currentNode.setGValue(0);
        openList.add(currentNode);

        while(!openList.isEmpty()) {

            currentNode = openList.get(0);      //pop de c++

            if (currentNode.point.equals(destNode.point)) {
                return this.calculatePath(destNode);
            }
            openList.remove(currentNode);
            closedList.add(currentNode);

            for (int i = 0; i < 8; i++) {
                //Recorrer 8 direcciones en sentido horario empezando por el norte
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
                   //     System.out.println("Nodo: " + adjNode.point);
                        openList.add(adjNode);
                 //       System.out.println("Pasa");
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

    private ArrayList<MapPoint> calculatePath(AStarNode destinationNode) {
        ArrayList<MapPoint> path = new ArrayList<MapPoint>();
        AStarNode node = destinationNode;
        while (node.parent != null) {
            path.add(node.point);
            node = node.parent;
        }
        return path;
    }

    private boolean isInsideBounds(MapPoint point) {
        return point.x >= 0 &&
               point.x < this.width && 
               point.y >= 0 && 
               point.y < this.height;
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
              for (int i=0; i<(m_real); i++) {
                  line = sc.nextLine().trim().split(",");
                 for (int j=0; j<line.length; j++) {
                   // myArray[i][j] = Integer.parseInt(line[j]);
                    map_real.add( Integer.parseInt(line[j]));
                 }
              }
           }
        } catch (FileNotFoundException ex) {   // SI NO EXISTE EL ARCHIVO

        //    initMap(map);
            System.out.println(ANSI_YELLOW+"No existe mapa");
        }
      
    }
}
