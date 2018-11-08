/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guglecar;

/**
 *
 * @author Rubén Mogica Garrido
 * 
 * Clase que contiene la estructura de un nodo del algoritmo A*
 */
public class AStarNode {

    public final MapPoint point;

    public AStarNode parent;

    public int gValue; //Pasos dados desde el inicio
    public int hValue; //Distancia hasta el objetivo
    public int fValue; //Peso del nodo
    public boolean isWall = false;


    /**
     * @author Rubén Marín Asunción
     * 
     * Constructor con parámetros
     * 
     * @param point Representa el punto del mapa del nodo
     * @param p Representa al nodo padre.
     */
    public AStarNode(MapPoint point, AStarNode p) {
        this.point = point;
        this.parent = p;
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
     * Función que transforma en un String el contenido de un objeto de la clase.
     * 
     * @return String con información del objeto.
     */
    @Override
    public String toString() {
        return "AStarNode{" + "point=" + point + ", parent=" + parent.point + ", gValue=" + gValue + ", hValue=" + hValue + ", isWall=" + isWall + "}";
    }

    /**
     * @author Rubén Marín Asunción
     * 
     * Función que calcula el fValue del nodo en base al gValue y el hValue.
     */
    public void calculateFValue() {
        this.fValue = this.gValue + this.hValue;
    }
    
    
   /**
    * @author Rubén Marín Asunción
    * @author Rubén Mogica Garrido
    * 
    * Función que calcula el gValue
    */ 
    public void calculateGValue() {
        this.gValue = this.parent.getGValue() + 1;
    }
    
    /**
     * @author Rubén Marín Asunción
     * @author Rubén Mogica Garrido
     * 
     * Función que calcula el hValue
     * 
     * @param destino MapPoint que representa el punto objetivo del mapa.
     */
    public void calculateHValue(MapPoint destino) {
        int dif_x = Math.abs(point.x - destino.x);
        int dif_y = Math.abs(point.y - destino.y);
        
        if(dif_x >= dif_y)
            this.hValue = dif_x;
        else
            this.hValue = dif_y;
        
    }
    
    /**
     * @author Rubén Marín Asunción
     * 
     * Función que calcula todos los values del nodo
     * 
     * @param destino MapPoint que representa el punto objetivo del mapa.
     */
    
    public void calculateValues(MapPoint destino){
        calculateHValue(destino);
        calculateGValue();
        calculateFValue();
    }

    
    /**
     * @author Rubén Marín Asunción
     * 
     * get del fValue
     * 
     * @return Integer que representa al fValue
     */
    public int getFValue() {
        return this.fValue;
    }
    
    /**
     * @author Rubén Marín Asunción
     * 
     * get del gValue
     * 
     * @return Integer que representa al gValue
     */
    public int getGValue() {
        return this.gValue;
    }
    
    /**
     * @author Rubén Marín Asunción
     * 
     * get del hValue
     * 
     * @return Integer que representa al hValue
     */
    public int getHValue() {
        return this.hValue;
    }
    
    
    /**
     * @author Rubén Mogica Garrido
     * @author Antonio Camarero Ortega
     * 
     * Función que compara un nodo con otro y nos indica si son iguales
     * 
     * @param obj Object que es un nodo con el que se quiere comparar.
     * @return true si son iguales, false si son diferentes
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AStarNode other = (AStarNode) obj;
        if (!this.point.equals(other.point)) {
            return false;
        }
        return true;
    }
    
}