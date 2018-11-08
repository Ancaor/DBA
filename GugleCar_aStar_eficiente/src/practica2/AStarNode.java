/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica2;

/**
 * @author Rubén Mogica Garrido
 * 
 * Clase que contiene la estructura de un nodo del algoritmo A*
 */
public class AStarNode {

    public final MapPoint point;

    public AStarNode parent;

    public double gValue;
    public double hValue;
    public boolean isWall = false;

    private final int MOVEMENT_COST = 1;

    
    /**
     * @author Rubén Mogica Garrido
     * 
     * Constructor con parámetros
     * 
     * @param point Representa el punto del mapa del nodo
     */
    public AStarNode(MapPoint point) {
        this.point = point;
    }

    /**
     * @author Rubén Mogica Garrido
     * 
     * Set del gValue
     * 
     * @param amount Representa el peso que se le va a asignar al gValue
     */
    public void setGValue(int amount) {
        this.gValue = amount;
    }

    
    /**
     * @author Rubén Mogica Garrido
     * 
     * Función que calcula el hValue
     * 
     * @param destPoint MapPoint que representa el punto objetivo del mapa.
     */
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

    }

    
    /**
     * @author Rubén Mogica Garrido
     * 
     * Función que transforma en un String el contenido de un objeto de la clase.
     * 
     * @return String con información del objeto.
     */
    @Override
    public String toString() {
        return "AStarNode{" + "point=" + point + ", parent=" + parent + ", gValue=" + gValue + ", hValue=" + hValue + ", isWall=" + isWall + ", MOVEMENT_COST=" + MOVEMENT_COST + '}';
    }

    
    /**
    * @author Rubén Mogica Garrido
    * 
    * Función que calcula el gValue
    */ 
    public void calculateGValue(AStarNode point, boolean diagonal) {
        double multiplier = 1;
        if(diagonal)
            multiplier = 1.414;
        this.gValue = point.gValue + this.MOVEMENT_COST * multiplier;
    }

    /**
     * @author Rubén Mogica Garrido
     * 
     * get del fValue
     * 
     * @return Integer que representa al fValue
     */
    public double getFValue() {
        return this.gValue + this.hValue;
    }
}