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
    public boolean isWall = false;

    private final int MOVEMENT_COST = 10;

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
        this.hValue = (Math.abs(point.x - destPoint.point.x) + Math.abs(point.y - destPoint.point.y)) * this.MOVEMENT_COST;
    }

    @Override
    public String toString() {
        return "AStarNode{" + "point=" + point + ", parent=" + parent + ", gValue=" + gValue + ", hValue=" + hValue + ", isWall=" + isWall + ", MOVEMENT_COST=" + MOVEMENT_COST + '}';
    }

    public void calculateGValue(AStarNode point) {
        this.gValue = point.gValue + this.MOVEMENT_COST;
    }

    public int getFValue() {
        return this.gValue + this.hValue;
    }
}