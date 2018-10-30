package guglecar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;


public class AStar {

    private final int width;
    private final int height;

    private final Map<MapPoint, AStarNode> nodes = new HashMap<MapPoint, AStarNode>();

    @SuppressWarnings("rawtypes")
    private final Comparator fComparator = new Comparator<AStarNode>() {
        public int compare(AStarNode a, AStarNode b) {
            return Integer.compare(a.getFValue(), b.getFValue()); //ascending to get the lowest
        }
    };

    public AStar(int width, int height, List<MapPoint> wallPositions) {
        this.width = width;
        this.height = height;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                MapPoint point = new MapPoint(x, y);
                this.nodes.put(point, new AStarNode(point));
            }
        }

        for (MapPoint point : wallPositions) {
            AStarNode node = this.nodes.get(point);
            node.isWall = true;
        }
    }

    @SuppressWarnings("unchecked")
    public ArrayList<MapPoint> calculateAStar(MapPoint p1, MapPoint p2) {

        Queue<AStarNode> openList = new PriorityQueue<AStarNode>();
        HashSet<AStarNode> closedList = new HashSet<AStarNode>();

        AStarNode destNode = this.nodes.get(p2);

        AStarNode currentNode = this.nodes.get(p1);
        currentNode.parent = null;
        currentNode.setGValue(0);
        openList.add(currentNode);

        while(!openList.isEmpty()) {

            currentNode = openList.poll();      //pop de c++

            if (currentNode.point.equals(destNode.point)) {
                return this.calculatePath(destNode);
            }

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
}
