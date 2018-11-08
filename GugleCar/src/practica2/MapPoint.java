/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica2;

/**
 *
 * @author Rubén Mogica Garrido
 * 
 * Clase que representa un punto en el mapa.
 */
public class MapPoint {
    public int x;
    public int y;

    /**
     * @author Rubén Mogica Garrido
     * 
     * Constructor con parámetros
     * 
     * @param x Representa el valor de X en el mapa.
     * @param y Representa el valor de Y en el mapa.
     */
    MapPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @author Rubén Mogica Garrido
     * 
     * Función que almacena en un string la información del objeto
     * 
     * @return String con la información del objeto.
     */
    @Override
    public String toString() {
        return "MapPoint{" + "x=" + x + ", y=" + y + '}';
    }

    /**
     * @author Rubén Mogica Garrido
     * @author Antonio Camarero Ortega
     * 
     * Función que compara un MapPoint con otro y nos indica si son iguales
     * 
     * @param obj Object que es un MapPoint con el que se quiere comparar.
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
        final MapPoint other = (MapPoint) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }
    
    
}
