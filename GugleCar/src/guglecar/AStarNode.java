/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guglecar;

/**
 *
 * @author Ruben
 */
public class AStarNode {

    public final MapPoint point;

    public AStarNode parent;

    public double gValue; //points from start
    public double hValue; //distance from target
    public boolean isWall = false;

    private final int MOVEMENT_COST = 1;

    public AStarNode(MapPoint point) {
        this.point = point;
    }

    /**
     * Used for setting the starting node value to 0
     */
    public void setGValue(int amount) {
        this.gValue = amount;
    }

    public void calculateHValue(AStarNode destPoint) {
       
        int maxValue1 = 0;
        int maxValue2 = 0;
        maxValue1 = (Math.abs(point.x - destPoint.point.x));
        maxValue2 = (Math.abs(point.y - destPoint.point.y));
        
        if(maxValue1 > maxValue2){
            this.hValue = this.MOVEMENT_COST*maxValue1;
        }
        
        else{
            this.hValue = this.MOVEMENT_COST*maxValue2;
        }
        
        
        //version 2
        /*
        double cost_diagonal = MOVEMENT_COST*1.414;
        
        int value1 = 0;
        int value2 = 0;
        float max;
        float min;
        value1 = (Math.abs(point.x - destPoint.point.x));
        value2 = (Math.abs(point.y - destPoint.point.y));
        
        if(value1 > value2){
            max = value1;
            min = value2;
        }
        
        else{
            max = value2;
            min = value1;
        }
        
        this.hValue = cost_diagonal*min+MOVEMENT_COST*(max-min);
        */
    }

    @Override
    public String toString() {
        return "AStarNode{" + "point=" + point + ", parent=" + parent + ", gValue=" + gValue + ", hValue=" + hValue + ", isWall=" + isWall + ", MOVEMENT_COST=" + MOVEMENT_COST + '}';
    }

    public void calculateGValue(AStarNode point, boolean diagonal) {
        double multiplier = 1;
        if(diagonal)
            multiplier = 1.414;
        this.gValue = point.gValue + this.MOVEMENT_COST * multiplier;
    }

    public double getFValue() {
        return this.gValue + this.hValue;
    }
}