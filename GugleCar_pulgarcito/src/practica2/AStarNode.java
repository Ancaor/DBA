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

    public int gValue; 
    public int hValue;
    public boolean isWall = false;

    private final int MOVEMENT_COST = 10;

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
        this.hValue = (Math.abs(point.x - destPoint.point.x) + Math.abs(point.y - destPoint.point.y)) * this.MOVEMENT_COST;
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
    public void calculateGValue(AStarNode point) {
        this.gValue = point.gValue + this.MOVEMENT_COST;
    }

    /**
     * @author Rubén Mogica Garrido
     * 
     * get del fValue
     * 
     * @return Integer que representa al fValue
     */
    public int getFValue() {
        return this.gValue + this.hValue;
    }
}