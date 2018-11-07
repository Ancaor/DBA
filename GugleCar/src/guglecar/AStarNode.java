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

    public int gValue; //points from start
    public int hValue; //distance from target
    public int fValue;
    public boolean isWall = false;


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
        this.hValue = (Math.abs(point.x - destPoint.point.x) + Math.abs(point.y - destPoint.point.y)) ;
    }

    @Override
    public String toString() {
        return "AStarNode{" + "point=" + point + ", parent=" + parent + ", gValue=" + gValue + ", hValue=" + hValue + ", isWall=" + isWall '}';
    }


    public void calculateFValue() {
         fValue = gValue + this.hValue;
    }
    
    public int getFValue(){
        return fValue;
    }
}