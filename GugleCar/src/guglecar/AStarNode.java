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

    public int gValue; //Pasos dados desde el inicio
    public int hValue; //Distancia hasta el objetivo
    public int fValue; //Peso del nodo
    public boolean isWall = false;


    public AStarNode(MapPoint point) {
        this.point = point;
    }

    
    public void setGValue(int amount) {
        this.gValue = amount;
    }

    
    @Override
    public String toString() {
        return "AStarNode{" + "point=" + point + ", parent=" + parent.point + ", gValue=" + gValue + ", hValue=" + hValue + ", isWall=" + isWall + ", MOVEMENT_COST=}";
    }

    public void calculateFValue(AStarNode point) {
        this.fValue = this.gValue + this.hValue;
    }
    
    public void calculateGValue(AStarNode point) {
        this.gValue = this.parent.getGValue() + 1;
    }
    
    public void calculateHValue(AStarNode destPoint) {
        int dif_x = Math.abs(point.x - destPoint.point.x);
        int dif_y = Math.abs(point.y - destPoint.point.y);
        
        if(dif_x >= dif_y)
            this.hValue = dif_x;
        else
            this.hValue = dif_y;
        
    }

    public int getFValue() {
        return this.fValue;
    }
    
    public int getGValue() {
        return this.gValue;
    }
    
    public int getHValue() {
        return this.hValue;
    }
    
}